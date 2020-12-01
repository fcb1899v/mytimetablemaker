package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class MainFragment {

    //クラスの呼び出し
    private val mainview = MainView()
    private val mainviewdialog = MainViewDialog()

    fun mainFun(
        context: Context, currentday: Int, currenttime: Int, goorback: String,
        departurepointview: TextView, departstationview: Array<TextView>, arrivestationview: Array<TextView>,
        arrivalpointview: TextView, linenameview: Array<TextView>, linelineview: Array<View>,
        transportview: Array<TextView>, transitlineview: Array<View>, layoutview: Array<LinearLayout>,
        timeview: Array<TextView>, countdownview: TextView) {

        //＜設定データの取得・表示＞
        //乗換回数・出発地・目的地・乗車駅・降車駅・路線名・各路線カラー・移動手段の取得
        val changeline: Int = goorback.changeLine
        val departpoint: String = goorback.departPoint(R.string.office.strings, R.string.home.strings)
        val arrivalpoint: String = goorback.arrivePoint(R.string.office.strings, R.string.home.strings)
        val departstation: Array<String> = mainview.getDepartStation(goorback, changeline)
        val arrivestation: Array<String> = mainview.getArriveStation(goorback, changeline)
        val linename: Array<String> = mainview.getLineName(goorback, changeline)
        val linecolor: Array<String> = mainview.getLineColor(goorback, changeline)
        val transportation: Array<String> = mainview.getTransportation(goorback, changeline)

        //出発地・目的地・乗車駅・降車駅・路線名・各路線カラー・移動手段の表示
        departurepointview.text = departpoint
        arrivalpointview.text = arrivalpoint
        transportview[0].text = transportation[0]
        for (i: Int in 0..changeline) {
            departstationview[i].text = if (changeline >= i) {departstation[i]} else {""}
            arrivestationview[i].text = if (changeline >= i) {arrivestation[i]} else {""}
            linenameview[i].text = if (changeline >= i) {linename[i]} else {""}
            linenameview[i].setTextColor(parseColor(linecolor[i]))
            linelineview[i].setBackgroundColor(parseColor(linecolor[i]))
            transportview[i + 1].text = if (changeline >= i) {transportation[i + 1]} else {""}
        }

        //＜各時刻の計算＞
        val calctime = CalcTime(goorback, changeline, currenttime, currentday)
        //乗車可能時刻[0]・各発車時刻[1]・各到着時刻[2]
        val time: Array<String> = calctime.getDisplayTimeArray

        //出発時刻、到着時刻、発車時刻、降車時刻の表示
        timeview[0].text = time[0]
        for (i: Int in 0..changeline) {
            timeview[2 * i + 2].text = if (timeview[2 * i + 1].text == "--:--") {"--:--"} else {time[2 * i + 2]}
            timeview[2 * i + 3].text = if (timeview[2 * i + 2].text == "--:--") {"--:--"} else {time[2 * i + 3]}
            timeview[1].text = if (timeview[2 * i + 3].text == "--:--") {"--:--"} else {time[1]}
        }
        //
        layoutview[0].visibility = if (changeline > 0) {View.VISIBLE} else {View.GONE}
        layoutview[1].visibility = if (changeline > 1) {View.VISIBLE} else {View.GONE}

        //出発までのカウントダウンの表示
        countdownview.text = calctime.getCountdownTime
        countdownview.setTextColor(calctime.getCountDownColor)

        //出発地名の設定
        departurepointview.setOnClickListener {
            mainviewdialog.setDeparturePointDialog(departurepointview, context, goorback)
        }
        //目的地名の設定
        arrivalpointview.setOnClickListener {
            mainviewdialog.setArrivalPointDialog(arrivalpointview, context, goorback)
        }

        val intentarray: Array<Intent> = changeTimetableActivity(context, goorback, currentday)

        for (i: Int in 0..changeline) {
            //乗車駅名の設定
            departstationview[i].setOnClickListener {
                mainviewdialog.setDepartStationDialog(departstationview[i], context, goorback, i)
            }
            //降車駅名の設定
            arrivestationview[i].setOnClickListener {
                mainviewdialog.setArriveStationDialog(arrivestationview[i], context, goorback, i)
            }
            //路線名の設定
            linenameview[i].setOnClickListener {
                mainviewdialog.setLineNameDialog(linenameview[i], linelineview[i], context, goorback, i)
            }
            //乗車時間の設定
            linelineview[i].setOnClickListener {
                mainviewdialog.setRideTimeDialog(context, goorback, i, intentarray[i])
            }
            //移動手段の設定
            transportview[i + 1].setOnClickListener {
                mainviewdialog.setTransportationDialog(transportview[i + 1], context, goorback, i + 1)
            }
            //乗換時間の設定
            transitlineview[i + 1].setOnClickListener {
                mainviewdialog.setTransitTimeDialog(context, goorback, i + 1)
            }
        }
        //移動手段の設定
        transportview[0].setOnClickListener {
            mainviewdialog.setTransportationDialog(transportview[0], context, goorback, 0)
        }
        //乗換時間の設定
        transitlineview[0].setOnClickListener {
            mainviewdialog.setTransitTimeDialog(context, goorback, 0)
        }
    }

    companion object {
        private fun changeTimetableActivity(context: Context, goorback: String, currentday: Int): Array<Intent> {

            //乗換回数
            val changeline: Int = goorback.changeLine
            //路線番号
            val linenumber: Array<Int> = Array(changeline + 1){it}

            //時刻表アクティビティにデータを送るためのキー
            val intentgoorback = "goorback"
            val intentlinenumber = "linenumber"
            val intentcurrentday = "currentday"

            var intentarray: Array<Intent> = arrayOf()
            for (i: Int in 0..changeline) {
                intentarray += Intent(context, TimetableActivity::class.java).apply {
                    putExtra(intentgoorback, goorback)
                    putExtra(intentlinenumber, linenumber[i])
                    putExtra(intentcurrentday, currentday)
                }
            }
            return intentarray
        }
    }
}