package com.kgsoft.favorsbank.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/** Replaces SketchwareUtil.showMessage / clipboard helpers with idiomatic Kotlin. */
fun Context.toast(msg: String, long: Boolean = false) {
    Toast.makeText(this, msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Context.copyToClipboard(label: String, text: String, toastMsg: String = "تم نسخ النص إلى الحافظة") {
    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
    toast(toastMsg)
}

fun Context.shareText(text: String, chooserTitle: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, chooserTitle))
}

/**
 * Shares the app's own APK file ("شارك التطبيق و إجعله صدقة جارية في ميزان حسناتك").
 * Uses FileProvider so it works on modern Android (the original used Uri.fromFile,
 * which throws FileUriExposedException on API 24+).
 */
fun Context.shareOwnApk() {
    try {
        val source = File(applicationInfo.publicSourceDir)
        val dest = File(cacheDir, "${packageName}.apk")
        if (!dest.exists() || dest.length() != source.length()) {
            source.copyTo(dest, overwrite = true)
        }
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", dest)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(
            Intent.createChooser(intent, "شارك التطبيق و إجعله صدقة جارية في ميزان حسناتك")
        )
    } catch (e: Exception) {
        toast("حدث خطأ ما، شارك التطبيق بالطرق الخارجية", long = true)
    }
}

fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
    } catch (e: Exception) {
        toast("تعذر فتح الرابط")
    }
}

fun Context.sendBugReportEmail(body: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("botskg650@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, body)
        }
        startActivity(intent)
    } catch (e: Exception) {
        toast("لا يوجد تطبيق بريد مثبت")
    }
}
