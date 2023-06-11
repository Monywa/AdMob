package com.monywastudent.admob

import android.app.AlertDialog
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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class RewardedAdActivity : AppCompatActivity() {

    private val showAdBtn: Button by lazy {
        findViewById(R.id.show_ad_btn)
    }

    private val loadAndShowAdBtn: Button by lazy {
        findViewById(R.id.load_and_show_ad_btn)
    }

    companion object {
        const val TAG = "RewardedAdActivity"
    }


    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_ad)

        //Initialize mobile ads sdk (e.g. if you want to ad test device id to get test ads)
        MobileAds.initialize(this) { status ->
            Log.d(RewardedInterstitialActivity.TAG, "onCreate status:$status")
        }

        //Set your test devices. Check your logcat output for the hashed device id to
        //get test ads on a physical device. e.g
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("TEST_ID_1", "TEST_ID_N"))
                .build()
        )


        loadRewardedAd()
        showAdBtn.setOnClickListener {
            showRewardedAd()
        }
        loadAndShowAdBtn.setOnClickListener {
            loadAndShowRewardedAd()
        }
    }


    private fun loadRewardedAd() {
        //AdRequest to load interstitial Ad
        val adRequest = AdRequest.Builder().build()

        //load  Rewarded  ad and if exits , assign value
        RewardedAd.load(
            this,
            resources.getString(R.string.rewarded_ad_id_test),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    super.onAdFailedToLoad(loadError)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd=rewardedAd
                }

            })
    }

    private fun showRewardedAd(){
        if (mRewardedAd != null) {
            Log.d(TAG, "showRewardedAd: Ad was loaded, we can show")
            mRewardedAd!!.fullScreenContentCallback =
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
                        mRewardedAd = null
                        toast("Ad is closed. here you can perform the task e.g start activity")
                        loadRewardedAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                        mRewardedAd = null
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
            mRewardedAd?.let {ad->
                ad.show(this) { rewardItem ->
                /* called when user completely watched ad */

                Log.d(TAG, "OnUserEarnedReward : ${rewardItem.toString()}")
                toast("Reward Earned....")
            }
            }
        } else {

            //ad was not loaded
            Log.d(TAG, "showRewardedAd: Ad was not loaded , we can't show")
            toast("Ad was not loaded. Perform task e.g start activity")
        }
    }


    private fun loadAndShowRewardedAd(){
        //show progress dialog
        val alertDialog = AlertDialog.Builder(this).setCancelable(false).setMessage("Loading").show()
        //load  Rewarded Interstitial ad and if exits , assign value
        //AdRequest to load interstitial Ad
        val adRequest = AdRequest.Builder().build()
        //load  Rewarded  ad and if exits , assign value
        RewardedAd.load(
            this,
            resources.getString(R.string.rewarded_ad_id_test),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    super.onAdFailedToLoad(loadError)
                    mRewardedAd = null
                    alertDialog.dismiss()
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd=rewardedAd
                    showRewardedAd()
                    alertDialog.dismiss()
                }

            })
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}