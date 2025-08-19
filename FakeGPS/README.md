# Fake GPS (Android 15 / API 35)

**PENTING:** Lingkungan ini tidak memiliki Android SDK/NDK sehingga APK tidak bisa dikompilasi otomatis di sini.
File ZIP ini berisi proyek lengkap. Untuk menghasilkan `.apk` (debug) di komputer Anda:

1. Buka folder `FakeGPS/` dengan **Android Studio Jellyfish/Koala atau lebih baru**.
2. Tunggu Gradle sync, lalu jalankan: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   - APK akan berada di: `app/build/outputs/apk/debug/app-debug.apk`.
3. Aktifkan **Developer Options > Select mock location app** dan pilih **Fake GPS**.

Alternatif via CLI:
```bash
./gradlew assembleDebug
```

Catatan hukum: Gunakan Mock Location hanya untuk pengujian. Penggunaan di aplikasi produksi bisa melanggar ToS/hukum.
