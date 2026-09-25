package com.kgsoft.favorsbank.ui

/**
 * Multilingual string table. Every string has a stable key; Arabic and English
 * are inline, the other 8 languages live in LangTables. A missing translation
 * falls back to Arabic (or English when the UI language is English), so the
 * UI never shows a blank or broken label.
 */
object Strings {
    /** Current UI language code: ar, en, fr, ur, id, fa, sw, ha, bn, tr. Set at startup. */
    var langCode: String = "ar"

    private fun s(key: String, ar: String, en: String): String {
        if (langCode != "ar" && langCode != "en") {
            LangTables.get(langCode, key)?.let { return it }
        }
        return if (langCode == "en") en else ar
    }

    val appName get() = s("appName", "بنك الحسنات", "Bank of Hasanat")
    val hasanatSuffix get() = s("hasanatSuffix", "حسنة", "hasanat")

    // login
    val loginTitle get() = s("loginTitle", "تسجيل الدخول", "Login")
    val loginSubtitle get() = s("loginSubtitle", "خطوة غير ضرورية يمكنك تجاوزها", "An unnecessary step you can skip")
    val username get() = s("username", "اسم المستخدم", "Username")
    val password get() = s("password", "كلمة المرور", "Password")
    val login get() = s("login", "دخول", "Login")
    val dontShowAgain get() = s("dontShowAgain", "عدم العرض مجدداً", "Don't show again")
    val skip get() = s("skip", "تخطي", "Skip")

    // home cards
    val tasks get() = s("tasks", "المهام", "Tasks")
    val business get() = s("business", "الأعمال", "Actions")
    val dailyReview get() = s("dailyReview", "المحاسبة اليومية", "Daily review")
    val myAccount get() = s("myAccount", "حسابي", "My account")
    val share get() = s("share", "مشاركة", "Share")
    val salatTimes get() = s("salatTimes", "مواقيت الصلاة", "Prayer times")
    val azkar get() = s("azkar", "أذكار الصباح والمساء", "Morning & evening azkar")
    val morningAzkar get() = s("morningAzkar", "أذكار الصباح", "Morning azkar")
    val eveningAzkar get() = s("eveningAzkar", "أذكار المساء", "Evening azkar")
    val sibha get() = s("sibha", "السبحة", "Tasbih")

    // drawer
    val settings get() = s("settings", "الإعدادات", "Settings")
    val about get() = s("about", "حول التطبيق", "About")
    val language get() = s("language", "اللغة", "Language")
    val exit get() = s("exit", "خروج", "Exit")

    // todo tabs
    val suggestedBusiness get() = s("suggestedBusiness", "أعمال مقترحة", "Suggested deeds")

    // details
    val investmentDuration get() = s("investmentDuration", "مدة الإستثمار", "Duration")
    val netProfit get() = s("netProfit", "صافي الأرباح (الأجر)", "Reward")
    val investmentDetails get() = s("investmentDetails", "تفاصيل الإستثمار:", "Investment details:")
    val confirmCompletion get() = s("confirmCompletion", "تأكيد الإنجاز", "Confirm completion")
    val useSibha get() = s("useSibha", "إستخدام السبحة", "Use tasbih")
    val didYouDoIt get() = s("didYouDoIt", "هل أنجزت العمل؟", "Did you complete it?")
    val confirmHonestly get() = s("confirmHonestly", 
        "هل تقر و تشهد صدقاً أنك قمت بإنجاز المهمة أو العمل المذكور هنا؟",
        "Do you honestly confirm you completed this task?"
    )
    val yesOfCourse get() = s("yesOfCourse", "نعم بالطبع", "Yes, of course")
    val noLater get() = s("noLater", "لا، سأفعل", "No, I will")
    val blessedMsg get() = s("blessedMsg", "بارك الله فيك حصلت على إنجاز جديد: ", "Bless you, new achievement: ")

    // daily review
    val prev get() = s("prev", "السابق", "Previous")
    val next get() = s("next", "التالي", "Next")
    val endOfQuestions get() = s("endOfQuestions", "إنتهت الأسئلة!", "Questions finished!")
    val startOfQuestions get() = s("startOfQuestions", "بداية الأسئلة!", "Start of questions!")

    // sibha
    val reset get() = s("reset", "تصفير", "Reset")
    val customWird get() = s("customWird", "تخصيص وِرد", "Custom wird")
    val wirdText get() = s("wirdText", "نص الورد", "Wird text")
    val repeatCount get() = s("repeatCount", "عدد التكرار", "Repeat count")
    val save get() = s("save", "حفظ", "Save")
    val cancel get() = s("cancel", "إلغاء", "Cancel")
    val dontLeaveEmpty get() = s("dontLeaveEmpty", "لا تترك هذا الحقل خالياً", "Don't leave this field empty")
    val numbersOnly get() = s("numbersOnly", "أدخل أرقام فقط", "Enter numbers only")

    // settings
    val notifications get() = s("notifications", "التنبيهات", "Notifications")
    val nightTheme get() = s("nightTheme", "الوضع الليلي", "Night theme")
    val zikrToasts get() = s("zikrToasts", "تنبيهات الأذكار", "Zikr toasts")
    val toastTasbih get() = s("toastTasbih", "تنبيهات التسبيح والأذكار", "Tasbih & azkar reminders")
    val toastProphet get() = s("toastProphet", "الصلاة على النبي", "Salawat reminders")
    val toastAll get() = s("toastAll", "الكل", "All")
    val reportBug get() = s("reportBug", "الإبلاغ عن خطأ", "Report a bug")
    val diagLogTitle get() = s("diagLogTitle", "سجل التشخيص", "Diagnostic log")
    val diagLogSubtitle get() = s(
        "diagLogSubtitle",
        "ماذا فعل التطبيق خلال آخر 24 ساعة",
        "What the app did in the last 24 hours"
    )
    val copyLog get() = s("copyLog", "نسخ", "Copy")
    val refreshLog get() = s("refreshLog", "تحديث", "Refresh")
    val clearLog get() = s("clearLog", "مسح", "Clear")
    val logCopied get() = s("logCopied", "تم نسخ السجل", "Log copied")
    val logCleared get() = s("logCleared", "تم مسح السجل", "Log cleared")
    val noLogEntries get() = s("noLogEntries", "لا توجد إدخالات بعد", "No entries yet")
    val enabled get() = s("enabled", "مفعلة", "On")
    val disabled get() = s("disabled", "غير مفعلة", "Off")

    // language screen
    val chooseLanguage get() = s("chooseLanguage", "اختر لغة التطبيق", "Choose app language")
    val saveChanges get() = s("saveChanges", "حفظ التغييرات؟", "Save changes?")
    val restartNeeded get() = s("restartNeeded", 
        "تحتاج إلى إعادة تشغيل التطبيق لرؤية التغييرات.",
        "You need to restart the app to see the changes."
    )
    val restart get() = s("restart", "إعادة التشغيل", "Restart")
    val later get() = s("later", "لاحقاً", "Later")
    val langChanged get() = s("langChanged", 
        "تم تغيير اللغة بنجاح، أعد تشغيل التطبيق لترى التغييرات",
        "Language changed, restart the app to see the changes"
    )

    // my account
    val accountDetails get() = s("accountDetails", "بيانات الحساب", "Account details")
    val level get() = s("level", "المستوى", "Level")
    val editAccount get() = s("editAccount", "تعديل الحساب", "Edit account")
    val balance get() = s("balance", "الرصيد", "Balance")

    // about
    val aboutApp get() = s("aboutApp", "نبذة عن التطبيق", "About the app")
    val policy get() = s("policy", "سياسة", "Policy")
    val donate get() = s("donate", "تبرع", "Donate")
    val understood get() = s("understood", "فهمت", "Understood")
    val translationSources get() = s("translationSources", "مصادر الترجمات", "Translation sources")
    val close get() = s("close", "إغلاق", "Close")
    val shareAndEarn get() = s("shareAndEarn", "مشاركة و كسب الأجر", "Share and earn reward")
    val checkUpdates get() = s("checkUpdates", "التحقق من التحديثات", "Check for updates")

    // report bugs
    val sendSuggestion get() = s("sendSuggestion", "إرسال إقتراح", "Send suggestion")
    val reportBugTitle get() = s("reportBugTitle", "إبلاغ عن خطأ.", "Report a bug.")
    val writeHere get() = s("writeHere", "اكتب هنا...", "Write here...")
    val submit get() = s("submit", "إرسال", "Submit")

    // salat
    val fajr get() = s("fajr", "الفجر", "Fajr")
    val shuruj get() = s("shuruj", "الشروق", "Sunrise")
    val dhuhr get() = s("dhuhr", "الظهر", "Dhuhr")
    val asr get() = s("asr", "العصر", "Asr")
    val maghrib get() = s("maghrib", "المغرب", "Maghrib")
    val isha get() = s("isha", "العشاء", "Isha")
    val nextPrayer get() = s("nextPrayer", "الصلاة القادمة", "Next prayer")
    val remainingTime get() = s("remainingTime", "المتبقي", "Remaining")
    val locationTitle get() = s("locationTitle", "تحديد الموقع", "Set location")
    val locationMessage get() = s("locationMessage", 
        "نحتاج إلى موقعك مرة واحدة فقط لحساب مواقيت الصلاة بدقة حسب مدينتك.",
        "We need your location once to calculate accurate prayer times for your city."
    )
    val useMyLocation get() = s("useMyLocation", "استخدام موقعي", "Use my location")
    val useKhartoum get() = s("useKhartoum", "الخرطوم (افتراضي)", "Khartoum (default)")
    val searchCityHint get() = s("searchCityHint", "ابحث عن مدينتك…", "Search your city…")
    val noCityResults get() = s("noCityResults", "لا توجد مدينة بهذا الاسم", "No city with this name")
    val changeCity get() = s("changeCity", "تغيير", "Change")
    val myCurrentLocation get() = s("myCurrentLocation", "موقعي الحالي", "My current location")
    val gpsTitle get() = s("gpsTitle", "تفعيل خدمة الموقع", "Enable location services")
    val gpsMessage get() = s("gpsMessage",
        "خدمة الموقع (GPS) غير مفعّلة على هاتفك. يرجى تفعيلها من الإعدادات حتى نتمكن من تحديد موقعك وحساب مواقيت الصلاة بدقة.",
        "Location services (GPS) are turned off on your phone. Please enable them in Settings so we can determine your location and calculate accurate prayer times."
    )
    val openSettings get() = s("openSettings", "فتح الإعدادات", "Open settings")
    val loadingPrayerTimes get() = s("loadingPrayerTimes", "جاري تحميل مواقيت الصلاة...", "Loading prayer times...")
    val searchingCities get() = s("searchingCities", "جاري البحث…", "Searching…")

    // prayer times settings (repair + manual times)
    val prayerSettings get() = s("prayerSettings", "إعدادات مواقيت الصلاة", "Prayer times settings")
    val repairPrayerTimes get() = s("repairPrayerTimes", "إصلاح مواقيت الصلاة", "Repair prayer times")
    val repairPrayerTimesDesc get() = s("repairPrayerTimesDesc",
        "يمسح المواقيت المحفوظة ويعيد تحميلها من الإنترنت فوراً",
        "Clears saved times and reloads them from the internet now"
    )
    val repair get() = s("repair", "إصلاح", "Repair")
    val repairing get() = s("repairing", "جاري الإصلاح…", "Repairing…")
    val repairDone get() = s("repairDone", "تم تحميل المواقيت بنجاح", "Prayer times loaded successfully")
    val repairFailed get() = s("repairFailed",
        "تعذّر التحميل — تحقق من اتصال الإنترنت وحاول مجدداً",
        "Couldn't load — check your internet connection and try again"
    )
    val manualTimes get() = s("manualTimes", "تحديد المواقيت يدوياً", "Set times manually")
    val manualTimesDesc get() = s("manualTimesDesc",
        "تُعرض هذه المواقيت بدلاً من مواقيت الإنترنت (بصيغة 24 ساعة، مثال: 04:30)",
        "These times are shown instead of internet times (24-hour format, e.g. 04:30)"
    )
    val saveManualTimes get() = s("saveManualTimes", "حفظ المواقيت", "Save times")
    val clearManualTimes get() = s("clearManualTimes", "إلغاء التحديد اليدوي", "Clear manual times")
    val manualSaved get() = s("manualSaved", "تم حفظ المواقيت اليدوية", "Manual times saved")
    val manualCleared get() = s("manualCleared", "تم إلغاء المواقيت اليدوية", "Manual times cleared")
    val manualBadge get() = s("manualBadge", "يدوية", "manual")
    val invalidTime get() = s("invalidTime", "صيغة غير صحيحة", "Invalid format")
    val changeCityTitle get() = s("changeCityTitle", "تغيير المدينة", "Change city")
    val citySaved get() = s("citySaved", "تم حفظ المدينة", "City saved")

    // completed tasks log
    val completedLog get() = s("completedLog", "سجل المهام المنجزة", "Completed tasks log")
    val noCompletedTasks get() = s("noCompletedTasks", "لا توجد مهام منجزة بعد", "No completed tasks yet")
    val hasanatAdded get() = s("hasanatAdded", "أُضيفت إلى رصيدك", "added to your balance")
    val blessedWithCount get() = s("blessedWithCount", "بارك الله فيك! حصلت على", "Bless you! You earned")
    val mayAllahAccept get() = s("mayAllahAccept", "تقبل الله طاعتك", "May Allah accept your worship")

    val aboutText get() = s("aboutText", 
        "بنك الحسنات تطبيق إسلامي يساعدك على استثمار وقتك في الطاعات وجمع الحسنات: مهام وأعمال مقترحة بالأجر والدليل، محاسبة يومية للنفس، أذكار الصباح والمساء، سبحة إلكترونية، ومواقيت الصلاة. شارك التطبيق واجعله صدقة جارية في ميزان حسناتك.",
        "Bank of Hasanat is an Islamic app that helps you invest your time in good deeds: suggested tasks with rewards and evidence, daily self-review, morning and evening azkar, a digital tasbih, and prayer times. Share the app and make it ongoing charity."
    )
}
