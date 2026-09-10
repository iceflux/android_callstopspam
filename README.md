English | [Русский](README.ru.md)

# CallStopSpam

![Android](https://img.shields.io/badge/Android-API_16+-green)
![Java](https://img.shields.io/badge/language-Java-orange)
![License](https://img.shields.io/badge/license-MIT-green)

A lightweight Android app for old devices: it automatically rejects incoming
calls from numbers that are not in the phone book, and can switch airplane
mode on a schedule. Target platform: Android 4.1.2 (API 16) and newer.

## Features

- Anti-spam mode: incoming calls from unknown numbers are rejected
  automatically; the mode can be turned off in one tap
- Airplane mode scheduler: turns airplane mode on and off by schedule
- Works on very old Android versions where modern spam apps are not available

## Two versions

| Version | How the contact lookup works | Trade-off |
|---|---|---|
| fast | Single-step lookup in the phone book | Phone numbers must be stored in the format +7 123 456-78-90 |
| slow | Sequential comparison with every contact (11 digits required) | Slower rejection, but any phone number format works |

Both APKs are available in the [apk-release](apk-release/) folder of this
repository:

- [CallStopSpam_fast.apk](apk-release/CallStopSpam_fast.apk)
- [CallStopSpam_slow.apk](apk-release/CallStopSpam_slow.apk)

## Screenshot

![Screenshot](readme_assets/Screenshot_20240630_170156.png)

## Technologies

- Java
- Android SDK, minSdk 16 (no external libraries)
- PhoneStateListener for incoming call detection
- Scheduled airplane mode via AlarmManager and TIME_TICK receiver

## Permissions used

- READ_PHONE_STATE - detect incoming calls
- READ_CONTACTS - check if the caller is in the phone book
- CALL_PHONE - reject the call
- MODIFY_PHONE_STATE - toggle airplane mode
- SET_ALARM - schedule the airplane mode

## Build

```bash
./gradlew assembleRelease
```

## License

[MIT](LICENSE)
