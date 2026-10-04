package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.GeminiService
import com.example.data.AllLanguages
import com.example.data.AllSubjects
import com.example.data.ChatMessage
import com.example.data.LanguageItem
import com.example.data.SubjectItem
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.IndigoGradientBrush
import com.example.ui.theme.TealAccent
import kotlinx.coroutines.launch

@Composable
fun AskScreen(
    initialSubjectId: String? = null,
    onQuestionAsked: (subject: String, question: String) -> Unit,
    loadMessages: (subject: String) -> List<ChatMessage>,
    saveMessages: (subject: String, messages: List<ChatMessage>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember {
        mutableStateOf(
            AllSubjects.find { it.id == initialSubjectId } ?: AllSubjects.first()
        )
    }
    var selectedLanguage by remember { mutableStateOf(AllLanguages.first()) }
    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    var chatMessages by remember(selectedSubject.id) {
        mutableStateOf(loadMessages(selectedSubject.id))
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Scroll to bottom when messages update
    LaunchedEffect(chatMessages.size, isGenerating) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky/Top Control Header: Subject Chips & Language Chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                // 1. Subject Picker Pill Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subject:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    AllSubjects.forEach { subj ->
                        val isSelected = subj.id == selectedSubject.id
                        Box(
                            modifier = Modifier
                                .testTag("subject_chip_${subj.id}")
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) DeepIndigo else MaterialTheme.colorScheme.background
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) DeepIndigo else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (selectedSubject.id != subj.id) {
                                        selectedSubject = subj
                                        chatMessages = loadMessages(subj.id)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${subj.emoji} ${subj.name}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Language Picker Pill Chips (14 languages with flags)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Language:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    AllLanguages.forEach { lang ->
                        val isSelected = lang.code == selectedLanguage.code
                        Box(
                            modifier = Modifier
                                .testTag("lang_chip_${lang.code}")
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) TealAccent else MaterialTheme.colorScheme.background
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) TealAccent else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedLanguage = lang }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${lang.flagEmoji} ${lang.name}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Chat Log Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (chatMessages.isEmpty() && !isGenerating) {
                // Empty state greeting
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = selectedSubject.emoji,
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ask your ${selectedSubject.name} tutor anything!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Replies will be explained step-by-step in ${selectedLanguage.name} ${selectedLanguage.flagEmoji}. You'll earn +3 XP per question!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }

                    if (isGenerating) {
                        item {
                            ThreeDotLoadingIndicator()
                        }
                    }
                }
            }
        }

        // Bottom Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text("Ask a question about ${selectedSubject.name}...")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ask_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Send Button (Indigo Gradient)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isGenerating) IndigoGradientBrush
                                else androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
                            )
                            .clickable(enabled = inputText.isNotBlank() && !isGenerating) {
                                val questionText = inputText.trim()
                                if (questionText.isNotEmpty()) {
                                    val userMsg = ChatMessage(
                                        subject = selectedSubject.id,
                                        isUser = true,
                                        text = questionText
                                    )
                                    val updatedList = chatMessages + userMsg
                                    chatMessages = updatedList
                                    saveMessages(selectedSubject.id, updatedList)
                                    inputText = ""
                                    isGenerating = true

                                    onQuestionAsked(selectedSubject.id, questionText)

                                    coroutineScope.launch {
                                        val reply = GeminiService.askTutor(
                                            question = questionText,
                                            subject = selectedSubject.name,
                                            language = "${selectedLanguage.name} (${selectedLanguage.flagEmoji})"
                                        )
                                        val aiMsg = ChatMessage(
                                            subject = selectedSubject.id,
                                            isUser = false,
                                            text = reply
                                        )
                                        val finalList = chatMessages + aiMsg
                                        chatMessages = finalList
                                        saveMessages(selectedSubject.id, finalList)
                                        isGenerating = false
                                    }
                                }
                            }
                            .testTag("send_question_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (isUser) {
            // User message in Indigo Gradient bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(IndigoGradientBrush)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        } else {
            // AI message in White / Surface bordered bubble
            Card(
                modifier = Modifier.widthIn(max = 580.dp),
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 18.dp
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Tutor",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeDotLoadingIndicator() {
    val transition = rememberInfiniteTransition(label = "dots")
    val dot1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d1"
    )
    val dot2 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d2"
    )
    val dot3 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d3"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(TealAccent.copy(alpha = dot1))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(TealAccent.copy(alpha = dot2))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(TealAccent.copy(alpha = dot3))
            )
        }
    }
}
