package com.vovan.lab7.ui.screens.subjectDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel = koinViewModel(),
) {
    val isLoadingState = viewModel.isLoadingStateFlow.collectAsState()
    val textPairListState = viewModel.textPairListStateFlow.collectAsState()

    // invoke once the screen is opened
    LaunchedEffect(Unit) {
        viewModel.requestGameData()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            when {
                // show loading circular progress indicator
                isLoadingState.value -> {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }

                else -> {
                    // show the list of TextPair if it's not Null
                    // - textPairListState.value?.let { } - automatically check if not null
                    textPairListState.value?.let { textPairList ->
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(items = textPairList) { textPair ->
                                // nested state for managing visibility of Answer
                                // by default is False
                                val isVisibleText2State = remember { mutableStateOf(false) }
                                Card(
                                    onClick = { isVisibleText2State.value = true },
                                    modifier = Modifier.height(100.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = textPair.text1,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        // animation container component
                                        // it will animate visibility when isVisibleText2State.value is True
                                        AnimatedVisibility(
                                            visible = isVisibleText2State.value,
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = textPair.text1,
                                                fontSize = 20.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    // if Null show the placeholder
                    // ?: - means "do if null" or "else"
                    } ?: Text("No items")
                }
            }
        }
    }
}
