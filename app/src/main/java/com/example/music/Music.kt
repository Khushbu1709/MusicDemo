package com.example.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Music {

    var checked = true
    const val ACTION_START_PLAYER = "com.music.action.START"
    const val ACTION_STOP_PLAYER = "com.music.action.STOP"
    const val ACTION_CLOSE = "com.music.action.STOP.SERVICE"
    const val CHANNEL_ID = 104

    const val PLAY="Play"
    const val PAUSE="Pause"

    var mutableLiveData:MutableLiveData<String> = MutableLiveData()
    var livedata:LiveData<String> = mutableLiveData


}