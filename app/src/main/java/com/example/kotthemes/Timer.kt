package com.example.kotthemes

import android.os.Handler
import android.os.Looper
import java.time.Duration

class Timer (listener: OnTimerTickListener){

    interface OnTimerTickListener{
        fun OnTimerTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable {
            duration = duration + delay
            handler.postDelayed(runnable,delay)
            listener.OnTimerTick(duration.toString())
        }
    }

    fun start(){
        handler.postDelayed(runnable,delay)
    }

    fun pause(){
        handler.removeCallbacks(runnable)
    }

    fun stop(){
        handler.removeCallbacks(runnable)
        duration=0L
    }

}