package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch
import timber.log.Timber

class RetryNetworkRequestViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            val numberOfTries = 2

            try {
                repeat(numberOfTries) {
                    try {
                        loadRecentAndroidVersions()
                        return@launch
                    } catch (ex: java.lang.Exception) {
                        Timber.e(ex)
                    }
                }
                loadRecentAndroidVersions()
            } catch (exception: Exception) {
                Timber.e(exception)
                uiState.value = UiState.Error("Network Request failed!")
            }
        }
    }

    private suspend fun loadRecentAndroidVersions() {
        val recentAndroidVersions = api.getRecentAndroidVersions()
        uiState.value = UiState.Success(recentAndroidVersions)
    }
}