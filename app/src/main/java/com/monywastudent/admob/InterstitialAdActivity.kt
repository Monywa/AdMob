package com.monywastudent.admob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdActivity : AppCompatActivity() {

    private val showInterstitialBtn: Button by lazy {
        findViewById(R.id.show_interstitial_ad)
    }

    companion object {
        private const val TAG = "InterstitialAd"
    }

    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial_ad)

        //action bar title
        title = "Interstitial Ad"


        //Initialize mobile ads sdk (e.g. if you want to ad test device id to get test ads)
        MobileAds.initialize(this) { status ->
            Log.d(TAG, "onCreate status:$status")
        }

        //Set your test devices. Check your logcat output for the hashed device id to
        //get test ads on a physical device. e.g
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("TEST_ID_1", "TEST_ID_N"))
                .build()
        )



        showInterstitialBtn.setOnClickListener {
            loadInterstitialAd()
        }
    }

    private fun loadInterstitialAd() {
        //AdRequest to load interstitial Ad
        val adRequest = AdRequest.Builder().build()

        //load Interstitial ad and if exits , assign value
        InterstitialAd.load(
            this,
            resources.getString(R.string.interstitial_ad_id_test),
            adRequest,
            object :
                InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    Log.d(TAG, "onAdFailedToLoad :$adError")
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    Log.d(TAG, "onAdLoaded :$interstitialAd")
                    mInterstitialAd = interstitialAd
                    showInterstitialAd()
                }
            })
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            Log.d(TAG, "showInterstitialAd: Ad was loaded, we can show")
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "onAdClicked:")
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()

                    // Called when ad is dismissed.
                    // Don't forget to set the ad reference to null so you don't show the same ad again
                    Log.d(TAG, "onAdDismissedFullScreenContent: ")
                    mInterstitialAd = null
                    toast("Ad is closed. here you can perform the task e.g start activity")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    // Called when ad fails to show.
                    Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "onAdImpression: ")
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    // Called when ad is shown.
                    Log.d(TAG, "onAdShowedFullScreenContent: ")
                }
            }

            //show ad
            mInterstitialAd?.show(this)
        } else {
            //ad was not loaded
            Log.d(TAG, "showInterstitialAd: Ad was not loaded , we can't show")
            toast("Ad was not loaded. Perform task e.g start activity")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}