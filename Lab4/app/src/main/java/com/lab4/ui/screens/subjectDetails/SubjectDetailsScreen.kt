package com.lab4.ui.screens.subjectDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lab4.data.db.DatabaseStorage
import com.lab4.data.entity.LabStatus
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import com.lab4.ui.navigation.SubjectDetailsRoute
import com.lab4.ui.theme.Lab4Theme
import kotlinx.coroutines.launch

@Composable
fun SubjectDetailsScreen(
    route: SubjectDetailsRoute,
) {
    // Context - object which contains info about your app, has access to storage
    // is used for Room DB initialization
    val context = LocalContext.current
    // Getting the DB instance for screen
    val db = DatabaseStorage.getDatabase(context)
    val coroutineScope = rememberCoroutineScope()

    // States have to be filled with values from LaunchedEffect(Unit) {}
    val subjectState = remember { mutableStateOf<SubjectEntity?>(null) }
    val subjectLabsState = remember { mutableStateOf<List<SubjectLabEntity>>(emptyList()) }
    
    // Dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedLab by remember { mutableStateOf<SubjectLabEntity?>(null) }

    /** ! Important !
        LaunchEffect(){...} - is the side effect which allows make operations in another thread (for accessing to DB)
     *  This example LaunchEffect(Unit) - means that the operations inside will be performed only once on creating the screen
     */
    LaunchedEffect(Unit) {
        // fetching the Subject from DB by id (subjectsDao is used)
        subjectState.value = db.subjectsDao.getSubjectById(route.id)
        // fetching the Labs from DB by subject id (subjectLabsDao is used)
        subjectLabsState.value = db.subjectLabsDao.getSubjectLabsBySubjectId(route.id)
    }

    // Function to refresh labs list
    fun refreshLabs() {
        coroutineScope.launch {
            subjectLabsState.value = db.subjectLabsDao.getSubjectLabsBySubjectId(route.id)
        }
    }

    // UI of screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Subject title
        Text(
            text = subjectState.value?.title ?: "Завантаження...",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Statistics
        val stats = calculateStats(subjectLabsState.value)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem("Всього", stats.total.toString())
                    StatItem("Виконано", stats.completed.toString())
                    StatItem("В процесі", stats.inProgress.toString())
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem("Відкладено", stats.postponed.toString())
                    StatItem("Не розпочато", stats.notStarted.toString())
                    // Empty space for symmetry
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Text(
            text = "Лабораторні роботи",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Labs list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subjectLabsState.value) { lab ->
                LabCard(
                    lab = lab,
                    onClick = {
                        selectedLab = lab
                        showEditDialog = true
                    }
                )
            }
        }
    }

    // Edit dialog
    if (showEditDialog && selectedLab != null) {
        EditLabDialog(
            lab = selectedLab!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedLab ->
                coroutineScope.launch {
                    db.subjectLabsDao.updateSubjectLab(updatedLab)
                    refreshLabs()
                    showEditDialog = false
                }
            }
        )
    }
}

@Composable
fun LabCard(lab: SubjectLabEntity, onClick: () -> Unit) {
    val status = try {
        LabStatus.valueOf(lab.status)
    } catch (e: Exception) {
        LabStatus.NOT_STARTED
    }
    
    val (icon, iconColor) = getStatusIconAndColor(status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.name,
                tint = iconColor,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lab.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = getStatusText(status),
                    style = MaterialTheme.typography.bodySmall,
                    color = iconColor
                )
                if (lab.comment.isNotBlank()) {
                    Text(
                        text = lab.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditLabDialog(
    lab: SubjectLabEntity,
    onDismiss: () -> Unit,
    onSave: (SubjectLabEntity) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(lab.status) }
    var comment by remember { mutableStateOf(lab.comment) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = lab.title) },
        text = {
            Column {
                Text(
                    text = "Статус роботи",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    val currentStatus = try {
                        LabStatus.valueOf(selectedStatus)
                    } catch (e: Exception) {
                        LabStatus.NOT_STARTED
                    }
                    
                    OutlinedTextField(
                        value = getStatusText(currentStatus),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        LabStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(getStatusText(status)) },
                                onClick = {
                                    selectedStatus = status.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Коментар",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("Додайте коментар...") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(lab.copy(status = selectedStatus, comment = comment))
                }
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

fun getStatusIconAndColor(status: LabStatus): Pair<ImageVector, Color> {
    return when (status) {
        LabStatus.NOT_STARTED -> Icons.Default.Circle to Color.Gray
        LabStatus.IN_PROGRESS -> Icons.Default.PlayArrow to Color(0xFF2196F3)
        LabStatus.POSTPONED -> Icons.Default.Schedule to Color(0xFFFF9800)
        LabStatus.COMPLETED -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
    }
}

fun getStatusText(status: LabStatus): String {
    return when (status) {
        LabStatus.NOT_STARTED -> "Не розпочато"
        LabStatus.IN_PROGRESS -> "В прогресі"
        LabStatus.POSTPONED -> "Відкладено"
        LabStatus.COMPLETED -> "Виконано"
    }
}

data class LabStats(
    val total: Int,
    val completed: Int,
    val inProgress: Int,
    val postponed: Int,
    val notStarted: Int
)

fun calculateStats(labs: List<SubjectLabEntity>): LabStats {
    var completed = 0
    var inProgress = 0
    var postponed = 0
    var notStarted = 0

    labs.forEach { lab ->
        // Try to parse status, default to NOT_STARTED if invalid
        val status = try {
            LabStatus.valueOf(lab.status)
        } catch (e: IllegalArgumentException) {
            LabStatus.NOT_STARTED
        }
        
        when (status) {
            LabStatus.COMPLETED -> completed++
            LabStatus.IN_PROGRESS -> inProgress++
            LabStatus.POSTPONED -> postponed++
            LabStatus.NOT_STARTED -> notStarted++
        }
    }

    return LabStats(
        total = labs.size,
        completed = completed,
        inProgress = inProgress,
        postponed = postponed,
        notStarted = notStarted
    )
}

/**
 * Preview can't display data from DB
 */
@Preview(showBackground = true)
@Composable
private fun SubjectDetailsScreenPreview() {
    Lab4Theme {
        SubjectDetailsScreen(SubjectDetailsRoute(1))
    }
}