package com.monywastudent.admob

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.monywastudent.admob.databinding.ActivityRewardedInterstitialBinding

class RewardedInterstitialActivity : AppCompatActivity() {

    private val showAdBtn: Button by lazy {
        findViewById(R.id.show_ad_btn)
    }

    private val loadAndShowAdBtn: Button by lazy {
        findViewById(R.id.load_and_show_ad_btn)
    }

    companion object {
        const val TAG = "RewardedInterstitialActivity"
    }

    private var mRewardInterstitialAd: RewardedInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_interstitial)


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



        showAdBtn.setOnClickListener {
          loadRewardInterstitialAd()
        }
        loadAndShowAdBtn.setOnClickListener {
            loadAndShowRewardedAd()
        }
    }


    private fun loadRewardInterstitialAd() {
        //AdRequest to load interstitial Ad
        val adRequest = AdRequest.Builder().build()

        //load  Rewarded Interstitial ad and if exits , assign value
        RewardedInterstitialAd.load(
            this,
            resources.getString(R.string.rewarded_interstitial_ad_id_test),
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    super.onAdFailedToLoad(loadError)
                    mRewardInterstitialAd = null
                }

                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    super.onAdLoaded(rewardedInterstitialAd)
                    mRewardInterstitialAd = rewardedInterstitialAd
                    showRewardedInterstitialAd()
                }
            })
    }

    private fun showRewardedInterstitialAd() {
        if (mRewardInterstitialAd != null) {
            Log.d(TAG, "showRewardedInterstitialAd: Ad was loaded, we can show")
            mRewardInterstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        // Called when a click is recorded for an ad.
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()

                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you don't show the same ad again
                        Log.d(TAG, "onAdDismissedFullScreenContent: ")
                        mRewardInterstitialAd = null
                        toast("Ad is closed. here you can perform the task e.g start activity")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                        mRewardInterstitialAd = null
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

            //show ad, listen for user earn reward
            mRewardInterstitialAd!!.show(this) { rewardItem ->
                /* called when user completely watched ad */

                Log.d(TAG, "OnUserEarnedReward : ${rewardItem.toString()}")
                toast("Reward Earned....")
            }
        } else {

            //ad was not loaded
            Log.d(TAG, "showRewardedInterstitialAd: Ad was not loaded , we can't show")
            toast("Ad was not loaded. Perform task e.g start activity")
        }
    }

    /*
    2) When user click show ad button then load ad and show when loaded, you may show progress
    while ad is being loaded
     */
    private fun loadAndShowRewardedAd() {
        //show progress dialog
        val alertDialog = AlertDialog.Builder(this).setCancelable(false).setMessage("Loading").show()



        //AdRequest to load interstitial Ad
        val adRequest = AdRequest.Builder().build()

        //load  Rewarded Interstitial ad and if exits , assign value
        RewardedInterstitialAd.load(
            this,
            resources.getString(R.string.rewarded_interstitial_ad_id_test),
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    super.onAdFailedToLoad(loadError)
                    mRewardInterstitialAd = null

                    alertDialog.dismiss()
                }

                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    super.onAdLoaded(rewardedInterstitialAd)
                    mRewardInterstitialAd = rewardedInterstitialAd

                    alertDialog.dismiss()
                    showRewardedInterstitialAd()
                }
            })

    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}