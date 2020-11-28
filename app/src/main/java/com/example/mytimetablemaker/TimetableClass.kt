package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Timetable {

    private val setting = Setting()

    //全時刻のPreferenceに保存されている発車時刻の文字列を配列で取得する関数
    fun getTimetableStringArray(goorback: String, i: Int, currentday: Int): Array<String> {
        var timetablearray: Array<String> = arrayOf()
        for (hour: Int in 4..25) {
            timetablearray += goorback.timeTableString(i, hour, currentday)
        }
        return timetablearray
    }

    //平日と土日の切替表示
    fun getTimetableText (textview: TextView, currentday: Int) {
        textview.apply {
            text = currentday.dayOrEndString(R.string.weekend.strings, R.string.weekday.strings)
            setTextColor(currentday.dayOrEndColor(RED, WHITE))
        }
    }
    //平日と土日のボタン切替表示
    fun getTimetableButton (button: Button, currentday: Int) {
        button.apply {
            text = currentday.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings)
            setTextColor(currentday.dayOrEndColor(R.string.primarydark.setColor, RED))
        }
    }

    //時刻表に2桁の数字を入力するDialogの表示する関数
    fun makeTimetableDialog (textview: TextView, context: Context, goorback: String, linenumber: Int,
                             hour: Int, currentday: Int, textviewarray: Array<TextView?>) {
        val edittext = EditText(context)
        val title1: String = R.string.inputtime.strings
        val title2: String = R.string.copytime.strings
        val key = "${goorback}line${linenumber + 1}h${hour.addZeroTime}${currentday.addWeekend}setting"
        val hint: String = R.string.inputtimehint.strings
        val currenttext: String = textview.text.toString()
        val arraylist: Array<String> = timetableListArray(hour, currentday)
        val arrayvalue: Array<String> = timetableValueArray(goorback, linenumber, hour, currentday)
        setting.setEditNumber2(edittext, hint)
        AlertDialog.Builder(context).apply {
            setTitle(title1)
            setView(edittext)
            setNegativeButton(R.string.add) { _, _ ->
                setting.prefSaveText(context, key, currenttext.addTime(edittext.text.toString()))
                textview.text = currenttext.addTime(edittext.text.toString())
                makeTimetableDialog (textview, context, goorback, linenumber, hour, currentday, textviewarray)
            }
            setPositiveButton(R.string.delete) { _, _ ->
                setting.prefSaveText(context, key, currenttext.deleteTime(edittext.text.toString()))
                textview.text = currenttext.deleteTime(edittext.text.toString())
                makeTimetableDialog (textview, context, goorback, linenumber, hour, currentday, textviewarray)
            }
            setNeutralButton(R.string.copytime) { _, _ ->
                setChoiceListItemsDialog(textview, context, key, title2, arraylist, arrayvalue, textviewarray, goorback, linenumber, currentday)
            }
            show()
        }
    }

    //★リストを選択するDialogを表示する関数
    private fun setChoiceListItemsDialog(textview: TextView, context: Context, key: String, title: String,
                                         arraylist: Array<String>, arrayvalue: Array<String>, textviewarray: Array<TextView?>,
                                         goorback: String, linenumber: Int, currentday: Int) {
        var selectnumber = 0
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraylist, 0) { _, which ->
                selectnumber = which
                setting.prefSaveText(context, key, arrayvalue[which])
                textview.text = arrayvalue[which]
            }
            setNegativeButton(R.string.cancel) { _, _ ->
            }
            setPositiveButton(R.string.ok) { _, _ ->
                if (selectnumber in 2..5) {
                    allCopyTimetableDialog(textviewarray, context, selectnumber, goorback, linenumber, currentday)
                }
            }
            show()
        }
    }

    private fun allCopyTimetableDialog(textviewarray: Array<TextView?>, context: Context, selectnumber: Int,
                                       goorback: String, linenumber: Int, currentday: Int) {
        val title: String = R.string.copyalltime.strings
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setNegativeButton(R.string.yes) { _, _ ->
                for (hour in 4..25) {
                    val selectkey: String = goorback.timeTableKey(linenumber, hour, currentday)
                    val copykey: String = allCopyKey(goorback, linenumber, hour, currentday, selectnumber)
                    setting.prefSaveText(context, selectkey, copykey.savedText(""))
                    textviewarray[hour - 4]!!.text = copykey.savedText("")
                }
            }
            setPositiveButton(R.string.no) { _, _ ->
            }
            show()
        }
    }

    private fun timetableListArray(hour: Int, currentday: Int): Array<String> = arrayOf(
        "${hour - 1}${R.string.jidai.strings}",
        "${hour + 1}${R.string.jidai.strings}",
        currentday.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings),
        R.string.otherrouteline.strings + "1",
        R.string.otherrouteline.strings + "2",
        R.string.otherrouteline.strings + "3"
    )

    private fun timetableValueArray(goorback: String, linenumber: Int, hour: Int, currentday: Int): Array<String> =arrayOf(
        goorback.timeTableString(linenumber, hour - 1, currentday),
        goorback.timeTableString(linenumber, hour + 1, currentday),
        goorback.timeTableString(linenumber, hour, currentday.otherDay),
        goorback.otherGoOrBack.timeTableString(0, hour, currentday),
        goorback.otherGoOrBack.timeTableString(1, hour, currentday),
        goorback.otherGoOrBack.timeTableString(2, hour, currentday)
    )

    private fun allCopyKey(goorback: String, linenumber: Int, hour: Int, currentday: Int, selectnumber: Int): String = when(selectnumber){
        3 -> goorback.otherGoOrBack.timeTableKey(0, hour, currentday)
        4 -> goorback.otherGoOrBack.timeTableKey(1, hour, currentday)
        5 -> goorback.otherGoOrBack.timeTableKey(2, hour, currentday)
        else -> goorback.timeTableKey(linenumber, hour, currentday.otherDay)
    }
}