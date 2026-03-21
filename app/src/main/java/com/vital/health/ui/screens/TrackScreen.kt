package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackScreenContent(
    onLogWeight: () -> Unit,
    onLogBP: () -> Unit,
    onFabClick: () -> Unit
) {
    val currentMonthYear = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonthYear,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextMain
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", tint = PrimaryBlack, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.Search, contentDescription = "Search", tint = PrimaryBlack)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Outlined.List, contentDescription = "Filter", tint = PrimaryBlack)
            }

            SectionColumn("TODAY") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CreamCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(TanButton), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Person, "Weight", tint = PrimaryBlack)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("145 lbs", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("08:30 AM", color = TextMuted, fontSize = 14.sp)
                        }
                        Icon(Icons.Outlined.List, contentDescription = "Arrow", tint = TextMuted)
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = CreamCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryBlack), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.FavoriteBorder, "Heart", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("120/80 mmHg", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("09:15 AM", color = TextMuted, fontSize = 14.sp)
                        }
                        Icon(Icons.Outlined.List, contentDescription = "Arrow", tint = TextMuted)
                    }
                }
            }

            SectionColumn("QUICK ACTIONS") {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onLogWeight,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Log Weight")
                    }
                    Button(
                        onClick = onLogBP,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Log BP")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
        }
        
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = PrimaryBlack,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(Icons.Filled.Add, "Add")
        }
    }
}
