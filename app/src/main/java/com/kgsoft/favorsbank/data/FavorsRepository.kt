package com.kgsoft.favorsbank.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kgsoft.favorsbank.model.GoodDeedTask
import com.kgsoft.favorsbank.model.UserProfile
import com.kgsoft.favorsbank.model.ZikrCategory
import com.kgsoft.favorsbank.model.ZikrItem
import java.util.UUID

class FavorsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("favors_bank_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_HASANAT = "key_hasanat"
        private const val KEY_AZKAR_DONE = "key_azkar_done"
        private const val KEY_TASKS_DONE = "key_tasks_done"
        private const val KEY_TASBEEH_TOTAL = "key_tasbeeh_total"
        private const val KEY_CUSTOM_TASKS = "key_custom_tasks"
        private const val KEY_COMPLETED_TASK_IDS = "key_completed_task_ids"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_VIBRATION = "key_vibration"
        private const val KEY_DAILY_REVIEW_IDS = "key_daily_review_ids"
    }

    // --- Hasanat Score ---
    fun getTotalHasanat(): Long = prefs.getLong(KEY_HASANAT, 0L)

    fun addHasanat(amount: Int) {
        val current = getTotalHasanat()
        prefs.edit().putLong(KEY_HASANAT, current + amount).apply()
    }

    // --- User Profile ---
    fun getUserProfile(): UserProfile {
        return UserProfile(
            totalHasanat = getTotalHasanat(),
            azkarCompletedCount = prefs.getInt(KEY_AZKAR_DONE, 0),
            tasksCompletedCount = prefs.getInt(KEY_TASKS_DONE, 0),
            tasbeehTotalCount = prefs.getLong(KEY_TASBEEH_TOTAL, 0L)
        )
    }

    fun incrementAzkarDone() {
        val current = prefs.getInt(KEY_AZKAR_DONE, 0)
        prefs.edit().putInt(KEY_AZKAR_DONE, current + 1).apply()
    }

    fun addTasbeehCount(count: Int) {
        val current = prefs.getLong(KEY_TASBEEH_TOTAL, 0L)
        prefs.edit().putLong(KEY_TASBEEH_TOTAL, current + count).apply()
        addHasanat(count)
    }

    // --- Good Deed Tasks ---
    fun getDefaultTasks(): List<GoodDeedTask> {
        val defaultList = listOf(
            GoodDeedTask("t1", "صلاة الفجر في وقتها", "عبادات", 100),
            GoodDeedTask("t2", "قراءة صفحتين من القرآن الكريم", "قرآن", 80),
            GoodDeedTask("t3", "الصدقة ولو بمبلغ بسيط أو ابتسامة", "أعمال خير", 50),
            GoodDeedTask("t4", "صلة الرحم (اتصال بأحد الأقارب)", "أخلاق", 70),
            GoodDeedTask("t5", "إفشاء السلام على من عرفت ومن لم تعرف", "أخلاق", 30),
            GoodDeedTask("t6", "أذكار الصباح والمساء", "أذكار", 60),
            GoodDeedTask("t7", "مساعدة أحد الوالدين أو كبار السن", "بر الوالدين", 90),
            GoodDeedTask("t8", "الصلاة على النبي 100 مرة", "أذكار", 50)
        )

        val customTasks = getCustomTasks()
        val completedIds = getCompletedTaskIds()

        val all = defaultList + customTasks
        return all.map { task ->
            task.copy(isCompleted = completedIds.contains(task.id))
        }
    }

    private fun getCustomTasks(): List<GoodDeedTask> {
        val json = prefs.getString(KEY_CUSTOM_TASKS, null) ?: return emptyList()
        val type = object : TypeToken<List<GoodDeedTask>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addCustomTask(title: String, category: String, reward: Int) {
        val currentCustom = getCustomTasks().toMutableList()
        currentCustom.add(
            GoodDeedTask(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                rewardHasanat = reward,
                isCustom = true
            )
        )
        prefs.edit().putString(KEY_CUSTOM_TASKS, gson.toJson(currentCustom)).apply()
    }

    fun toggleTaskCompleted(task: GoodDeedTask): Boolean {
        val completedSet = getCompletedTaskIds().toMutableSet()
        val newState = !task.isCompleted
        if (newState) {
            completedSet.add(task.id)
            addHasanat(task.rewardHasanat)
            val currentTasksCount = prefs.getInt(KEY_TASKS_DONE, 0)
            prefs.edit().putInt(KEY_TASKS_DONE, currentTasksCount + 1).apply()
        } else {
            completedSet.remove(task.id)
        }
        prefs.edit().putStringSet(KEY_COMPLETED_TASK_IDS, completedSet).apply()
        return newState
    }

    private fun getCompletedTaskIds(): Set<String> {
        return prefs.getStringSet(KEY_COMPLETED_TASK_IDS, emptySet()) ?: emptySet()
    }

    // --- Azkar Data ---
    fun getAzkarList(category: ZikrCategory): List<ZikrItem> {
        return when (category) {
            ZikrCategory.MORNING -> listOf(
                ZikrItem("m1", "أصْبَحْنَا وَأصْبَحَ المُلْكُ للَّهِ والحمدُ للَّهِ لا إلهَ إلاَّ اللَّهُ وَحْدَهُ لا شَرِيكَ لهُ", 1, 0, 20, ZikrCategory.MORNING),
                ZikrItem("m2", "اللَّهُمَّ بِكَ أَصْبَحْنَا وَبِكَ أَمْسَيْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ", 1, 0, 15, ZikrCategory.MORNING),
                ZikrItem("m3", "اللَّهُمَّ أَنْتَ رَبِّي لا إِلَهَ إِلا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ...", 1, 0, 30, ZikrCategory.MORNING, "سيد الاستغفار"),
                ZikrItem("m4", "آية الكرسي (اللَّهُ لا إِلهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ...)", 1, 0, 25, ZikrCategory.MORNING),
                ZikrItem("m5", "سورة الإخلاص والمعوذتين (الفلق والناس)", 3, 0, 30, ZikrCategory.MORNING),
                ZikrItem("m6", "سُبْحَانَ اللهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ", 3, 0, 30, ZikrCategory.MORNING)
            )
            ZikrCategory.EVENING -> listOf(
                ZikrItem("e1", "أَمْسَيْنَا وَأَمْسَى المُلْكُ للَّهِ والحمدُ للَّهِ لا إلهَ إلاَّ اللَّهُ وَحْدَهُ لا شَرِيكَ لهُ", 1, 0, 20, ZikrCategory.EVENING),
                ZikrItem("e2", "اللَّهُمَّ بِكَ أَمْسَيْنَا وَبِكَ أَصْبَحْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ", 1, 0, 15, ZikrCategory.EVENING),
                ZikrItem("e3", "أَعُوذُ بِكَلِمَاتِ اللهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ", 3, 0, 30, ZikrCategory.EVENING),
                ZikrItem("e4", "حَسْبِيَ اللَّهُ لا إِلَهَ إِلاَّ هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ", 7, 0, 35, ZikrCategory.EVENING),
                ZikrItem("e5", "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ", 1, 0, 15, ZikrCategory.EVENING)
            )
            ZikrCategory.DAILY -> listOf(
                ZikrItem("d1", "لا حَوْلَ وَلا قُوَّةَ إِلاَّ بِاللَّهِ", 10, 0, 50, ZikrCategory.DAILY, "كنز من كنوز الجنة"),
                ZikrItem("d2", "أَسْتَغْفِرُ اللَّهَ العَظِيمَ الَّذِي لاَ إِلَهَ إِلاَّ هُوَ الحَيَّ القَيُّومَ وَأَتُوبُ إِلَيْهِ", 10, 0, 50, ZikrCategory.DAILY),
                ZikrItem("d3", "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ", 10, 0, 50, ZikrCategory.DAILY),
                ZikrItem("d4", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ العَظِيمِ", 10, 0, 50, ZikrCategory.DAILY, "كلمتان خفيفتان على اللسان")
            )
        }
    }

    // --- Settings ---
    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "ar") ?: "ar"
    fun setLanguage(lang: String) = prefs.edit().putString(KEY_LANGUAGE, lang).apply()

    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION, true)
    fun setVibrationEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()

    fun resetAllData() {
        prefs.edit().clear().apply()
    }
}
