package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.ui.theme.*

@Composable
fun VitalsScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Health Trends", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextMain)
                Text("Analytics & Insights", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TanButton, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = "Calendar", tint = PrimaryBlack)
            }
        }

        // Segmented Control
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
                Text("Week", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Month", color = TextMuted, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Year", color = TextMuted, fontWeight = FontWeight.SemiBold)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("WEIGHT TREND", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("62.4 kg", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Average", color = TextMuted, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(VitalSuccess).padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-0.8 kg", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(60.dp)) // Mock Chart space
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { index, day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(if(index == 3) PrimaryBlack else TanButton))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day, color = if(index == 3) PrimaryBlack else TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Favorite, "Heart", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Blood Pressure", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Average: 118/76 mmHg", color = TextMuted, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(VitalSuccess).padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("• NORMAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
        
        SectionColumn("HEALTH INSIGHTS") {
            Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Info, "Insight", tint = PrimaryBlack, modifier = Modifier.padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("BLOOD PRESSURE PATTERN", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Systolic readings are consistently 5% higher in the morning (6AM-9AM).", color = TextMuted, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Stability improved following consistent medication adherence last week.", color = TextMuted, fontSize = 14.sp)
                    }
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = CreamCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Warning, "Insight", tint = VitalError, modifier = Modifier.padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("WEIGHT CORRELATION", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Downward trend correlates with 15% increase in tracked physical activity.", color = TextMuted, fontSize = 14.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
