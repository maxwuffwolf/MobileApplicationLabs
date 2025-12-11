package com.lab4.ui.screens.subjectDetails

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab4.data.db.DatabaseStorage
import com.lab4.data.entity.LabStatus
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import com.lab4.data.entity.toUkrainianText
import com.lab4.ui.navigation.SubjectDetailsRoute
import com.lab4.ui.theme.Lab4Theme

@Composable
fun SubjectDetailsScreen(
    route: SubjectDetailsRoute,
    onLabClick: (Int) -> Unit
) {
    // Context - object which contains info about your app, has access to storage
    // is used for Room DB initialization
    val context = LocalContext.current
    // Getting the DB instance for screen
    val db = DatabaseStorage.getDatabase(context)

    // States have to be filled with values from LaunchedEffect(Unit) {}
    val subjectState = remember { mutableStateOf<SubjectEntity?>(null) }
    val subjectLabsState = remember { mutableStateOf<List<SubjectLabEntity>>(emptyList()) }

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

    // UI of screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Subject", fontSize = 28.sp)
        Text(
            text = "ID: ${subjectState.value?.id} Title: ${subjectState.value?.title}",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(text = "Лабораторні роботи", fontSize = 28.sp, modifier = Modifier.padding(top = 16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            items(subjectLabsState.value) { lab ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            lab.id?.let { onLabClick(it) }
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = lab.title,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Статус: ${lab.status.toUkrainianText()}",
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if (lab.comment != null) {
                            Text(
                                text = "Коментар: ${lab.comment}",
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Preview can't display data from DB
 */
@Preview(showBackground = true)
@Composable
private fun SubjectDetailsScreenPreview() {
    Lab4Theme {
        SubjectDetailsScreen(SubjectDetailsRoute(1), {})
    }
}