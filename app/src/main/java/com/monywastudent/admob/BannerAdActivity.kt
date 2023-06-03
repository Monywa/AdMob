package com.monywastudent.admob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class BannerAdActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BannerAdActivity"
    }

    private val adView: AdView by lazy { findViewById<AdView>(R.id.adView) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_ad)


        //Initialize mobile ads sdk (e.g. if you want to ad test device id to get test ads)
        MobileAds.initialize(this) {
            Log.d(TAG, "onIntializedCompleted:")
        }




        //For test device
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(
                listOf(
                    "PLACE_TEST_DEVICE_ID_1_HERE",
                    "PLACE_TEST_DEVICE_ID_2_HERE"
                )
            ).build()
        )

        //init banner ad
        val adRequest=AdRequest.Builder().build()
        adView.loadAd(adRequest)


        adView.adListener=object: AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG,"onAdClicked:")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(TAG,"onAdClosed:")

            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                // Code to be executed when an ad request fails.
                Log.e(TAG,"onAdFailedToLoad:${adError.message}")

            }

            override fun onAdImpression() {
                super.onAdImpression()
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.d(TAG,"onAdImpression:")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                // Code to be executed when an ad finishes loading.
                Log.d(TAG,"onAdLoaded:")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(TAG,"onAdOpened:")
            }

            override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
                Log.d(TAG,"onAdSwipeGestureClicked:")
            }

        }


    }
}