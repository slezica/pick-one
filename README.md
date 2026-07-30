# Pick One

Put a finger on the screen with your friends — the app picks one at random.


## Requirements

- **JDK 17**
- **Android SDK**: platform `android-35`, build-tools `35.0.0`
- Toolchain (managed by Gradle): AGP 8.7.3, Kotlin 2.0.21, Gradle 8.9

## Development

```bash
./gradlew :app:assembleDebug        # build debug APK
./gradlew :app:testDebugUnitTest    # JVM unit tests (incl. PickWinnerTest)
./gradlew :app:lintRelease          # static analysis

adb install -r -t app/build/outputs/apk/debug/app-debug.apk
```

### Screenshot harness (multitouch)

Captures a screenshot for the (PlayStore demanded) website.

```bash
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest

adb install -r -t app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

adb shell am instrument -w -e class io.slezica.pickone.ScreenshotTest \
  io.slezica.pickone.test/androidx.test.runner.AndroidJUnitRunner

adb pull /sdcard/Android/data/io.slezica.pickone/files/three-fingers.png
```

## Release

```bash
./gradlew :app:bundleRelease # -> app/build/outputs/bundle/release/app-release.aab
```

## Website

Static site under `web/` — landing page + privacy policy, no dependencies.
