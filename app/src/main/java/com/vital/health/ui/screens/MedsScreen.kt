package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedsScreenContent(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextMain)
            }
            Text(
                text = "Metformin",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextMain,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            Text("ACTIVE", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = true,
                onCheckedChange = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlack
                )
            )
        }

        // Today's Progress Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TODAY'S PROGRESS", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("2 of 2 completed", color = PrimaryBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(TanButton)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // 100% completed based on text
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryBlack)
                    )
                }
            }
        }

        // Frequency
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Frequency", color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CreamCard),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlack)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Daily", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Weekly", color = TextMuted, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Custom", color = TextMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Schedule
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Schedule", color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("2 Reminders Set", color = TextMuted, fontSize = 12.sp)
            }

            // Reminder 1
            ReminderCard("08:00 AM", "1 tablet • Before meal", true)
            
            // Reminder 2
            ReminderCard("08:00 PM", "1 tablet • Before meal", true)
        }

        Spacer(modifier = Modifier.weight(1f, fill = false)) // push the button somewhat down if space exists

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = Color.White)
        ) {
            Text("+ Add New Time", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ReminderCard(time: String, description: String, isActive: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TanButton),
                contentAlignment = Alignment.Center
            ) {
                // Using Search temporary icon to mock clock since I don't import clock
                Icon(Icons.Outlined.Search, contentDescription = "Time", tint = PrimaryBlack)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(time, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(description, color = TextMuted, fontSize = 14.sp)
            }
            Icon(Icons.Outlined.Create, contentDescription = "Edit", tint = TextMuted, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isActive,
                onCheckedChange = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlack
                )
            )
        }
    }
}
