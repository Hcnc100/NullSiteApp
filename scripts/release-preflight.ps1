param(
    [string]$WebRepoPath,
    [string]$Java21Home,
    [ValidateRange(1,2100000000)][int]$VersionCode = 10
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
Push-Location $root
try {
    # No publication, mapping upload, credential output, or repository mutations.
    & .\gradlew.bat :app:testDebugUnitTest :app:lintRelease :app:bundleRelease "-PversionCode=$VersionCode" -x :app:uploadCrashlyticsMappingFileRelease
    if ($LASTEXITCODE -ne 0) { throw 'Android preflight failed' }
    $bundle = Join-Path $root 'app/build/outputs/bundle/release/app-release.aab'
    $verify = & (Join-Path $env:JAVA_HOME 'bin/jarsigner.exe') -verify $bundle 2>&1
    if ($LASTEXITCODE -ne 0 -or ($verify -join "`n") -notmatch 'jar verified|jar verificado') { throw 'Bundle signature verification failed' }
    Get-FileHash -Algorithm SHA256 $bundle | Select-Object Algorithm,Hash,Path | ConvertTo-Json | Set-Content (Join-Path $root 'build/release-sha256.json')
    if ($WebRepoPath) {
        if (!(Test-Path (Join-Path $WebRepoPath 'tests/firebase-rules.mjs'))) { throw 'Web rules suite missing' }
        if (!$Java21Home -or !(Test-Path (Join-Path $Java21Home 'bin/java.exe'))) { throw 'Java21Home required for Firebase CLI' }
        $rules = Join-Path $WebRepoPath 'firestore.rules'
        $storage = Join-Path $WebRepoPath 'storage.rules'
        $config = Join-Path $root 'build/phase5-firebase-preflight.json'
        @{
            firestore = @{ rules = $rules }; storage = @{ rules = $storage }
            emulators = @{
                firestore = @{ host = '127.0.0.1'; port = 18085 }
                storage = @{ host = '127.0.0.1'; port = 19195 }
                hub = @{ host = '127.0.0.1'; port = 14400 }
                logging = @{ host = '127.0.0.1'; port = 14500 }
                ui = @{ enabled = $false }; singleProjectMode = $true
            }
        } | ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 $config
        Push-Location $WebRepoPath
        $oldJava = $env:JAVA_HOME; $oldPath = $env:PATH
        try {
            & npx.cmd --no-install vitest run src/app/models/blog-draft.spec.ts src/app/models/blog-post.spec.ts src/app/admin/blog src/app/admin/legal
            if ($LASTEXITCODE -ne 0) { throw 'Web editorial tests failed' }
            $env:JAVA_HOME = $Java21Home; $env:PATH = "$Java21Home/bin;$oldPath"
            & firebase.cmd emulators:exec --config $config --project demo-nullsite-phase2 --only firestore,storage 'node tests/firebase-rules.mjs'
            if ($LASTEXITCODE -ne 0) { throw 'Firebase rules tests failed' }
        } finally { $env:JAVA_HOME = $oldJava; $env:PATH = $oldPath; Pop-Location }
    }
    Write-Host 'Local preflight passed. Verify Play versionCode/certificate and approval gates before publishing.'
} finally { Pop-Location }
