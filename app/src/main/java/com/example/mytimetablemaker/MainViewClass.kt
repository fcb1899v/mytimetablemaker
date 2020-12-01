package com.example.mytimetablemaker

import android.text.format.DateFormat.getBestDateTimePattern
import android.widget.Button
import androidx.preference.PreferenceManager
import com.example.mytimetablemaker.Application.Companion.context
import java.text.SimpleDateFormat
import java.util.*

class MainView {

    //　日時をローカライズ表示のStringで取得する関数
    fun localizeDateString(date: Date, skeleton: String): String {
        val local: Locale = Locale.getDefault()
        val format: String = getBestDateTimePattern(local, skeleton)
        val dateFormat = SimpleDateFormat(format, local)
        return dateFormat.format(date)
    }

    //　日時をローカライズ表示のDateで取得する関数
    fun getLocalizeDate(date: String, skeleton: String): Date? {
        val local: Locale = Locale.getDefault()
        val format: String = getBestDateTimePattern(local, skeleton)
        val dateFormat = SimpleDateFormat(format, local)
        return dateFormat.parse(date)
    }

    //開始ボタンおよび停止ボタンを押したときの表示変更をする関数
    fun changeStartStop(button1: Button, button2: Button, timeflag: Boolean): Boolean {
        button1.setTextColor(R.string.coloraccent.setColor)
        button2.setTextColor(R.string.lightgray.setColor)
        return !timeflag
    }
    //帰宅ボタンおよび外出ボタンを押したときの表示変更をする関数
    fun changeGoBack(button1: Button, button2: Button, timeflag: Boolean): Boolean {
        button1.setTextColor(R.string.primarydark.setColor)
        button2.setTextColor(R.string.lightgray.setColor)
        return !timeflag
    }

    //MainActivityの表示された曜日をInt型で取得(日：0、月：1、火：2、水：3、木：4、金：5、土：6)
    //fun getDayOfWeekInt(displaydate: String): Int {
    //祝日
    //val gcalendar = CalendarApp.getCalenderById("ja.japanese#holiday@group.v.calender.google.com")
    //if (gcalender.getEventsForDay(date).length > 0) { return 7 }　//祝：7
    //}

    //設定画面のルート2を表示するSwitchPreferenceの状態に応じたBooleanを読み出す関数
    fun getRoot2Boolean(key: String, defaultflag: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultflag)
    }

    fun getDepartStation (goorback: String, changeline: Int): Array<String> {
        var departstation: Array<String> = arrayOf()
        (0 .. changeline).forEach { i: Int ->
            departstation += goorback.departStation(i, "${R.string.depsta.strings}${i + 1}")
        }
        return departstation
    }

    fun getArriveStation (goorback: String, changeline: Int): Array<String> {
        var arrivestation: Array<String> = arrayOf()
        (0 .. changeline).forEach { i: Int ->
            arrivestation += goorback.arriveStation(i, "${R.string.arrsta.strings}${i + 1}")
        }
        return arrivestation
    }

    fun getLineName (goorback: String, changeline: Int): Array<String> {
        var linename: Array<String> = arrayOf()
        (0 .. changeline).forEach { i: Int ->
            linename += goorback.lineName(i, "${R.string.line.strings}${i + 1}")
        }
        return linename
    }

    fun getLineColor (goorback: String, changeline: Int): Array<String> {
        var linecolor: Array<String> = arrayOf()
        (0 .. changeline).forEach { i: Int ->
            linecolor += goorback.lineColor(i, R.string.coloraccent.strings)
        }
        return linecolor
    }

    fun getTransportation (goorback: String, changeline: Int): Array<String> {
        var transportation: Array<String> = arrayOf()
        (0 .. changeline + 1).forEach { i: Int ->
            transportation += goorback.transportation(i, R.string.walking.strings)
        }
        return transportation
    }

    fun getRideTime (goorback: String, changeline: Int): Array<String> {
        var ridetime: Array<String> = arrayOf()
        (0 .. changeline).forEach { i: Int ->
            ridetime += goorback.rideTime(i, "0")
        }
        return ridetime
    }

    fun getTransitTime (goorback: String, changeline: Int): Array<String> {
        var transittime: Array<String> = arrayOf()
        (0 .. changeline + 1).forEach { i: Int ->
            transittime += goorback.transitTime(i, "0")
        }
        return transittime
    }
}