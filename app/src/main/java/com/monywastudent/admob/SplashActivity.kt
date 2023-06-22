package com.monywastudent.admob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView

class SplashActivity : AppCompatActivity() {

    private val timerTv: TextView by lazy {
        findViewById(R.id.timer_tv)
    }

    companion object {
        private const val TAG="SPLASH_TAG"
        private const val COUNTER_TIMER:Long=5
    }

    private var secondRemaining:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        createTimer(COUNTER_TIMER)
    }

    private fun createTimer(seconds: Long) {
         val countDownTimer:CountDownTimer=object : CountDownTimer(seconds*1000,1000){
             override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"OnTick: $millisUntilFinished")
                 secondRemaining=millisUntilFinished/1000 +1
                 timerTv.text="Loading in $secondRemaining"
             }

             override fun onFinish() {
                 Log.d(TAG,"onFinish: ")
                 secondRemaining=0
                 timerTv.text="Loaded.."

                 val application=application
                 if(application !is MyApplication){
                     Log.d(TAG,"onFinish: Failed to cast application to MyApplication") // e.g if you don't register your application in manifest
                     startMainActivity()
                     return
                 }

                 application.showAdIfAvailable(this@SplashActivity,object:
                     MyApplication.OnShowAdCompleteListener {
                     override fun onShowAdComplete() {
                         startMainActivity()
                     }
                 })


             }

         }

        //start Timer
        countDownTimer.start()
    }

    private fun startMainActivity() {
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
    }


}