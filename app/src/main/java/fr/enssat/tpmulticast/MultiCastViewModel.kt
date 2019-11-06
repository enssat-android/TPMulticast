package fr.enssat.tpmulticast

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MultiCastViewModel(context: Context): ViewModel() {

    private val _msgSet = mutableSetOf<String>()
    private val _allmessages = MutableLiveData<List<String>>()
    private val _multicast = MultiCastAgent(this::onReceiveMessage)

    val messages: LiveData<List<String>> get() = _allmessages

    init {
        MultiCastAgent.wifiLock(context)
        _multicast.startReceiveLoop()
    }

    override fun onCleared() {
        _multicast.stopReceiveLoop()
        MultiCastAgent.releaseWifiLock()
        super.onCleared()
    }

    fun onReceiveMessage(msg: String) {
        _msgSet.add(msg)
        _allmessages.postValue(_msgSet.toList())
    }

    fun send(msg: String) {
        _multicast.send(msg)
    }
}