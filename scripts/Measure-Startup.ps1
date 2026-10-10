param(
    [Parameter(Mandatory = $true)][string]$Serial,
    [Parameter(Mandatory = $true)][string]$OutputPath,
    [ValidateRange(3, 50)][int]$Iterations = 7,
    [string]$Adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
)
$ErrorActionPreference = 'Stop'
$package = 'com.nullpointer.nullsiteadmin'
$component = "$package/.ui.activitys.MainActivity"
$samples = for ($i = 1; $i -le $Iterations; $i++) {
    & $Adb -s $Serial shell am force-stop $package
    if ($LASTEXITCODE -ne 0) { throw 'Could not stop app' }
    $result = (& $Adb -s $Serial shell am start -W -n $component 2>&1) -join "`n"
    if ($LASTEXITCODE -ne 0 -or $result -notmatch 'Status: ok' -or
        $result -notmatch 'LaunchState: COLD' -or $result -notmatch 'TotalTime: (\d+)') {
        throw "Invalid startup sample: $result"
    }
    [pscustomobject]@{ Iteration = $i; TotalTimeMs = [int]$Matches[1] }
    Start-Sleep -Seconds 2
}
$samples | Export-Csv -LiteralPath $OutputPath -NoTypeInformation
# Discard first sample after install; this is not a Macrobenchmark or physical-device result.
$sorted = @($samples | Select-Object -Skip 1 -ExpandProperty TotalTimeMs | Sort-Object)
$n = $sorted.Count
$median = if ($n % 2) { $sorted[[int][Math]::Floor($n / 2)] }
    else { ($sorted[$n / 2 - 1] + $sorted[$n / 2]) / 2 }
"Cold-start TotalTime median (excluding first): $median ms; samples=$n"
