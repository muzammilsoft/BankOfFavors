# بنك الحسنات — نسخة Kotlin + Jetpack Compose

تحويل كامل لتطبيق «بنك الحسنات» (Bank of Favors) من Java + Sketchware إلى
**Kotlin** بأسلوب **Jetpack Compose** الحديث.

## ما تم تغييره

| الأصل (Java) | النسخة الجديدة (Kotlin) |
|---|---|
| 14 Activity | شاشة واحدة `MainActivity` + تنقّل Compose |
| SharedPreferences | DataStore Preferences (نفس أسماء الملفات والمفاتيح) |
| Gson | kotlinx.serialization |
| AppCompat / Material XML | Material3 Compose |
| FlatDialog | Compose AlertDialog |
| Sketchware FileUtil / SketchwareUtil | كود Kotlin مباشر |
| Animatoo | انتقالات Compose |
| `Uri.fromFile` (معطّل في API 24+) | FileProvider |

## البناء

```bash
export ANDROID_HOME=/path/to/android-sdk
./gradlew assembleDebug
```

يتطلب JDK 17 و Android SDK (platform 34, build-tools 34.0.0).

## ملاحظات

- بيانات المهام/الوظائف/الأذكار/المحاسبة محفوظة كما هي من النسخة الأصلية.
- عند تأكيد إنجاز مهمة تُضاف الحسنات (30 للمهمة، 50 للوظيفة) إلى الرصيد —
  النسخة الأصلية كانت تكتب `bonus` فقط دون زيادة الرصيد.
- رابط تيليجرام في شاشة «حول التطبيق» كان يُبنى ولا يُفتح في الأصل؛ هنا يعمل.
