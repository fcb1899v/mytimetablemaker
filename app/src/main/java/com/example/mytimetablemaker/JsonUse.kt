package com.example.mytimetablemaker

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class JsonUse {

    //Assetsフォルダ内のJSONファイルを内部ストレージのfilesにコピー
    val jsonfilearray: Array<String> = arrayOf(
        "setting.json",
        "back1timetable1.json", "back1timetable2.json", "back1timetable3.json",
        "back2timetable1.json", "back2timetable2.json", "back2timetable3.json",
        "go1timetable1.json", "go1timetable2.json", "go1timetable3.json",
        "go2timetable1.json", "go2timetable2.json", "go2timetable3.json"
    )

    //ファイルをコピー
    fun copyData(inputstream: InputStream, outputstream: OutputStream) {
        try {
            val buffer = ByteArray(1024)
            var length: Int?
            while (true) {
                length = inputstream.read(buffer)
                if (length <= 0)
                    break
                outputstream.write(buffer, 0, length)
            }
            outputstream.flush()
            outputstream.close()
            inputstream.close()
        } catch (e: IOException) {
            throw IOException()
        }
    }

    //指定のフォルダから指定のJSONファイルを読取する関数
    fun readJSONFile(folder: File, jsonfilename: String): String {
        val readjsonfile: String = File(folder, jsonfilename).bufferedReader().readText()
        File(folder, jsonfilename).bufferedReader().close()
        return readjsonfile
    }

    //指定のフォルダから複数の指定のJSONファイルを読取する関数
    fun readJSONFileArray(folder: File, jsonfilename: Array<String>): Array<String> {
        var readjsonfilearray: Array<String> = arrayOf()
        for (i: Int in 0 until jsonfilename.size) {
            readjsonfilearray += readJSONFile(folder, jsonfilename[i])
        }
        return readjsonfilearray
    }

    //使用するJSONファイルの配列を作成
    fun makeJSONFileArray(goorback: String, changeline: Int): Array<String> {
        var makejsonfilearray: Array<String> = arrayOf()
        for (i: Int in 0..changeline) {
            makejsonfilearray += goorback + "timetable" + (i + 1).toString() + ".json"
        }
        return makejsonfilearray
    }

    //JSONのkeyを指定してArray<Int>データを取得する関数
    fun getSettingIntArray(readjsonarray: Array<String>, key: String): Array<Int> {
        var settingintarray: Array<Int> = arrayOf()
        for (i: Int in 0 until readjsonarray.size) {
            settingintarray += JSONObject(readjsonarray[i]).getInt(key)
        }
        return settingintarray
    }

    //JSONのkeyを指定してArray<String>データを取得する関数
    fun getSettingStringArray(readjsonarray: Array<String>, key: String): Array<String> {
        var settingintarray: Array<String> = arrayOf()
        for (i: Int in 0 until readjsonarray.size) {
            settingintarray += JSONObject(readjsonarray[i]).getString(key)
        }
        return settingintarray
    }

    //JSOｎファイルから時刻データを取得
    private fun getTimeDataFromJson(
        readjsonfile: String,
        hour: String,
        currentday: Int
    ): Array<Int> {

        //土日祝
        val jsonarraydata: JSONArray = if (currentday == 0 || currentday == 6) {
            JSONObject(readjsonfile).getJSONObject("Weekend").getJSONArray(hour)
            //平日
        } else {
            JSONObject(readjsonfile).getJSONObject("Weekday").getJSONArray(hour)
        }
        var starttimearray: Array<Int> = arrayOf()

        return if (jsonarraydata.length() > 0) {
            for (i: Int in 0 until jsonarraydata.length()) {
                starttimearray += jsonarraydata.getInt(i)
            }
            starttimearray
        } else {
            arrayOf()
        }
    }

    //JSOｎファイルから取得した時刻データから時刻表を作成
    private fun getTimetableFromJson(jsonfilename: String, currentday: Int): Array<Int> {
        var timetable: Array<Int> = arrayOf()
        for (i: Int in 0..21) {
            timetable += getTimeDataFromJson(jsonfilename, (i + 4).toString(), currentday)
                .map { it }.takeWhile { it < 60 }.map { it + (i + 4) * 100 }.toTypedArray()
                .sortedArray()
        }
        return timetable
    }

    //ルート内の各路線の時刻表の配列を生成
    fun getTimetableArrayFromJson(
        readjsonarray: Array<String>,
        currentday: Int
    ): Array<Array<Int>> {
        var timetablearray: Array<Array<Int>> = arrayOf()
        for (i: Int in 0 until readjsonarray.size) {
            timetablearray += getTimetableFromJson(readjsonarray[i], currentday)
        }
        return timetablearray
    }

    //JSOｎファイルから乗換時間の配列を取得
    fun getWalkTimeArray(readjsonfile: String, key: String): Array<Int> {
        val jsonarraydata: JSONArray =
            JSONObject(readjsonfile).getJSONObject(key).getJSONArray("walktime")
        var walktimearray: Array<Int> = arrayOf()
        return if (jsonarraydata.length() > 0) {
            for (i: Int in 0 until jsonarraydata.length()) {
                walktimearray += jsonarraydata.getInt(i)
            }
            walktimearray
        } else {
            arrayOf()
        }
    }
}

/*
//＜時刻表に関するJSONファイルの取得＞
//設定用JSONファイルの読込み
val readsettingfile: String = fileanddata.readJSONFile(context!!.filesDir, "setting.json")
//読込みたいJSONファイルのリスト
val jsonfile: Array<String> = fileanddata.makeJSONFileArray(goorback, changeline)
//時刻表に関するJSONファイルの読込み
val readjsonfile: Array<String> = fileanddata.readJSONFileArray(context!!.filesDir, jsonfile)
//時刻表
val timetable: Array<Array<Int>> = dateandtime.getTimetableArrayFromJson(readjsonfile, currentday)
//乗換時間
val walktime: Array<Int> = dateandtime.getWalkTimeArray(readsettingfile, goorback)
//乗車時間
val ridetime: Array<Int> = fileanddata.getSettingIntArray(readjsonfile, "ridetime")

//    class MyPrefsBackupAgent: BackupAgentHelper() {
//        override fun onCreate() {
//            SharedPreferencesBackupHelper(this, "user_preference").also {
//                addHelper("prefs", it)
//            }
//        }
//    }

//    var inputstream: InputStream
//    var outputstream: OutputStream
//    for (i: Int in 0 until jsonfilearray.size) {
//        inputstream = assets.open(jsonfilearray[i])
//        outputstream = openFileOutput(jsonfilearray[i], Context.MODE_PRIVATE)
//        fileanddata.copyData(inputstream, outputstream)
//    }

//東海道線対応
val backtoukaidouridetime = 77 //東海道線の乗車時間
val backslasttime = 2255       //新幹線の終電時間
val backtlasttime = 2404       //東海道線の終電時間
if (changeline > 1) {
    if (time[2][0] in backslasttime + 1..backtlasttime) {
        time[2][2] += backtoukaidouridetime - ridetime[2]
    }
}

//東海道線対応
if (changeline > 1) {
    val toukaidoulinename = "JR東海道線"
    val toukaidoulinecolor = "#F68B1E"
    if (time[2][0] in backslasttime + 1..backtlasttime) {
        linename5.text = toukaidoulinename
        linename5.setTextColor(Color.parseColor(toukaidoulinecolor))
        lineline5.setBackgroundColor(Color.parseColor(toukaidoulinecolor))
    }
}

//＜時刻表に関するJSONファイルの取得＞
//設定用JSONファイルの読込み
val readsettingfile: String = fileanddata.readJSONFile(context!!.filesDir, "setting.json")
//読込みたいJSONファイルのリスト
val jsonfile: Array<String> = fileanddata.makeJSONFileArray(goorback, changeline)
//時刻表に関するJSONファイルの読込み
val readjsonfile: Array<String> = fileanddata.readJSONFileArray(context!!.filesDir, jsonfile)
//時刻表
val timetable: Array<Array<Int>> = dateandtime.getTimetableArrayFromJson(readjsonfile, currentday)
//乗換時間
val walktime: Array<Int> = dateandtime.getWalkTimeArray(readsettingfile, goorback)
//乗車時間
val ridetime: Array<Int> = fileanddata.getSettingIntArray(readjsonfile, "ridetime")

//東海道線対応
val backtoukaidouridetime = 77 //東海道線の乗車時間
val backslasttime = 2255       //新幹線の終電時間
val backtlasttime = 2404       //東海道線の終電時間
if (changeline > 1) {
    if (time[2][0] in backslasttime + 1..backtlasttime) {
        time[2][2] += backtoukaidouridetime - ridetime[2]
    }
}

//京浜東北線対応
if (changeline > 0) {
val yamanotekeihintohokulinename = "JR山手線/京浜東北線"
linename3!!.text = yamanotekeihintohokulinename
lineline3!!.setBackgroundResource(R.drawable.gradient_color)
}
//東海道線対応
if (changeline > 1) {
val toukaidoulinename = "JR東海道線"
val toukaidoulinecolor = "#F68B1E"
if (time[2][0] in backslasttime + 1..backtlasttime) {
linename5!!.text = toukaidoulinename
linename5.setTextColor(Color.parseColor(toukaidoulinecolor))
lineline5!!.setBackgroundColor(Color.parseColor(toukaidoulinecolor))
}
}
*/

