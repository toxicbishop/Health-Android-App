package com.vital.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital.health.data.local.HealthLogEntity
import com.vital.health.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    val healthLogs: StateFlow<List<HealthLogEntity>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addLog(type: String, value: String, unit: String, notes: String? = null) {
        viewModelScope.launch {
            repository.addLog(type, value, unit, notes)
        }
    }

    fun sync() {
        viewModelScope.launch {
            repository.syncWithRemote()
        }
    }
}
