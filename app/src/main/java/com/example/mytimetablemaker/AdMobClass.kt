package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

@Suppress("DEPRECATION")
class AdMobClass {

    private val adappid: String = "ca-app-pub-1585283309075901~2388370986"

    @SuppressLint("MissingPermission")
    fun setAdMob(adview: AdView, context: Context) {
        //Admob広告
        MobileAds.initialize(context, adappid)
        val adrequest: AdRequest = AdRequest.Builder().build()
        adview.loadAd(adrequest)

        adview.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                Log.d("debug", "Code to be executed when an ad finishes loading.")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("debug", "Code to be executed when an ad request fails.")
            }

            override fun onAdOpened() {
                Log.d("debug", "Code to be executed when an ad opens an overlay that covers the screen.")
            }

            override fun onAdLeftApplication() {
                Log.d("debug", "Code to be executed when the user has left the app.")
            }

            override fun onAdClosed() {
                Log.d("debug", "Code to be executed when the user is about to return to the app after tapping on an ad.")
            }
        })
    }
}