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
            try {
                retry(5) {
                    loadRecentAndroidVersions()
                }
            } catch (exception: Exception) {
                Timber.e(exception)
                uiState.value = UiState.Error("Network Request failed!")
            }
        }
    }

    private suspend fun <T> retry(numberOfRetries: Int, block: suspend () -> T): T {
        repeat(numberOfRetries) {
            try {
                return block()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
        return block()
    }

    private suspend fun loadRecentAndroidVersions() {
        val recentAndroidVersions = api.getRecentAndroidVersions()
        uiState.value = UiState.Success(recentAndroidVersions)
    }
}