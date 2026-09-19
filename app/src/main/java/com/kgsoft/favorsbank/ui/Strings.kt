package com.kgsoft.favorsbank.ui

/**
 * Minimal bilingual string table. The original app hard-coded Arabic with a few
 * English overrides driven by settings.language / system locale; this keeps the
 * same behavior in one place.
 */
object Strings {
    var english: Boolean = false

    private fun s(ar: String, en: String): String = if (english) en else ar

    val appName get() = s("بنك الحسنات", "Bank of Hasanat")
    val hasanatSuffix get() = s("حسنة", "hasanat")

    // login
    val loginTitle get() = s("تسجيل الدخول", "Login")
    val loginSubtitle get() = s("خطوة غير ضرورية يمكنك تجاوزها", "An unnecessary step you can skip")
    val username get() = s("اسم المستخدم", "Username")
    val password get() = s("كلمة المرور", "Password")
    val login get() = s("دخول", "Login")
    val dontShowAgain get() = s("عدم العرض مجدداً", "Don't show again")
    val skip get() = s("تخطي", "Skip")

    // home cards
    val tasks get() = s("المهام", "Tasks")
    val business get() = s("الأعمال", "Actions")
    val dailyReview get() = s("المحاسبة اليومية", "Daily review")
    val myAccount get() = s("حسابي", "My account")
    val share get() = s("مشاركة", "Share")
    val salatTimes get() = s("مواقيت الصلاة", "Prayer times")
    val azkar get() = s("أذكار الصباح والمساء", "Morning & evening azkar")
    val sibha get() = s("السبحة", "Tasbih")

    // drawer
    val settings get() = s("الإعدادات", "Settings")
    val about get() = s("حول التطبيق", "About")
    val language get() = s("اللغة", "Language")
    val exit get() = s("خروج", "Exit")

    // todo tabs
    val suggestedBusiness get() = s("أعمال مقترحة", "Suggested deeds")

    // details
    val investmentDuration get() = s("مدة الإستثمار", "Duration")
    val netProfit get() = s("صافي الأرباح (الأجر)", "Reward")
    val investmentDetails get() = s("تفاصيل الإستثمار:", "Investment details:")
    val confirmCompletion get() = s("تأكيد الإنجاز", "Confirm completion")
    val useSibha get() = s("إستخدام السبحة", "Use tasbih")
    val didYouDoIt get() = s("هل أنجزت العمل؟", "Did you complete it?")
    val confirmHonestly get() = s(
        "هل تقر و تشهد صدقاً أنك قمت بإنجاز المهمة أو العمل المذكور هنا؟",
        "Do you honestly confirm you completed this task?"
    )
    val yesOfCourse get() = s("نعم بالطبع", "Yes, of course")
    val noLater get() = s("لا، سأفعل", "No, I will")
    val blessedMsg get() = s("بارك الله فيك حصلت على إنجاز جديد: ", "Bless you, new achievement: ")

    // daily review
    val prev get() = s("السابق", "Previous")
    val next get() = s("التالي", "Next")
    val endOfQuestions get() = s("إنتهت الأسئلة!", "Questions finished!")
    val startOfQuestions get() = s("بداية الأسئلة!", "Start of questions!")

    // sibha
    val reset get() = s("تصفير", "Reset")
    val customWird get() = s("تخصيص وِرد", "Custom wird")
    val wirdText get() = s("نص الورد", "Wird text")
    val repeatCount get() = s("عدد التكرار", "Repeat count")
    val save get() = s("حفظ", "Save")
    val cancel get() = s("إلغاء", "Cancel")
    val dontLeaveEmpty get() = s("لا تترك هذا الحقل خالياً", "Don't leave this field empty")
    val numbersOnly get() = s("أدخل أرقام فقط", "Enter numbers only")

    // settings
    val notifications get() = s("التنبيهات", "Notifications")
    val nightTheme get() = s("الوضع الليلي", "Night theme")
    val zikrToasts get() = s("تنبيهات الأذكار", "Zikr toasts")
    val toastTasbih get() = s("تنبيهات التسبيح والأذكار", "Tasbih & azkar reminders")
    val toastProphet get() = s("الصلاة على النبي", "Salawat reminders")
    val toastAll get() = s("الكل", "All")
    val reportBug get() = s("الإبلاغ عن خطأ", "Report a bug")
    val enabled get() = s("مفعلة", "On")
    val disabled get() = s("غير مفعلة", "Off")

    // language screen
    val chooseLanguage get() = s("اختر لغة التطبيق", "Choose app language")
    val saveChanges get() = s("حفظ التغييرات؟", "Save changes?")
    val restartNeeded get() = s(
        "تحتاج إلى إعادة تشغيل التطبيق لرؤية التغييرات.",
        "You need to restart the app to see the changes."
    )
    val restart get() = s("إعادة التشغيل", "Restart")
    val later get() = s("لاحقاً", "Later")
    val langChanged get() = s(
        "تم تغيير اللغة بنجاح، أعد تشغيل التطبيق لترى التغييرات",
        "Language changed, restart the app to see the changes"
    )

    // my account
    val accountDetails get() = s("بيانات الحساب", "Account details")
    val level get() = s("المستوى", "Level")
    val editAccount get() = s("تعديل الحساب", "Edit account")
    val balance get() = s("الرصيد", "Balance")

    // about
    val aboutApp get() = s("نبذة عن التطبيق", "About the app")
    val policy get() = s("سياسة", "Policy")
    val donate get() = s("تبرع", "Donate")
    val understood get() = s("فهمت", "Understood")
    val close get() = s("إغلاق", "Close")
    val shareAndEarn get() = s("مشاركة و كسب الأجر", "Share and earn reward")
    val checkUpdates get() = s("التحقق من التحديثات", "Check for updates")

    // report bugs
    val sendSuggestion get() = s("إرسال إقتراح", "Send suggestion")
    val reportBugTitle get() = s("إبلاغ عن خطأ.", "Report a bug.")
    val writeHere get() = s("اكتب هنا...", "Write here...")
    val submit get() = s("إرسال", "Submit")

    // salat
    val fajr get() = s("الفجر", "Fajr")
    val shuruj get() = s("الشروق", "Sunrise")
    val dhuhr get() = s("الظهر", "Dhuhr")
    val asr get() = s("العصر", "Asr")
    val maghrib get() = s("المغرب", "Maghrib")
    val isha get() = s("العشاء", "Isha")
    val nextPrayer get() = s("الصلاة القادمة", "Next prayer")
    val remainingTime get() = s("المتبقي", "Remaining")
    val locationTitle get() = s("تحديد الموقع", "Set location")
    val locationMessage get() = s(
        "نحتاج إلى موقعك مرة واحدة فقط لحساب مواقيت الصلاة بدقة حسب مدينتك.",
        "We need your location once to calculate accurate prayer times for your city."
    )
    val useMyLocation get() = s("استخدام موقعي", "Use my location")
    val useKhartoum get() = s("الخرطوم (افتراضي)", "Khartoum (default)")

    // completed tasks log
    val completedLog get() = s("سجل المهام المنجزة", "Completed tasks log")
    val noCompletedTasks get() = s("لا توجد مهام منجزة بعد", "No completed tasks yet")
    val hasanatAdded get() = s("أُضيفت إلى رصيدك", "added to your balance")
    val blessedWithCount get() = s("بارك الله فيك! حصلت على", "Bless you! You earned")
    val mayAllahAccept get() = s("تقبل الله طاعتك", "May Allah accept your worship")

    val aboutText get() = s(
        "بنك الحسنات تطبيق إسلامي يساعدك على استثمار وقتك في الطاعات وجمع الحسنات: مهام وأعمال مقترحة بالأجر والدليل، محاسبة يومية للنفس، أذكار الصباح والمساء، سبحة إلكترونية، ومواقيت الصلاة. شارك التطبيق واجعله صدقة جارية في ميزان حسناتك.",
        "Bank of Hasanat is an Islamic app that helps you invest your time in good deeds: suggested tasks with rewards and evidence, daily self-review, morning and evening azkar, a digital tasbih, and prayer times. Share the app and make it ongoing charity."
    )
}
