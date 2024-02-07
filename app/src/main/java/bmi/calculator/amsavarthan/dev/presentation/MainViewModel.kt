package bmi.calculator.amsavarthan.dev.presentation

import android.graphics.Picture
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.Snapshot.Companion.withMutableSnapshot
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import bmi.calculator.amsavarthan.dev.domain.AppConstants
import bmi.calculator.amsavarthan.dev.domain.BitmapHelper
import bmi.calculator.amsavarthan.dev.domain.BmiHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface MainUiEvent {
    data class ShareImage(val uri: Uri) : MainUiEvent
    data class Error(val message: String) : MainUiEvent
}

@OptIn(SavedStateHandleSaveableApi::class)
class MainViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<MainUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var weightInKg by savedStateHandle.saveable { mutableIntStateOf(AppConstants.INITIAL_WEIGHT) }
        private set

    var heightInCm by savedStateHandle.saveable { mutableIntStateOf(AppConstants.INITIAL_HEIGHT) }
        private set

    val bmiResult: Double
        get() = BmiHelper.calculateBmi(weightInKg, heightInCm)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            throwable.printStackTrace()
            _uiEvent.emit(MainUiEvent.Error("Something went wrong!"))
        }
    }

    fun updateWeight(weight: Int) {
        withMutableSnapshot {
            weightInKg = weight
        }
    }

    fun updateHeight(height: Int) {
        withMutableSnapshot {
            heightInCm = height
        }
    }

    fun createBitmap(picture: Picture, bitmapHelper: BitmapHelper) {
        viewModelScope.launch(exceptionHandler) {
            val bitmap = bitmapHelper.createBitmapFromPicture(picture)
            val uri = bitmapHelper.saveToCacheAndGetUri(bitmap)
            _uiEvent.emit(MainUiEvent.ShareImage(uri))
        }
    }

}