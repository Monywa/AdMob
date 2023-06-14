package com.monywastudent.admob

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

/* Adapter class for recyclerview */
class AdapterProduct(
    val context: Context,
    val productArrayList: ArrayList<ModelProduct>
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        //TAG
        const val TAG="PRODUCT_ADAPTER"

        //There will be 2 view types 1 for the actual content and 2 for Native Ad
        private const val VIEW_TYPE_CONTENT=0
        private const val VIEW_TYPE_AD=1
    }


    //ViewHolder class for row_product_item.xml
    inner class HolderProduct(itemView: View): RecyclerView.ViewHolder(itemView){

        //init View
        val iconView: ImageView=itemView.findViewById(R.id.icon_iv)
        val titleTv: TextView =itemView.findViewById(R.id.title_tv)
        val ratingBar:RatingBar=itemView.findViewById(R.id.rating_bar)
        val descriptionTv:TextView=itemView.findViewById(R.id.description_tv)


        fun bind(model:ModelProduct){
            titleTv.text=model.title
            descriptionTv.text=model.description
            ratingBar.rating=model.rating

            //handle item click
            itemView.setOnClickListener{
                Toast.makeText(context,"${model.title}",Toast.LENGTH_SHORT).show()
            }

        }

    }

    //ViewHolder class for row_native_ad_item.xml
    inner class HolderNativeAd(itemView: View): RecyclerView.ViewHolder(itemView){

        //init View
        val adAppIcon:ImageView=itemView.findViewById(R.id.ad_app_icon)
        val adHeadline:TextView=itemView.findViewById(R.id.ad_headline)
        val adAdvertiser:TextView=itemView.findViewById(R.id.ad_advertiser)
        val adStars:RatingBar=itemView.findViewById(R.id.ad_stars)
        val adBody:TextView=itemView.findViewById(R.id.ad_body)
        val mediaView:MediaView=itemView.findViewById(R.id.media_view)
        val adPrice:TextView=itemView.findViewById(R.id.ad_price)
        val adStore:TextView=itemView.findViewById(R.id.ad_store)
        val adCallToAction: Button =itemView.findViewById(R.id.ad_call_to_action)
        val nativeAdView:NativeAdView=itemView.findViewById(R.id.native_ad_view)

        fun bind(nativeAd:NativeAd){
            /*-----some assets aren't guaranteed to be in every NativeAd, so we need to check before displaying item */
            if(nativeAd.headline==null){
                //no content, hide view
                adHeadline.visibility=View.INVISIBLE
            }else {
                //have content, show view
                adHeadline.visibility=View.VISIBLE
                //set data
                adHeadline.text=nativeAd.headline
            }

            viewVisibility(nativeAd.body,adBody)
            viewVisibility(nativeAd.icon?.drawable,adAppIcon)
            viewVisibility(nativeAd.starRating,adStars)
            viewVisibility(nativeAd.price,adPrice)
            viewVisibility(nativeAd.store,adStore)
            viewVisibility(nativeAd.advertiser,adAdvertiser)
            viewVisibility(nativeAd.mediaContent,mediaView)
//            viewVisibility(nativeAd.callToAction,adCallToAction)


            if(nativeAd.callToAction==null){
                adCallToAction.visibility=View.INVISIBLE
            }else {
                adCallToAction.visibility=View.VISIBLE
                adCallToAction.text=nativeAd.callToAction
                nativeAdView.callToActionView=adCallToAction
            }




            //add native Ad the NativeAdView
            nativeAdView.setNativeAd(nativeAd)


        }

        private fun viewVisibility(content:Any?,view:View){
            if(content==null){
                view.visibility=View.INVISIBLE
            }else{
                view.visibility=View.VISIBLE
                when(view){
                    is TextView -> view.text=content as String
                    is ImageView -> view.setImageDrawable(content as Drawable)
                    is RatingBar -> view.rating=(content as Double).toFloat()
                    is MediaView -> {
                        view.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        view.mediaContent=content as MediaContent
                    }
                    is Button->{view.text=content as String
                        nativeAdView.callToActionView=view
                    }


                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view:View
        return if(viewType== VIEW_TYPE_CONTENT){
            //inflate/return view row_product_item.xml
            view=LayoutInflater.from(context).inflate(R.layout.row_product_item,parent,false)
            HolderProduct(view)
        }else{
            //inflate/return view row_native-ad.xml
            view=LayoutInflater.from(context).inflate(R.layout.row_native_ad_item,parent,false)
            HolderNativeAd(view)
        }
    }

    override fun getItemCount(): Int {
       return productArrayList.size //return items list size || number of records
    }

    override fun getItemViewType(position: Int): Int {
        //logic to display native Ad between content
        return if(position%5==0){
            //return the view type VIEW_TYPE_AD to show native ad after 5 times
            VIEW_TYPE_AD
        }else {
            //return the view type VIEW_TYPE_CONTENT to show content
            VIEW_TYPE_CONTENT
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      // Manage data of products and Native AD
        if(getItemViewType(position)== VIEW_TYPE_CONTENT){
            // view type is content/product, show products details

            val model=productArrayList[position]

            // instance of our HolderProduct to access UI Views of row_product_item.xml
            (holder as HolderProduct).bind(model)

        }else if (getItemViewType(position)== VIEW_TYPE_AD){
            //view type is native Ad, show Ad details

            //load Ad

            val adLoader=AdLoader.Builder(context,context.getString(R.string.native_ad_id_test))
                .forNativeAd {nativeAd->
                Log.d(TAG,"onNativeAdLoaded:")

                //Ad is loaded, show it

                // instance of our HolderProduct to access UI Views of row_native_ad_item.xml

                (holder as HolderNativeAd).bind(nativeAd)
            }.withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d(TAG,"onAdClicked:")

                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.d(TAG,"onAdClosed:")

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d(TAG,"onAdFailedToLoad:${loadAdError.message}")

                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.d(TAG,"onAdImpression:")

                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG,"onAdLoaded:")

                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    Log.d(TAG,"onAdOpened:")

                }

                override fun onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked()
                    Log.d(TAG,"onAdSwipeGestureClicked:")

                }

            }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }
}