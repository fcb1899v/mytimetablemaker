package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import java.util.*
import java.util.Calendar.*

class MainViewDialog {

    private val mainview = MainView()
    private val setting = Setting()

    //＜日付の設定＞
    //(a)標準的なDatePickerDialogを表示する関数
    fun setDatePickerDialog(textview: TextView, context: Context) {
        val inputdate: Calendar = getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            inputdate.apply {
                set(DAY_OF_MONTH, day)
                set(MONTH, month)
                set(YEAR, year)
            }
            textview.text = mainview.localizeDateString(inputdate.time, "EEEMMMdyyyy")
        }
        DatePickerDialog(context, dateSetListener, inputdate.get(YEAR), inputdate.get(MONTH), inputdate.get(DAY_OF_MONTH))
            .show()
    }

    //＜日時の設定＞
    //(b)標準的なTimePickerDialogを表示する関数
    @SuppressLint("SetTextI18n")
    fun setTimePickerDialog(textview: TextView, context: Context) {
        val inputtime: Calendar = getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
            inputtime.apply {
                set(HOUR_OF_DAY, hour)
                set(MINUTE, minute)
            }
            textview.text = mainview.localizeDateString(inputtime.time, "HH:mm") + ":00"
        }
        TimePickerDialog(context, timeSetListener, inputtime.get(HOUR_OF_DAY), inputtime.get(MINUTE), true)
            .show()
    }

    //出発地名を設定するダイアログを表示する関数
    fun setDeparturePointDialog(textview: TextView, context: Context, goorback: String) {
        val edittext = EditText(context)
        val key: String = goorback.goOrBackString("destination", "departurepoint")
        val title: String = R.string.settingdepartplacetitle.strings
        val savedtext: String = goorback.departPoint("", "")
        edittext.setText(savedtext)
        setting.setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, context, title, key)
    }

    //発車駅名を設定するダイアログを表示する関数
    fun setDepartStationDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val key = "${goorback}departstation${i + 1}"
        val title = "${R.string.settingstationnametitle.strings}${R.string.departstation.strings}${i + 1}"
        val savedtext: String = goorback.departStation(i, "")
        edittext.setText(savedtext)
        setting.setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, context, title, key)
    }

    //降車駅名を取得するダイアログ
    fun setArriveStationDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val key = "${goorback}arrivalstation${i + 1}"
        val title = "${R.string.settingstationnametitle.strings}${R.string.arrivestation.strings}${i + 1}"
        val savedtext: String = goorback.arriveStation(i, "")
        edittext.setText(savedtext)
        setting.setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, context, title, key)
    }

    //目的地名を取得するダイアログ
    fun setArrivalPointDialog(textview: TextView, context: Context, goorback: String) {
        val edittext = EditText(context)
        val key: String = goorback.goOrBackString("departurepoint", "destination")
        val title: String = R.string.settingdestinationtitle.strings
        val savedtext: String = goorback.arrivePoint("", "")
        edittext.setText(savedtext)
        setting.setEditText20(edittext)
        setEditTextAlertDialog(textview, edittext, context, title, key)
    }

    //路線名を設定するDialogを表示する関数（Neutralボタンで路線カラー設定用Dialogを表示）
    fun setLineNameDialog(textview: TextView, view: View, context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val key1 = "${goorback}linename${i + 1}"
        val key2 = "${goorback}linecolor${i + 1}"
        val title1 = "${R.string.settinglinenametitle.strings}${R.string.line.strings}${i + 1}"
        val title2: String = R.string.settinglinecolortitle.strings
        val linecolorlist: Array<String> = R.array.linecolorlist.arrayStrings
        val linecolorvalue: Array<String> = R.array.linecolorvalue.arrayStrings
        val linename: String = goorback.lineName(i, "")
        edittext.setText(linename)
        setting.setEditText20(edittext)
        setEditTextPlusColorAlertDialog(textview, view, edittext, context, key1, key2, title1, title2, linecolorlist, linecolorvalue)
    }

    //移動手段を設定するDialogを表示する関数
    fun setTransportationDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val key = "${goorback}transportation${i.e}"
        val title: String = "${R.string.settingtransportationtitle.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goorback.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val transportationlist: Array<String> = R.array.transportationlist.arrayStrings
        setSingleChoiceItemsAlertDialog(textview, context, key, title, transportationlist, transportationlist)
    }

    //乗車時間を設定するDialogを表示する関数
    fun setRideTimeDialog(context: Context, goorback: String, i: Int, intent: Intent) {
        val key = "${goorback}ridetime${i + 1}"
        val title = "${R.string.settingridetimetitle.strings}${goorback.lineName(i, "${R.string.line.strings}${i + 1}")}"
        val hint: String = R.string.minite2hint.strings
        val neutraltitle: String = R.string.timetablesetting.strings
        val savedridetime: String = goorback.rideTime(i, "")
        val edittext = EditText(context)
        edittext.setText(savedridetime)
        setting.setEditNumber2(edittext, hint)
        setStartActivityEditTextAlertDialog (edittext, context, key, title, neutraltitle, intent)
    }

    //乗換時間を設定するDialogを表示する関数
    fun setTransitTimeDialog(context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val key = "${goorback}transittime${i.e}"
        val title: String = "${R.string.settingtransittimetitle.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goorback.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val hint: String = R.string.minite2hint.strings
        val savedtransittime: String = goorback.transitTime(i, "")
        edittext.setText(savedtransittime)
        setting.setEditNumber2(edittext, hint)
        setNotSaveEditTextAlertDialog(edittext, context, title, key)
    }

    //★標準的なEditTextDialogを表示する関数
    private fun setEditTextAlertDialog(textview: TextView, edittext: EditText, context: Context, title: String, key: String) {
        AlertDialog.Builder(context).apply{
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    setting.prefSaveText(context, key, edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★リストを選択するラジオボタンDialogを表示する関数
    private fun setSingleChoiceItemsAlertDialog(textview: TextView, context: Context, key: String, title: String,
                                           arraylist: Array<String>, arrayvalue: Array<String>) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraylist, 0) { _, which ->
                setting.prefSaveText(context, key, arrayvalue[which])
                textview.text = arraylist[which]
            }
            setNegativeButton((R.string.register), null)
            show()
        }
    }

    //★NeutralボタンでEditTextを保存し、別のActivityに遷移するEditTextDialogの設定
    private fun setStartActivityEditTextAlertDialog (edittext: EditText, context: Context, key: String,
                                                title: String, neutraltitle: String, intent: Intent) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    setting.prefSaveText(context, key, edittext.text.toString())
                }
            }
            setNeutralButton(neutraltitle) { _, _ ->
                if (edittext.text.toString() != "") {
                    setting.prefSaveText(context, key, edittext.text.toString())
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Application.context.startActivity(intent)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★保存した値を表示しない標準的なEditTextDialog関数
    private fun setNotSaveEditTextAlertDialog(edittext: EditText, context: Context, title: String, key: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    setting.prefSaveText(context, key, edittext.text.toString())                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★Neutralボタンで路線カラー設定用Dialogを表示するEditTextDialogを表示する関数
    private fun setEditTextPlusColorAlertDialog (textview: TextView, view: View, edittext: EditText, context: Context,
                                    key1: String, key2: String, title1: String, title2: String,
                                    arraylist: Array<String>, arrayvalue: Array<String>) {
        AlertDialog.Builder(context).apply {
            setTitle(title1)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    setting.prefSaveText(context, key1, edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
            }
            setNeutralButton(R.string.settinglinecolorbutton) { _, _ ->
                if (edittext.text.toString() != "") {
                    setting.prefSaveText(context, key1, edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
                setChoiceColorAlertDialog(textview, view, context, key2, title2, arraylist, arrayvalue)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //路線カラーの設定用Dialogを表示する関数
    private fun setChoiceColorAlertDialog(textview: TextView, view: View, context: Context, key: String, title: String,
                                      arraycolorlist: Array<String>, arraycolorvalue: Array<String>) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraycolorlist, 0) { _, which ->
                setting.prefSaveText(context, key, arraycolorvalue[which])
                textview.setTextColor(parseColor(arraycolorvalue[which]))
                view.setBackgroundColor(parseColor(arraycolorvalue[which]))
            }
            setNegativeButton(R.string.register, null)
            show()
        }
    }

}