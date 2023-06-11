package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase8

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch

class RoomAndCoroutinesViewModel(
    private val api: MockApi,
    private val database: AndroidVersionDao
) : BaseViewModel<UiState>() {

    fun loadData() {
        uiState.value = UiState.Loading.LoadFromDb

        viewModelScope.launch {
            val localAndroidVersions = database.getAndroidVersions()
            if (localAndroidVersions.isNotEmpty()) {
                uiState.value =
                    UiState.Success(DataSource.DATABASE, localAndroidVersions.mapToUiModelList())
            } else {
                uiState.value = UiState.Error(DataSource.DATABASE, "Database is empty")
            }

            uiState.value = UiState.Loading.LoadFromNetwork
            try {
                val recentVersions = api.getRecentAndroidVersions()
                recentVersions.forEach { androidVersion ->
                    database.insert(androidVersion.mapToEntity())
                }
                uiState.value = UiState.Success(DataSource.NETWORK, recentVersions)
            } catch (exception: Exception) {
                uiState.value =
                    UiState.Error(DataSource.NETWORK, exception.message ?: "Something went wrong")
            }
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            database.clear()
        }
    }
}

enum class DataSource(val dataSourceName: String) {
    DATABASE("Database"),
    NETWORK("Network")
}