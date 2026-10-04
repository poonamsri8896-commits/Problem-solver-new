package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.RelaxingMusicPlayer
import com.example.audio.SoundPlayer
import com.example.data.ActivityItem
import com.example.data.DataRepository
import com.example.data.FlashcardSet
import com.example.data.PaperSet
import com.example.data.QuizPerformance
import com.example.data.QuizSet
import com.example.data.UserProfile
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.NavDestination
import com.example.ui.components.TopNavBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AskScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.theme.ProblemSolverTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = DataRepository(applicationContext)

        setContent {
            ProblemSolverApp(repository)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::repository.isInitialized && repository.loadProfile().musicEnabled) {
            RelaxingMusicPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        RelaxingMusicPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        RelaxingMusicPlayer.stop()
    }
}

@Composable
fun ProblemSolverApp(repository: DataRepository) {
    var profile by remember { mutableStateOf(repository.loadProfile()) }
    var currentNav by remember { mutableStateOf(NavDestination.HOME) }
    var preselectedSubjectId by remember { mutableStateOf<String?>(null) }

    var flashcardSets by remember { mutableStateOf(repository.loadFlashcardSets()) }
    var quizSets by remember { mutableStateOf(repository.loadQuizSets()) }
    var paperSets by remember { mutableStateOf(repository.loadPaperSets()) }
    var quizPerformance by remember { mutableStateOf(repository.loadQuizPerformance()) }
    var activityHistory by remember { mutableStateOf(repository.loadActivityHistory()) }

    var showLevelUpConfetti by remember { mutableStateOf(false) }

    // Manage relaxing background music playback
    LaunchedEffect(profile.musicEnabled) {
        if (profile.musicEnabled) {
            RelaxingMusicPlayer.start()
        } else {
            RelaxingMusicPlayer.stop()
        }
    }

    // Check daily streak once on start
    LaunchedEffect(Unit) {
        if (profile.isOnboarded) {
            val updated = repository.checkAndRecordDailyStreak(profile)
            profile = updated
        }
    }

    fun handleAddXp(amount: Int, title: String, detail: String) {
        val (updatedProfile, didLevelUp) = repository.addXP(amount)
        profile = updatedProfile
        repository.addActivity(title, detail, amount)
        activityHistory = repository.loadActivityHistory()

        if (didLevelUp) {
            showLevelUpConfetti = true
            SoundPlayer.playLevelUpArpeggio(profile.soundEnabled)
        }
    }

    ProblemSolverTheme(darkTheme = profile.darkMode) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (!profile.isOnboarded) {
                    OnboardingScreen(
                        onComplete = { name, avatarEmoji ->
                            val newProfile = profile.copy(
                                name = name,
                                avatarEmoji = avatarEmoji,
                                isOnboarded = true,
                                streakCount = 1
                            )
                            repository.saveProfile(newProfile)
                            profile = newProfile
                            handleAddXp(5, "Welcome to Problem Solver", "Completed profile setup")
                        }
                    )
                } else {
                    // Back handler: return to Home if on secondary screen
                    BackHandler(enabled = currentNav != NavDestination.HOME) {
                        currentNav = NavDestination.HOME
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Navbar (document scroll friendly)
                        TopNavBar(
                            currentDestination = currentNav,
                            onNavigate = { destination ->
                                if (destination != NavDestination.ASK) {
                                    preselectedSubjectId = null
                                }
                                currentNav = destination
                            },
                            profile = profile,
                            onToggleMusic = {
                                val updated = profile.copy(musicEnabled = !profile.musicEnabled)
                                repository.saveProfile(updated)
                                profile = updated
                            }
                        )

                        // Main Screen Content based on navigation
                        Box(modifier = Modifier.weight(1f)) {
                            when (currentNav) {
                                NavDestination.HOME -> {
                                    HomeScreen(
                                        profile = profile,
                                        flashcardSets = flashcardSets,
                                        quizSets = quizSets,
                                        paperSets = paperSets,
                                        onAskClick = { subjectId ->
                                            preselectedSubjectId = subjectId
                                            currentNav = NavDestination.ASK
                                        },
                                        onStudyClick = {
                                            currentNav = NavDestination.STUDY
                                        }
                                    )
                                }
                                NavDestination.ASK -> {
                                    AskScreen(
                                        initialSubjectId = preselectedSubjectId,
                                        onQuestionAsked = { subject, question ->
                                            handleAddXp(3, "Asked Tutor ($subject)", question.take(50))
                                        },
                                        loadMessages = { subj -> repository.loadChatMessages(subj) },
                                        saveMessages = { subj, msgs -> repository.saveChatMessages(subj, msgs) }
                                    )
                                }
                                NavDestination.STUDY -> {
                                    StudyScreen(
                                        flashcardSets = flashcardSets,
                                        quizSets = quizSets,
                                        paperSets = paperSets,
                                        soundEnabled = profile.soundEnabled,
                                        onSaveFlashcards = { set ->
                                            val updated = flashcardSets.filter { it.id != set.id } + set
                                            flashcardSets = updated
                                            repository.saveFlashcardSets(updated)
                                        },
                                        onDeleteFlashcards = { id ->
                                            val updated = flashcardSets.filter { it.id != id }
                                            flashcardSets = updated
                                            repository.saveFlashcardSets(updated)
                                        },
                                        onSaveQuiz = { set ->
                                            val updated = quizSets.filter { it.id != set.id } + set
                                            quizSets = updated
                                            repository.saveQuizSets(updated)
                                        },
                                        onDeleteQuiz = { id ->
                                            val updated = quizSets.filter { it.id != id }
                                            quizSets = updated
                                            repository.saveQuizSets(updated)
                                        },
                                        onQuizCompleted = { correct, total ->
                                            repository.recordQuizResult(correct, total)
                                            quizPerformance = repository.loadQuizPerformance()
                                            val bonus = 15 + (correct * 2)
                                            handleAddXp(bonus, "Completed Quiz", "Scored $correct / $total")
                                        },
                                        onSavePaper = { set ->
                                            val updated = paperSets.filter { it.id != set.id } + set
                                            paperSets = updated
                                            repository.savePaperSets(updated)
                                        },
                                        onDeletePaper = { id ->
                                            val updated = paperSets.filter { it.id != id }
                                            paperSets = updated
                                            repository.savePaperSets(updated)
                                        },
                                        onFlashcardGenerated = {
                                            flashcardSets = repository.loadFlashcardSets()
                                            handleAddXp(10, "Generated Flashcards", "Created study set")
                                        },
                                        onQuizGenerated = {
                                            quizSets = repository.loadQuizSets()
                                            handleAddXp(10, "Generated Quiz", "Created multiple-choice quiz")
                                        },
                                        onPaperGenerated = {
                                            paperSets = repository.loadPaperSets()
                                            handleAddXp(12, "Generated Exam Paper", "Created sectioned question paper")
                                        }
                                    )
                                }
                                NavDestination.ACCOUNT -> {
                                    val totalQuestionsAsked = activityHistory.count { it.title.startsWith("Asked Tutor") }
                                    AccountScreen(
                                        profile = profile,
                                        quizPerformance = quizPerformance,
                                        activityHistory = activityHistory,
                                        statsQuestionsAsked = totalQuestionsAsked,
                                        statsFlashcardSets = flashcardSets.size,
                                        statsQuizzesTaken = quizPerformance.quizzesCompleted,
                                        statsPapersCreated = paperSets.size,
                                        onUpdateProfile = { updated ->
                                            profile = updated
                                            repository.saveProfile(updated)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Level-up celebration confetti overlay
                ConfettiOverlay(
                    visible = showLevelUpConfetti,
                    onDismiss = { showLevelUpConfetti = false }
                )
            }
        }
    }
}
