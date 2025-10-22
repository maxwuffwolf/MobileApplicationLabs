package com.vovan.lab7.ui.screens.subjectDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vovan.lab7.data.GeminiAIRepository
import com.vovan.lab7.data.entity.TextPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class GameScreenViewModel(
    val geminiAIRepository: GeminiAIRepository
) : ViewModel() {

    // stateFlows to store the state of loading
    private val _isLoadingStateFlow = MutableStateFlow<Boolean>(false)
    val isLoadingStateFlow: StateFlow<Boolean>
        get() = _isLoadingStateFlow

    // stateFlows for storing current generated List<TextPair> data
    private val _textPairListStateFlow = MutableStateFlow<List<TextPair>?>(null)
    val textPairListStateFlow: StateFlow<List<TextPair>?>
        get() = _textPairListStateFlow

    // request to the geminiAIRepository to generate list of TextPair
    fun requestGameData(){
        viewModelScope.launch {
            // change loading state to start loading (loading progress on UI is visible)
            _isLoadingStateFlow.value = true

            // request generated data and storing in stateFlow
            _textPairListStateFlow.value = geminiAIRepository.generateTextParList()

            // change loading state to stop loading (loading progress on UI is invisible)
            _isLoadingStateFlow.value = false
        }
    }
}