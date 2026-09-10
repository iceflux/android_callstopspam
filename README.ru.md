[English](README.md) | Русский

# CallStopSpam

![Android](https://img.shields.io/badge/Android-API_16+-green)
![Java](https://img.shields.io/badge/language-Java-orange)
![License](https://img.shields.io/badge/license-MIT-green)

## Описание

Лёгкое приложение для старых устройств под Android API 16 (4.1.2) и новее.

1. Сбрасывает входящие звонки с неизвестных номеров, то есть номеров, которые
не записаны в телефонной книге. Режим антиспама можно выключать по
необходимости.

2. Включает/выключает режим полёта по расписанию.

## Версии приложения

| Версия | Как ищется номер в телефонной книге | Особенность |
|---|---|---|
| fast | Поиск за один шаг - сброс происходит быстрее | Номера в телефонной книге должны иметь формат +7 123 456-78-90 |
| slow | Последовательное сравнение с каждым контактом (достаточно 11 цифр) | Сброс медленнее, но формат номеров не важен. При небольшом числе контактов скорость как у fast |

APK-файлы лежат в папке [apk-release](apk-release/) этого репозитория:

- [CallStopSpam_fast.apk](apk-release/CallStopSpam_fast.apk)
- [CallStopSpam_slow.apk](apk-release/CallStopSpam_slow.apk)

## Скриншоты

![](readme_assets/Screenshot_20240630_170156.png)

## Стек технологий

- Java
- Android SDK, minSdk 16 (без внешних библиотек)
- PhoneStateListener для отслеживания входящих звонков
- Режим полёта по расписанию через AlarmManager и TIME_TICK receiver

## Используемые разрешения

- READ_PHONE_STATE - определять входящий звонок
- READ_CONTACTS - проверять, есть ли звонящий в телефонной книге
- CALL_PHONE - сбрасывать звонок
- MODIFY_PHONE_STATE - включать/выключать режим полёта
- SET_ALARM - планировать режим полёта

## Сборка

```bash
./gradlew assembleRelease
```

## License

[MIT](LICENSE)
