package com.kgsoft.favorsbank.data

/**
 * Daily self-review questions (from DailyReviewActivity).
 * Arabic is the source text; [questionEn]/[subEn] is the English version shown
 * for every non-Arabic UI language (English fallback per the v1.4.0 plan).
 */
data class ReviewQuestion(
    val question: String,
    val sub: String,
    val questionEn: String = "",
    val subEn: String = ""
)

/** Localized question text: Arabic for ar, English for every other language. */
fun ReviewQuestion.text(langCode: String): String =
    if (langCode == "ar" || questionEn.isBlank()) question else questionEn

/** Localized sub text: Arabic for ar, English for every other language. */
fun ReviewQuestion.subText(langCode: String): String =
    if (langCode == "ar" || subEn.isBlank()) sub else subEn

val REVIEW_QUESTIONS = listOf(
    ReviewQuestion(
        question = "هل أديت الصلوات الخمس في جماعة؟", sub = "",
        questionEn = "Did you perform the five daily prayers in congregation?", subEn = ""
    ),
    ReviewQuestion(
        question = "هل أديت الصلوات الخمس في خشوع؟", sub = "",
        questionEn = "Did you perform the five daily prayers with humility (khushu')?", subEn = ""
    ),
    ReviewQuestion(
        question = "هل أديت صلوات التطوع؟", sub = "«القيام  - الوتر  - الإستخارة، تحية المسجد»؟",
        questionEn = "Did you perform the voluntary prayers?",
        subEn = "Qiyam al-layl – Witr – Istikharah, Tahiyyat al-masjid?"
    ),
    ReviewQuestion(
        question = "هل صمت شيئاً من النوافل", sub = "(الاثنين و الخميس - 3 أيام من كل شهر... الخ)؟",
        questionEn = "Did you fast any voluntary fasts?",
        subEn = "(Mondays and Thursdays – 3 days each month... etc.)?"
    ),
    ReviewQuestion(
        question = "هل تلوت قدراً من القرآن بتدبر و خشوع أو حفظت شيئاً منه أو راجعت ما تحفظ؟", sub = "",
        questionEn = "Did you recite a portion of the Quran with reflection and humility, memorize some of it, or revise what you memorized?",
        subEn = ""
    ),
    ReviewQuestion(
        question = "هل رددت أذكار الصباح و المساء بتدبر و يقين؟", sub = "",
        questionEn = "Did you recite the morning and evening azkar with reflection and certainty?",
        subEn = ""
    ),
    ReviewQuestion(
        question = "هل رددت الذكر المناسب لكل عمل من أعمالك", sub = "(عند النوم و اليقظة، دخول الخلاء، الخروج من المنزل، ركوب الدابة، الطعام، لبس الثياب، دخول المسجد و الخروج منه...)",
        questionEn = "Did you recite the appropriate remembrance for each of your actions?",
        subEn = "(upon sleeping and waking, entering the bathroom, leaving the house, mounting a ride, eating, wearing clothes, entering and leaving the mosque...)"
    ),
    ReviewQuestion(
        question = "هل صليت على النبي ﷺ في يومك؟", sub = "",
        questionEn = "Did you send blessings upon the Prophet ﷺ today?", subEn = ""
    ),
    ReviewQuestion(
        question = "هل أديت ما عليك من حقوق لأصحابها؟", sub = "( حق والديك، زوجك و أبنائك، أقاربك، جيرانك، اخوانك حق عملك حق المسلمين، حق الدعوة إلى الله)",
        questionEn = "Did you fulfill the rights owed to those entitled to them?",
        subEn = "(the rights of your parents, your spouse and children, your relatives, your neighbors, your brothers, your work, the Muslims, and calling to Allah)"
    ),
    ReviewQuestion(
        question = "هل أديت زكاة مالك أو تصدقت بشيء ولو يسير؟", sub = "",
        questionEn = "Did you pay your zakat or give any charity, even a little?", subEn = ""
    ),
    ReviewQuestion(
        question = "هل صحبت أحداً لا يصلي للمسجد أو لصلاة الجمعة؟", sub = "",
        questionEn = "Did you accompany someone who doesn't pray to the mosque or to the Friday prayer?",
        subEn = ""
    ),
    ReviewQuestion(
        question = "هل نصحت مسلماً فاعنته على خير و منعته من شر؟", sub = "",
        questionEn = "Did you advise a Muslim, helping him toward good and preventing him from evil?",
        subEn = ""
    ),
    ReviewQuestion(
        question = "هل صنعت معروفاً أو قضيت حاجة لأحد من المسلمين؟", sub = "",
        questionEn = "Did you do a favor or fulfill a need for a Muslim?", subEn = ""
    ),
    ReviewQuestion(
        question = "هل تحريت الحلال الطيب في مطعمك و مشربك و ملبسك", sub = "أثناء سعيك لكسب ما قدر لك من رزق؟",
        questionEn = "Did you ensure your food, drink, and clothing were lawful and wholesome?",
        subEn = "while striving to earn what was decreed for you of provision?"
    ),
    ReviewQuestion(
        question = "هل جاهدت نفسك للتخلص من خلق مرذول أو صفة ذميمة أو عادة قبيحة", sub = "(الغضب و الحدة و الغلظة  - كثرة المزاح و الضحك... الخ)؟",
        questionEn = "Did you struggle against yourself to abandon a blameworthy trait, bad habit, or ugly custom?",
        subEn = "(anger, harshness – excessive joking and laughter... etc.)?"
    ),
    ReviewQuestion(
        question = "هل جاهدت نفسك لإكتساب خلق كريم أو صفة حميدة أو عادة طيبة أو التزمت بسُنة كنت تهملها", sub = "(الحلم، الصبر، الرحمة، الحياء، التوكل، الإخلاص، الكرم، الخوف من الله.. الخ)",
        questionEn = "Did you struggle to acquire a noble trait, good habit, or commit to a sunnah you used to neglect?",
        subEn = "(forbearance, patience, mercy, modesty, trust in Allah, sincerity, generosity, fear of Allah... etc.)"
    ),
    ReviewQuestion(
        question = "هل راقبت قلبك و حرسته و طهرته من المهلكات؟", sub = "(الكبر، الرياء، الحقد و الحسد، التعلق بالدنيا... الخ)",
        questionEn = "Did you watch over your heart, guard it, and purify it from destructive traits?",
        subEn = "(arrogance, showing off, malice and envy, attachment to worldly life... etc.)"
    ),
    ReviewQuestion(
        question = "هل أمسكت لسانك و جوارحك عن ارتكاب الكبائر و المحرمات و ما نهى الله عنه؟", sub = "(الكذب، الغيبة و النميمة، القول على الله بغير علم، غض البصر، شهوات البطن و الفرج.. الخ)",
        questionEn = "Did you restrain your tongue and limbs from major sins, prohibitions, and what Allah forbade?",
        subEn = "(lying, backbiting and gossip, speaking about Allah without knowledge, lowering the gaze, desires of the stomach and private parts... etc.)"
    ),
    ReviewQuestion(
        question = "هل تذكرت حقيقة الدنيا من الآخرة فلم يتعلق قلبك بزينتها؟", sub = "(النساء، البنين، الأموال، المناصب)..",
        questionEn = "Did you remember the reality of this world compared to the Hereafter, so your heart did not cling to its adornments?",
        subEn = "(women, children, wealth, positions)..."
    ),
    ReviewQuestion(
        question = "هل تذكرت الموت و سكرته و القبر و ظلمته و الموقف بين يدي الله", sub = "فاستعدت للقائه في يومك أو ليلتك؟  (زيارة القبور)",
        questionEn = "Did you remember death, its pangs, the grave and its darkness, and standing before Allah?",
        subEn = "and prepare to meet Him in your day or night? (visiting graves)"
    ),
    ReviewQuestion(
        question = "هل تذكرت الدعاء لنفسك ووالديك و المسلمين جميعا؟", sub = "",
        questionEn = "Did you remember to supplicate for yourself, your parents, and all Muslims?",
        subEn = ""
    ),
    ReviewQuestion(
        question = "هل خلوت لنفسك فحاسبتها عن يومها و ليلتها؟", sub = "",
        questionEn = "Did you seclude yourself and hold yourself accountable for your day and night?",
        subEn = ""
    ),
    ReviewQuestion(
        question = "هل ختمت يومك بتوبة نصوحة و استغفار خاشع و نية و عهد مع الله", sub = "على علاج التقصير و زيادة الاجتهاد؟",
        questionEn = "Did you end your day with sincere repentance, humble seeking of forgiveness, and a pledge to Allah?",
        subEn = "to remedy shortcomings and increase striving?"
    ),
)
