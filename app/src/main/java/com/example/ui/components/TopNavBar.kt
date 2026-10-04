package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.IndigoGradientBrush
import com.example.ui.theme.TealAccent

enum class NavDestination(val label: String, val testTag: String) {
    HOME("Home", "nav_home"),
    ASK("Ask", "nav_ask"),
    STUDY("Study", "nav_study"),
    ACCOUNT("Account", "nav_account")
}

@Composable
fun TopNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    profile: UserProfile,
    onToggleMusic: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo + Title + "Made by Virat Lala" Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigate(NavDestination.HOME) }
                    .padding(end = 16.dp)
            ) {
                ProblemSolverLogo(sizeDp = 42.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Problem Solver",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Made by Virat Lala",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Center: 4 Nav Links (Home, Ask, Study, Account)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    Box(
                        modifier = Modifier
                            .testTag(destination.testTag)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) {
                                    DeepIndigo
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable { onNavigate(destination) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right: Relaxing Music Button + Streak Counter + User Mascot Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                // Relaxing Background Music Toggle Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (profile.musicEnabled) TealAccent.copy(alpha = 0.18f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onToggleMusic() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("navbar_music_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (profile.musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        contentDescription = "Toggle Relaxing Music",
                        tint = if (profile.musicEnabled) TealAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (profile.musicEnabled) "Music On" else "Music Off",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.musicEnabled) TealAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Streak badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AmberAccent.copy(alpha = 0.16f))
                        .clickable { onNavigate(NavDestination.ACCOUNT) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak Flame",
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.streakCount}d",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberAccent
                    )
                }

                // Avatar emoji
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.avatarEmoji,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
