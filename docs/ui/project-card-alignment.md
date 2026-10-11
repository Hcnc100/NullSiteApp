# Project card alignment fix

## Goal
Center project images and edit buttons consistently, including short descriptions.

## Root cause and fix
The Card occupies the available width, but its content Column wrapped its children.
Short descriptions produced a narrower, start-aligned Column. Add fillMaxWidth()
before padding so Alignment.CenterHorizontally uses the complete card width.
Image files and scaling are unchanged.

## Verification
- testDebugUnitTest: passed.
- assembleRelease: passed.
- compileDebugAndroidTestKotlin: passed.
- Added ProjectCardAlignmentTest for image/button center coordinates with a short description.
- Instrumented test compiled but was not executed; the only running emulator is shared and was not touched.

## Deployment
User requested push for deployment. Use one push to main and the existing production
workflow only; no manual AAB upload or second workflow trigger.
Local validation did not publish an app, although the existing release task uploads
Crashlytics mapping data.

## Next steps
Check the workflow outcome and Google Play review/publication state after the push.

## Relevant files
- app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/project/projectScreen/componets/projectItem/ProjectItem.kt
- app/src/androidTest/java/com/nullpointer/nullsiteadmin/ui/ProjectCardAlignmentTest.kt

## Key Learnings
1. Center alignment in a wrap-content Column does not center its children within a wider Card; explicitly fill the Card width.
