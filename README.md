# JARVIS AI Android starter

This is a first buildable Android starter for Android 12+.

## Included in this first APK
- Dark blue JARVIS-style screen
- Tap-to-talk speech recognition (English India and Malayalam India, depending on the phone's speech service)
- Text-to-speech replies
- Basic offline commands: time/date and opening Android Settings; YouTube launch if installed
- GitHub Actions workflow to build a debug APK

## Not included yet
- Always-on "Hey Jarvis" wake word
- Full offline speech recognition (depends on installed offline language packs)
- Online AI chat
- Calls/SMS sending or unrestricted system controls

Those require extra SDKs, API configuration, permissions and user confirmation. Do not publish API secrets in source code.

## Build with GitHub Actions on a phone
1. Create a GitHub repository named `Jarvis-AI-Android`.
2. Upload all files and folders from this ZIP to the repository root (not inside another nested folder).
3. Open the repository's Actions tab.
4. If GitHub asks, enable Actions for the repository.
5. Select `Build JARVIS Android APK` and press `Run workflow` (or push to main to trigger automatically).
6. Wait for the green successful run.
7. Open that workflow run, scroll to Artifacts, and download `JARVIS-AI-debug-APK`.
8. Extract the downloaded ZIP and install `app-debug.apk` on your Android phone.
9. Allow microphone permission when prompted.

## Notes
- This is an unsigned debug APK for personal testing. It is not a Play Store release.
- Speech recognition may use Google's/phone's speech service and may require internet unless an offline language pack is installed.
- The app does not listen for "Hey Jarvis" continuously in this first build.
