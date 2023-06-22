package com.monywastudent.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

/** Application class that initializes, loads and show ads when activities change states. */

private const val LOG_TAG = "AppOpenAdManager"

class MyApplication : Application() ,Application.ActivityLifecycleCallbacks,DefaultLifecycleObserver{

    private var currentActivity:Activity?=null

    private var appOpenAdManager:AppOpenAdManager?=null


    /** Interface definition for a callback to be invoked when an app open ad is complete. */
    interface OnShowAdCompleteListener{
        fun onShowAdComplete()
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
       Log.d(LOG_TAG,"onActivityCreated:")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(LOG_TAG,"onActivityStarted:")
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one shows the ad.
        if(!(AppOpenAdManager().isShowingAd)){
            currentActivity=activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(LOG_TAG,"onActivityResumed:")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(LOG_TAG,"onActivityPaused:")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(LOG_TAG,"onActivityStopped:")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(LOG_TAG,"onActivitySaveInstanceState:")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(LOG_TAG,"onActivityDestroyed:")
    }

    /** Inner class that loads and shows app open ads. */
    private inner class AppOpenAdManager {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        private var loadTime:Long=0L

        /** Request an ad. */
        fun loadAd(context: Context) {

            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return
            }
            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                context.getString(R.string.app_open_ad_id_test),
                request,
                object : AppOpenAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        // Called when an app open ad has failed to load.
                        Log.d(LOG_TAG, loadAdError.message)
                        isLoadingAd = false;

                        Toast.makeText(context,"Failed to load ad due to ${loadAdError.message}",Toast.LENGTH_SHORT).show()

                    }

                    override fun onAdLoaded(ad: AppOpenAd) {
                        super.onAdLoaded(ad)
                        // Called when an app open ad has loaded.
                        Log.d(LOG_TAG, "Ad was loaded.")

                        appOpenAd=ad
                        isLoadingAd=false
                        loadTime= Date().time
                        Toast.makeText(context,"Ad loaded ...",Toast.LENGTH_SHORT).show()
                    }

                })

        }

        /*
        Check if ad was loaded more than n hours ago
         */
        private fun wasLoadLessThanNHoursAgo(numHours:Long):Boolean{
            val dateTimeDifference=Date().time - loadTime
            val numMilliSecondsPerHour=3600000
            return dateTimeDifference < numHours * numMilliSecondsPerHour
        }

        /** Check if ad exists and can be shown. */
        fun isAdAvailable(): Boolean {
            // Ad references in the app open beta will time out after four hours, but the time limit
            //may change in the future beta versions
            return appOpenAd != null && wasLoadLessThanNHoursAgo(4)
        }

        /** Shows the ad if one isn't already showing.
         * @param activity the activity that shows the app open ad
         *
         * */
        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ){
            // If the app open ad is already showing, do not show the ad again.
            if(isShowingAd){
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }
            // If the app open ad is not available yet, invoke the callback then load the ad.
            if(!isAdAvailable()){
                Log.d(LOG_TAG, "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            appOpenAd?.fullScreenContentCallback=object:FullScreenContentCallback(){
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    // Called when full screen content is dismissed.
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd=null
                    isShowingAd=false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    // Called when fullscreen content failed to show.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d(LOG_TAG, adError.message)
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)

                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    // Called when fullscreen content is shown.
                    Log.d(LOG_TAG, "Ad showed fullscreen content.")

                }
            }
            isShowingAd=true
            appOpenAd?.show(activity)


        }
    }


    override fun onCreate() {
        super<Application>.onCreate()
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this) { status ->

        }

        appOpenAdManager=AppOpenAdManager()

//        ProcessLifecycleOwner.get().lifecycle.addObserver(this) this is deprecated
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground.(deprecated) */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onMoveToForeground(){
//        currentActivity?.let {currentActivity->
//            appOpenAdManager?.showAdIfAvailable(currentActivity,object :OnShowAdCompleteListener{
//                override fun onShowAdComplete() {
//
//                }
//            })
//        }
//    }
    fun showAdIfAvailable(activity:Activity,onShowAdCompleteListener: OnShowAdCompleteListener){
        appOpenAdManager?.showAdIfAvailable(activity,onShowAdCompleteListener)
    }

    override fun onStart(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onCreate(owner)

        currentActivity?.let {currentActivity->
            Log.d(LOG_TAG,"onLifecyclerOnStart:......")
            appOpenAdManager?.showAdIfAvailable(currentActivity,object :OnShowAdCompleteListener{
                override fun onShowAdComplete() {

                }
            })
        }
    }
}