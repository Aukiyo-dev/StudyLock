# StudyLock (Android prototype)

An offline-first Android app-blocking study timer prototype. This repo is a **starter implementation**, not yet a release-ready app.

## Current prototype
- Daily start/end time settings
- Select launcher apps to block
- Accessibility service detects app/window changes
- Full-screen accessibility overlay appears over selected apps during the scheduled time
- Schedule and blocklist stored locally in SharedPreferences
- Overlay includes a Home action so users can leave the distracting app

## Build
1. Install Android Studio (current stable) and Android SDK Platform 35.
2. Open this project folder in Android Studio.
3. Let Gradle sync, then Build > Build APK(s).

## Setup on device
1. Install the debug APK.
2. In StudyLock, choose start/end time and blocked apps, then Save schedule.
3. Grant Display over other apps permission.
4. Enable StudyLock in Android Accessibility settings.

## Limitations / next work
- Prototype schedule is daily (no weekday selector or subject list yet).
- Schedule enforcement is evaluated when accessibility window/app events arrive; no alarm/boot receiver yet.
- App picker currently lists launcher apps; package visibility and manufacturer restrictions may vary.
- It is not tamper-proof and intentionally has no covert persistence or device-admin lock.
- Accessibility and overlay behavior must be tested across Android versions and OEMs; overlay display is not guaranteed on every system/protected screen.
- Add tests, accessible design, notifications, database migration, weekday rules, break support, session logs, and robust time-change/reboot handling before publishing.

## Privacy
No analytics, internet permission, cloud service, account, or backend is included in this prototype. The `QUERY_ALL_PACKAGES` permission is used to display a list of installed launchable apps for the user to choose from. Review Android/Google Play policy requirements before distributing publicly.

## License
MIT License. See `LICENSE`.
