package fr.enssat.tpmulticast

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MulticastViewModelFactory(val context: Context): ViewModelProvider.Factory {

    //factory crée afin de parvenir à passer un parameter en argumet ici le context aau view model...
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MultiCastViewModel(context.applicationContext) as T
    }

}