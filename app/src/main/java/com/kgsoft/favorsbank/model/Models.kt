package com.kgsoft.favorsbank.model

data class ZikrItem(
    val id: String,
    val text: String,
    val countNeeded: Int,
    var currentCount: Int = 0,
    val rewardHasanat: Int = 10,
    val category: ZikrCategory,
    val source: String = ""
)

enum class ZikrCategory {
    MORNING, EVENING, DAILY
}

data class GoodDeedTask(
    val id: String,
    val title: String,
    val category: String,
    val rewardHasanat: Int = 50,
    var isCompleted: Boolean = false,
    val isCustom: Boolean = false
)

data class PrayerTime(
    val nameAr: String,
    val nameEn: String,
    val time: String,
    val isNext: Boolean = false
)

data class DailyReviewItem(
    val id: String,
    val titleAr: String,
    val points: Int,
    var isDone: Boolean = false
)

data class UserProfile(
    val name: String = "عبد الله",
    val totalHasanat: Long = 0,
    val azkarCompletedCount: Int = 0,
    val tasksCompletedCount: Int = 0,
    val tasbeehTotalCount: Long = 0
) {
    val rankTitle: String
        get() = when {
            totalHasanat >= 10000 -> "سابق بالخيرات 🌟"
            totalHasanat >= 3000 -> "مقتصد مجتهد 🌿"
            totalHasanat >= 500 -> "مؤمن ذاكر 🌱"
            else -> "مبتدئ في بنك الحسنات 🌸"
        }
}
