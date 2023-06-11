package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val oreoFeature = mockApi.getAndroidVersionFeatures(27)
                val pieFeature = mockApi.getAndroidVersionFeatures(28)
                val android10Feature = mockApi.getAndroidVersionFeatures(29)

                val versionFeatures = listOf(oreoFeature, pieFeature, android10Feature)
                uiState.value = UiState.Success(versionFeatures)
            } catch (ex: Exception) {
                uiState.value = UiState.Error("Network Request failed")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val oreoFeatureDeferred = viewModelScope.async { mockApi.getAndroidVersionFeatures(27) }
                val pieFeatureDeferred = viewModelScope.async { mockApi.getAndroidVersionFeatures(28) }
                val android10FeatureDeferred = viewModelScope.async { mockApi.getAndroidVersionFeatures(29) }

                val versionFeatures = listOf(
                    oreoFeatureDeferred.await(),
                    pieFeatureDeferred.await(),
                    android10FeatureDeferred.await()
                )
                uiState.value = UiState.Success(versionFeatures)
            } catch (ex: Exception) {
                uiState.value = UiState.Error("Network Request failed")
            }
        }
    }
}