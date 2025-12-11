package com.lab4.ui.screens.labDetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab4.data.db.DatabaseStorage
import com.lab4.data.entity.LabStatus
import com.lab4.data.entity.SubjectLabEntity
import com.lab4.data.entity.toUkrainianText
import com.lab4.ui.navigation.LabDetailsRoute
import kotlinx.coroutines.launch

@Composable
fun LabDetailsScreen(
    route: LabDetailsRoute,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = DatabaseStorage.getDatabase(context)
    val scope = rememberCoroutineScope()
    
    var lab by remember { mutableStateOf<SubjectLabEntity?>(null) }
    var comment by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(LabStatus.NOT_STARTED) }
    var expanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        lab = db.subjectLabsDao.getSubjectLabById(route.labId)
        lab?.let {
            comment = it.comment ?: ""
            selectedStatus = it.status
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = onBack) {
            Text("← Назад")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        lab?.let { currentLab ->
            Text(
                text = currentLab.title,
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = currentLab.description,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Status selector
            Text(
                text = "Статус:",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedStatus.toUkrainianText(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    LabStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.toUkrainianText()) },
                            onClick = {
                                selectedStatus = status
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Comment field
            Text(
                text = "Коментар:",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Введіть коментар...") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save button
            Button(
                onClick = {
                    scope.launch {
                        db.subjectLabsDao.updateSubjectLab(
                            currentLab.copy(
                                status = selectedStatus,
                                comment = comment.ifBlank { null }
                            )
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зберегти", fontSize = 16.sp)
            }
        }
    }
}
