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

    private val rewardInterstitialBtn:Button by lazy{
        findViewById(R.id.rewardedInterstitial_btn)
    }

    private val rewardAdBtn:Button by lazy{
        findViewById(R.id.rewarded_btn)
    }

    private val nativeAdBtn:Button by lazy{
        findViewById(R.id.native_ad_btn)
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

        //navigate to rewarded interstitial ad
        rewardInterstitialBtn.setOnClickListener {
            val intent=Intent(this,RewardedInterstitialActivity::class.java)
            startActivity(intent)
        }

        //navigate to rewarded interstitial ad
        rewardAdBtn.setOnClickListener {
            val intent=Intent(this,RewardedAdActivity::class.java)
            startActivity(intent)
        }

        //navigate to native ad
        nativeAdBtn.setOnClickListener {
            val intent=Intent(this,NativeAdActivity::class.java)
            startActivity(intent)
        }


    }
}