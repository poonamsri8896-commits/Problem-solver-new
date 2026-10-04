package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("problem_solver_prefs", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // User Profile
    fun loadProfile(): UserProfile {
        val name = prefs.getString("user_name", "") ?: ""
        val avatar = prefs.getString("user_avatar", "🦊") ?: "🦊"
        val xp = prefs.getInt("user_xp", 0)
        val streak = prefs.getInt("user_streak", 1)
        val lastDate = prefs.getString("user_last_date", "") ?: ""
        val sound = prefs.getBoolean("user_sound", true)
        val music = prefs.getBoolean("user_music", true)
        val dark = prefs.getBoolean("user_dark", false)
        val onboarded = prefs.getBoolean("user_onboarded", false)

        return UserProfile(
            name = name,
            avatarEmoji = avatar,
            xp = xp,
            streakCount = streak,
            lastActiveDate = lastDate,
            soundEnabled = sound,
            musicEnabled = music,
            darkMode = dark,
            isOnboarded = onboarded
        )
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString("user_name", profile.name)
            .putString("user_avatar", profile.avatarEmoji)
            .putInt("user_xp", profile.xp)
            .putInt("user_streak", profile.streakCount)
            .putString("user_last_date", profile.lastActiveDate)
            .putBoolean("user_sound", profile.soundEnabled)
            .putBoolean("user_music", profile.musicEnabled)
            .putBoolean("user_dark", profile.darkMode)
            .putBoolean("user_onboarded", profile.isOnboarded)
            .apply()
    }

    fun checkAndRecordDailyStreak(current: UserProfile): UserProfile {
        val todayStr = dateFormat.format(Date())
        if (current.lastActiveDate == todayStr) {
            return current
        }

        var newStreak = current.streakCount
        if (current.lastActiveDate.isNotEmpty()) {
            try {
                val lastDate = dateFormat.parse(current.lastActiveDate)
                val todayDate = dateFormat.parse(todayStr)
                if (lastDate != null && todayDate != null) {
                    val diffDays = ((todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    newStreak = when (diffDays) {
                        1 -> current.streakCount + 1
                        else -> 1
                    }
                }
            } catch (_: Exception) {
                newStreak = 1
            }
        } else {
            newStreak = 1
        }

        val updated = current.copy(
            streakCount = newStreak,
            lastActiveDate = todayStr
        )
        saveProfile(updated)
        return updated
    }

    // Returns Pair(updatedProfile, didLevelUp: Boolean)
    fun addXP(amount: Int): Pair<UserProfile, Boolean> {
        val current = loadProfile()
        val oldLevel = getLevelInfo(current.xp).levelNumber
        val newXp = current.xp + amount
        val newLevel = getLevelInfo(newXp).levelNumber
        val updated = current.copy(xp = newXp)
        saveProfile(updated)
        val didLevelUp = newLevel > oldLevel
        return Pair(updated, didLevelUp)
    }

    // Flashcard Sets
    fun loadFlashcardSets(): List<FlashcardSet> {
        val raw = prefs.getString("flashcard_sets", "[]") ?: "[]"
        val list = mutableListOf<FlashcardSet>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val cardsArr = obj.getJSONArray("cards")
                val cards = mutableListOf<Flashcard>()
                for (j in 0 until cardsArr.length()) {
                    val cObj = cardsArr.getJSONObject(j)
                    cards.add(
                        Flashcard(
                            id = cObj.optString("id", java.util.UUID.randomUUID().toString()),
                            question = cObj.optString("question", ""),
                            answer = cObj.optString("answer", "")
                        )
                    )
                }
                list.add(
                    FlashcardSet(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.optString("title", "Untitled Flashcards"),
                        subject = obj.optString("subject", "math"),
                        cards = cards,
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveFlashcardSets(sets: List<FlashcardSet>) {
        val arr = JSONArray()
        for (set in sets) {
            val sObj = JSONObject().apply {
                put("id", set.id)
                put("title", set.title)
                put("subject", set.subject)
                put("createdAt", set.createdAt)
                val cardsArr = JSONArray()
                for (c in set.cards) {
                    val cObj = JSONObject().apply {
                        put("id", c.id)
                        put("question", c.question)
                        put("answer", c.answer)
                    }
                    cardsArr.put(cObj)
                }
                put("cards", cardsArr)
            }
            arr.put(sObj)
        }
        prefs.edit().putString("flashcard_sets", arr.toString()).apply()
    }

    // Quiz Sets
    fun loadQuizSets(): List<QuizSet> {
        val raw = prefs.getString("quiz_sets", "[]") ?: "[]"
        val list = mutableListOf<QuizSet>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val qArr = obj.getJSONArray("questions")
                val questions = mutableListOf<QuizQuestion>()
                for (j in 0 until qArr.length()) {
                    val qObj = qArr.getJSONObject(j)
                    val optArr = qObj.getJSONArray("options")
                    val opts = mutableListOf<String>()
                    for (k in 0 until optArr.length()) {
                        opts.add(optArr.getString(k))
                    }
                    questions.add(
                        QuizQuestion(
                            id = qObj.optString("id", java.util.UUID.randomUUID().toString()),
                            question = qObj.optString("question", ""),
                            options = opts,
                            correctIndex = qObj.optInt("correctIndex", 0)
                        )
                    )
                }
                list.add(
                    QuizSet(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.optString("title", "Untitled Quiz"),
                        subject = obj.optString("subject", "math"),
                        questions = questions,
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveQuizSets(sets: List<QuizSet>) {
        val arr = JSONArray()
        for (set in sets) {
            val sObj = JSONObject().apply {
                put("id", set.id)
                put("title", set.title)
                put("subject", set.subject)
                put("createdAt", set.createdAt)
                val qArr = JSONArray()
                for (q in set.questions) {
                    val qObj = JSONObject().apply {
                        put("id", q.id)
                        put("question", q.question)
                        val optArr = JSONArray()
                        for (o in q.options) {
                            optArr.put(o)
                        }
                        put("options", optArr)
                        put("correctIndex", q.correctIndex)
                    }
                    qArr.put(qObj)
                }
                put("questions", qArr)
            }
            arr.put(sObj)
        }
        prefs.edit().putString("quiz_sets", arr.toString()).apply()
    }

    // Paper Sets
    fun loadPaperSets(): List<PaperSet> {
        val raw = prefs.getString("paper_sets", "[]") ?: "[]"
        val list = mutableListOf<PaperSet>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    PaperSet(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.optString("title", "Exam Paper"),
                        subject = obj.optString("subject", "math"),
                        standard = obj.optString("standard", "Grade 10"),
                        chapters = obj.optString("chapters", ""),
                        totalMarks = obj.optInt("totalMarks", 50),
                        content = obj.optString("content", ""),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun savePaperSets(sets: List<PaperSet>) {
        val arr = JSONArray()
        for (set in sets) {
            val sObj = JSONObject().apply {
                put("id", set.id)
                put("title", set.title)
                put("subject", set.subject)
                put("standard", set.standard)
                put("chapters", set.chapters)
                put("totalMarks", set.totalMarks)
                put("content", set.content)
                put("createdAt", set.createdAt)
            }
            arr.put(sObj)
        }
        prefs.edit().putString("paper_sets", arr.toString()).apply()
    }

    // Chat History by Subject
    fun loadChatMessages(subject: String): List<ChatMessage> {
        val raw = prefs.getString("chat_${subject}", "[]") ?: "[]"
        val list = mutableListOf<ChatMessage>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ChatMessage(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        subject = subject,
                        isUser = obj.optBoolean("isUser", false),
                        text = obj.optString("text", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveChatMessages(subject: String, messages: List<ChatMessage>) {
        val arr = JSONArray()
        for (m in messages) {
            val mObj = JSONObject().apply {
                put("id", m.id)
                put("isUser", m.isUser)
                put("text", m.text)
                put("timestamp", m.timestamp)
            }
            arr.put(mObj)
        }
        prefs.edit().putString("chat_${subject}", arr.toString()).apply()
    }

    // Activity Log
    fun loadActivityHistory(): List<ActivityItem> {
        val raw = prefs.getString("activity_history", "[]") ?: "[]"
        val list = mutableListOf<ActivityItem>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ActivityItem(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.optString("title", ""),
                        detail = obj.optString("detail", ""),
                        xpEarned = obj.optInt("xpEarned", 0),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addActivity(title: String, detail: String, xpEarned: Int) {
        val current = loadActivityHistory().toMutableList()
        current.add(0, ActivityItem(title = title, detail = detail, xpEarned = xpEarned))
        val trimmed = if (current.size > 50) current.take(50) else current

        val arr = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("detail", item.detail)
                put("xpEarned", item.xpEarned)
                put("timestamp", item.timestamp)
            }
            arr.put(obj)
        }
        prefs.edit().putString("activity_history", arr.toString()).apply()
    }

    // Quiz Performance
    fun loadQuizPerformance(): QuizPerformance {
        return QuizPerformance(
            quizzesCompleted = prefs.getInt("perf_quizzes_completed", 0),
            totalQuestionsAnswered = prefs.getInt("perf_total_questions", 0),
            totalCorrect = prefs.getInt("perf_total_correct", 0),
            bestScore = prefs.getInt("perf_best_score", 0)
        )
    }

    fun recordQuizResult(correctCount: Int, totalCount: Int) {
        val current = loadQuizPerformance()
        val newCompleted = current.quizzesCompleted + 1
        val newTotal = current.totalQuestionsAnswered + totalCount
        val newCorrect = current.totalCorrect + correctCount
        val scorePercent = if (totalCount > 0) ((correctCount.toDouble() / totalCount.toDouble()) * 100).toInt() else 0
        val newBest = maxOf(current.bestScore, scorePercent)

        prefs.edit()
            .putInt("perf_quizzes_completed", newCompleted)
            .putInt("perf_total_questions", newTotal)
            .putInt("perf_total_correct", newCorrect)
            .putInt("perf_best_score", newBest)
            .apply()
    }
}
