package com.example.mytimetablemaker

import android.content.Context
import android.graphics.Color.parseColor
import androidx.preference.PreferenceManager
import com.example.mytimetablemaker.Application.Companion.context

//Resourcesファイルをどこでも参照できるようにするクラス
class Application: android.app.Application() {
    companion object { lateinit var  context: Context }
    override fun onCreate() {
        super.onCreate()
        context = this
    }
}
//
val Int.strings: String get() = context.getString(this)
//
val Int.arrayStrings: Array<String> get() = context.resources.getStringArray(this)
//
val Int.setColor: Int get() = parseColor(this.strings)

//SharedPreferenceに保存された値を取得:key
fun String.savedText(defaulttext: String): String = when (val edittext: String? =
    PreferenceManager.getDefaultSharedPreferences(context).getString(this, defaulttext)) {
    "", "未設定", "Not set", null -> defaulttext
    else -> edittext.toString()
}
//SharedPreferenceに保存された値を取得:key
val String.savedInt: Int get() = when (val edittextintornull: Int? =
    PreferenceManager.getDefaultSharedPreferences(context).getString(this, "0")?.toIntOrNull()) {
    null -> 0
    else -> edittextintornull
}

//乗換回数:goorback
val String.changeLine: Int get() = "${this}changeline".savedText("0").toInt()
//乗換回数の表示:goorback
val String.changeLineString: String get() = when (this.changeLine) {
    1 -> R.string.once.strings
    2 -> R.string.twice.strings
    else -> R.string.zero.strings
}
//出発地を取得:goorback
fun String.departPoint(defaultoffice: String, defaulthome: String): String =
        this.goOrBackString("destination".savedText(defaultoffice), "departurepoint".savedText(defaulthome))
//目的地を取得:goorback
fun String.arrivePoint(defaultoffice: String, defaulthome: String): String =
        this.goOrBackString("departurepoint".savedText(defaultoffice), "destination".savedText(defaulthome))
//乗車駅名を取得:goorback
fun String.departStation(i: Int, defaultdepsta: String): String =
        "${this}line${i + 1}departstation".savedText(defaultdepsta)
//降車駅名を取得:goorback
fun String.arriveStation(i: Int, defaultarrsta: String): String =
        "${this}line${i + 1}arrivestation".savedText(defaultarrsta)
//路線名を取得:goorback
fun String.lineName(i: Int, defaultline: String): String =
        "${this}line${i + 1}nameofline".savedText(defaultline)
//路線カラーを取得:goorback
fun String.lineColor(i: Int, defaultcolor: String): String =
        "${this}line${i + 1}colorofline".savedText(defaultcolor)
//乗車時間を取得:goorback
fun String.rideTime(i: Int, defaulttime: String): String =
        "${this}line${i + 1}ridetime".savedText(defaulttime)
//乗車時間を取得(Int型):goorback
fun String.rideTimeInt(i: Int): Int =
        "${this}line${i + 1}ridetime".savedInt
//乗換手段を取得:goorback
fun String.transportation(i: Int, defaulttrans: String): String =
        "${this}transportation${i.e}".savedText(defaulttrans)
//乗換時間を取得:goorback
fun String.transitTime(i: Int, defaulttime: String): String =
        "${this}transittime${i.e}".savedText(defaulttime)
//乗換時間を取得(Int型):goorback
fun String.transitTimeInt(i: Int): Int =
        "${this}transittime${i.e}".savedInt

//:goorback
fun String.goOrBackString(backstring: String, gostring: String) = when (this) {
    "back1", "back2" -> backstring
    else -> gostring
}
//:goorback
val String.otherGoOrBack: String get() = when (this) {
    "go1" -> "go2"
    "back2" -> "back1"
    "go2" -> "go1"
    else -> "back2"
}
//:goorback
fun String.transitStation(i: Int): String = when (i) {
    0 -> this.arrivePoint(R.string.office.strings, R.string.home.strings)
    1 -> this.departPoint(R.string.office.strings, R.string.home.strings)
    else -> this.arriveStation(i - 2, "${R.string.arrsta.strings}${i - 1}")
}
//
fun String.changeWord(changeword: String, i: Int):String = when (i) {0 -> this else -> changeword }

//
val Int.e:String get() = when (this) {
    0-> "e"
    else -> "$this"
}
//[分]の単位を追加
val String.addMinites: String get() = when (this) {
    "", R.string.notset.strings -> this
    else -> "$this ${R.string.minutesunit.strings}"
}
//各種設定のタイトルを取得
val String.variousSettingsTitle: String get() = "${R.string.varioussettings.strings} " + when (this) {
    "go1" -> R.string.go1route.strings
    "back2" -> R.string.back2route.strings
    "go2" -> R.string.go2route.strings
    else -> R.string.back1route.strings
}
