package com.example.mytimetablemaker

import android.graphics.Color.*

class CalcTime(
        val goorback: String,
        private val changeline: Int,
        private val currenttime: Int,
        val currentday: Int
    ) {

    //ルート内の各路線の乗車可能時刻[0]・発車時刻[1]・到着時刻[2]を取得する関数
    val getDisplayTimeArray: Array<String> get() {
        var displaytimearray: Array<String> = arrayOf()
        //出発時刻
        val departtime: Int = timeArray[0][1].minusHHMM(transitTimeArray[1])
        //到着時刻
        val hometime: Int = timeArray[changeline][2].plusHHMM(transitTimeArray[0])
        //＜フラグメントの表示＞
        //出発時刻、到着時刻、発車時刻、降車時刻
        displaytimearray += departtime.stringTime
        displaytimearray += hometime.stringTime
        for (i: Int in 0..changeline) {
            if (changeline >= i) {
                displaytimearray += timeArray[i][1].stringTime
                displaytimearray += timeArray[i][2].stringTime
            }
        }
        return displaytimearray
    }

    private fun getTimetable(i: Int): Array<Int> {
        var timetable: Array<Int> = arrayOf()
        for (hour: Int in 4..25) {
            timetable += goorback.timeTableArrayInt(i, hour, currentday)
        }
        return timetable
    }

    //内部ストレージに保存された各時刻表データを配列として取得する関数
    private val timetableArray: Array<Array<Int>> get() {
        var timetablearray: Array<Array<Int>> = arrayOf()
        for (i: Int in 0..changeline) {
            timetablearray += getTimetable(i)
        }
        return timetablearray
    }

    private val transitTimeArray: Array<Int> get() {
        var transittimearray: Array<Int> = arrayOf()
        for (i: Int in 0..changeline + 1) {
            transittimearray += goorback.transitTimeInt(i)
        }
        return  transittimearray
    }

    private val rideTimeArray: Array<Int> get() {
        var ridetimearray: Array<Int> = arrayOf()
        for (i: Int in 0..changeline) {
            ridetimearray += goorback.rideTimeInt(i)
        }
        return ridetimearray
    }

    //ルート内の各路線の乗車可能時刻[0]・発車時刻[1]・到着時刻[2]を取得する関数
    private val timeArray: Array<Array<Int>> get() {
        val timearray: Array<Array<Int>> = Array(changeline + 1) { Array(3) { 0 } }
        //路線1の乗車可能時刻・発車時刻・到着時刻を取得
        timearray[0][0] = (currenttime / 100).plusHHMM(transitTimeArray[1])
        timearray[0][1] = getNextStartTime(timearray[0][0], timetableArray[0])
        timearray[0][2] = timearray[0][1].plusHHMM(rideTimeArray[0])

        //路線1以降の乗車可能時刻・発車時刻・到着時刻を取得
        if (changeline > 0) {
            for (i: Int in 1..changeline) {
                timearray[i][0] = timearray[i - 1][2].plusHHMM(transitTimeArray[i + 1])
                timearray[i][1] = getNextStartTime(timearray[i][0], timetableArray[i])
                timearray[i][2] = timearray[i][1].plusHHMM(rideTimeArray[i])
            }
        }
        return timearray
    }

    //発車時刻を取得する関数
    private fun getNextStartTime(possibletime: Int, eachtimetable: Array<Int>): Int {
        return try {
            var nextstarttime: Int
            var i = 0
            loop@do {
                if (i >= eachtimetable.size) {
                    nextstarttime = eachtimetable[0]
                    break@loop
                }
                nextstarttime = eachtimetable[i]
                i++
            } while (possibletime > nextstarttime)
            nextstarttime
        } catch (e: ArrayIndexOutOfBoundsException) {
            9999
        }
    }

    //カウントダウン時間（mm:ss）を取得する関数
    val getCountdownTime: String get() {
        //出発時刻
        val departureTime: Int = timeArray[0][1].minusHHMM(transitTimeArray[1])
        //カウントダウン（出発時刻と現在時刻の差）を計算
        var countdowntime: Int = (departureTime * 100).minusHHMMSS(currenttime).HHMMSStoMMSS
        countdowntime = if (countdowntime in 0..9999) countdowntime else -1000000
        val countdownmm: String = (countdowntime / 100).addZeroTime
        val countdownss: String = (countdowntime % 100).addZeroTime
        return if (countdowntime == -1000000) "--:--"  else "$countdownmm:$countdownss"
    }

    //カウントダウン表示の警告色を取得する関数
    val getCountDownColor: Int get() {
        //出発時刻
        val departureTime: Int = timeArray[0][1].minusHHMM(transitTimeArray[1])
        val countdownmm: Int = departureTime.minusHHMM(currenttime / 100)
        return if (currenttime % 2 == 1) {
            LTGRAY
        } else {
            when (countdownmm) {
                in 11..99 -> { R.string.coloraccent.setColor }
                in 6..10 -> { YELLOW }
                in 0..5 -> { RED }
                else -> { LTGRAY }
            }
        }
    }
}