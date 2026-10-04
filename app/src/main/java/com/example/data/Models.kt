package com.example.data

data class UserProfile(
    val name: String = "",
    val avatarEmoji: String = "🦊",
    val xp: Int = 0,
    val streakCount: Int = 1,
    val lastActiveDate: String = "",
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val darkMode: Boolean = false,
    val isOnboarded: Boolean = false
) {
    val levelInfo: LevelInfo get() = getLevelInfo(xp)
}

data class LevelInfo(
    val levelNumber: Int,
    val name: String,
    val emoji: String,
    val minXp: Int,
    val nextLevelXp: Int?,
    val progress: Float
)

fun getLevelInfo(xp: Int): LevelInfo {
    return when {
        xp < 20 -> LevelInfo(
            levelNumber = 1,
            name = "Seed",
            emoji = "🌰",
            minXp = 0,
            nextLevelXp = 20,
            progress = (xp.toFloat() / 20f).coerceIn(0f, 1f)
        )
        xp < 60 -> LevelInfo(
            levelNumber = 2,
            name = "Sprout",
            emoji = "🌱",
            minXp = 20,
            nextLevelXp = 60,
            progress = ((xp - 20).toFloat() / 40f).coerceIn(0f, 1f)
        )
        xp < 140 -> LevelInfo(
            levelNumber = 3,
            name = "Sapling",
            emoji = "🌿",
            minXp = 60,
            nextLevelXp = 140,
            progress = ((xp - 60).toFloat() / 80f).coerceIn(0f, 1f)
        )
        xp < 300 -> LevelInfo(
            levelNumber = 4,
            name = "Bloom",
            emoji = "🌷",
            minXp = 140,
            nextLevelXp = 300,
            progress = ((xp - 140).toFloat() / 160f).coerceIn(0f, 1f)
        )
        else -> LevelInfo(
            levelNumber = 5,
            name = "Sequoia",
            emoji = "🌳",
            minXp = 300,
            nextLevelXp = null,
            progress = 1.0f
        )
    }
}

data class SubjectItem(
    val id: String,
    val name: String,
    val emoji: String
)

val AllSubjects = listOf(
    SubjectItem("math", "Math", "📐"),
    SubjectItem("science", "Science", "🔬"),
    SubjectItem("history", "History", "🏛️"),
    SubjectItem("english", "English", "📚"),
    SubjectItem("coding", "Coding", "💻"),
    SubjectItem("languages", "Languages", "🌍"),
    SubjectItem("art", "Art", "🎨"),
    SubjectItem("economics", "Economics", "💰")
)

data class LanguageItem(
    val code: String,
    val name: String,
    val flagEmoji: String
)

val AllLanguages = listOf(
    LanguageItem("en", "English", "🇬🇧"),
    LanguageItem("hi", "Hindi", "🇮🇳"),
    LanguageItem("es", "Spanish", "🇪🇸"),
    LanguageItem("fr", "French", "🇫🇷"),
    LanguageItem("ar", "Arabic", "🇸🇦"),
    LanguageItem("bn", "Bengali", "🇧🇩"),
    LanguageItem("pt", "Portuguese", "🇵🇹"),
    LanguageItem("zh", "Chinese", "🇨🇳"),
    LanguageItem("ur", "Urdu", "🇵🇰"),
    LanguageItem("ta", "Tamil", "🇮🇳"),
    LanguageItem("te", "Telugu", "🇮🇳"),
    LanguageItem("mr", "Marathi", "🇮🇳"),
    LanguageItem("sw", "Swahili", "🇰🇪"),
    LanguageItem("id", "Indonesian", "🇮🇩")
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val subject: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Flashcard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val answer: String
)

data class FlashcardSet(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val cards: List<Flashcard>,
    val createdAt: Long = System.currentTimeMillis()
)

data class QuizQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class QuizSet(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val questions: List<QuizQuestion>,
    val createdAt: Long = System.currentTimeMillis()
)

data class PaperSet(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val standard: String,
    val chapters: String,
    val totalMarks: Int,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class ActivityItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val detail: String,
    val xpEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuizPerformance(
    val quizzesCompleted: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val bestScore: Int = 0
) {
    val averageAccuracyPercent: Int
        get() = if (totalQuestionsAnswered > 0) {
            ((totalCorrect.toDouble() / totalQuestionsAnswered.toDouble()) * 100).toInt()
        } else {
            0
        }
}

val MascotEmojis = listOf("🦊", "🐼", "🐸", "🦉", "🐢", "🐰", "🐨", "🦁")
