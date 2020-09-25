package pro.devapp.currencyrates.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    fun showMainApp(callback: () -> Unit) {
        viewModelScope.launch {
            delay(1000)
            callback()
        }
    }
}