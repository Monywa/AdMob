package com.monywastudent.admob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val bannerAdBtn:Button by lazy{
        findViewById(R.id.banner_btn)
    }

    private val interstitialBtn:Button by lazy{
        findViewById(R.id.interstitial_btn)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //navigate to banner ad
        bannerAdBtn.setOnClickListener {
            val intent=Intent(this,BannerAdActivity::class.java)
            startActivity(intent)
        }

        //navigate to interstitial ad
        interstitialBtn.setOnClickListener {
            val intent=Intent(this,InterstitialAdActivity::class.java)
            startActivity(intent)
        }
    }
}