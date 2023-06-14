package com.monywastudent.admob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class NativeAdActivity : AppCompatActivity() {
    companion object{
        //TAG for debugging
        const val TAG="NATIVE_AD_TAG"
    }
    private val productRv: RecyclerView by lazy {
        findViewById(R.id.products_rv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_ad)

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

        loadProducts()

    }

    private fun loadProducts(){
        val arrayList= arrayListOf<ModelProduct>()
        for(i in 1..50){
            val model=ModelProduct(R.drawable.ic_android_black,"title$i","description$i",3.5f)
            arrayList.add(model)
        }
        val adapter = AdapterProduct(this,arrayList)
        productRv.adapter=adapter

    }
}