package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.parseColor
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.preference.PreferenceManager

class Setting {

    fun prefSaveText(context: Context, key: String, text: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putString("dummy", "dummy").apply()
            putString(key, text).apply()
        }
    }

    fun prefSaveBoolean(context: Context, key: String, isChecked: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putBoolean("dummy", true).apply()
            putBoolean(key, isChecked).apply()
        }
    }

    //EditTextの書式設定（10文字に制限、中央配置、黒色、サイズ20）
    fun setEditText20(edittext: EditText) {
        edittext.apply{
            filters = arrayOf(InputFilter.LengthFilter(20))
            setHint(R.string.character10hint)
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 20F
        }
    }

    //EditTextの書式設定（2桁の数字、中央配置、黒色、サイズ24、指定したhintを表示）
    fun setEditNumber2(edittext: EditText, hint: String) {
        edittext.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            setHint(hint)
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 24F
        }
    }

    fun setEditEmail(edittext: EditText) {
        edittext.apply {
            setHint(R.string.hint_email)
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 20F
        }
    }

    //乗換回数の設定用Dialogを表示する関数
    fun setChangeLineDialog(textview: TextView, context: Context, goorback: String) {
        val title: String = R.string.settingchangelinetitle.strings
        val key = "${goorback}changeline"
        val arraylist: Array<String> = R.array.changelinelist.arrayStrings
        val arrayvalue: Array<String> = R.array.changelinevalue.arrayStrings
        setSingleChoiceItemsAlertDialog(textview, context, key, title, arraylist, arrayvalue)
    }

    //路線名を設定するDialogを表示する関数（Neutralボタンで路線カラー設定用Dialogを表示）
    fun setPrefLineNameDialog(textview1: TextView, textview2: TextView, context: Context, goorback: String, i: Int) {
        val key1 = "${goorback}line${(i + 1)}nameofline"
        val key2 = "${goorback}line${(i + 1)}colorofline"
        val title1 = "${R.string.settinglinenametitle.strings}${R.string.line.strings}${(i + 1)}"
        val title2: String = R.string.settinglinecolortitle.strings
        val linecolorlist: Array<String> = R.array.linecolorlist.arrayStrings
        val linecolorvalue: Array<String> = R.array.linecolorvalue.arrayStrings
        val linename: String = goorback.lineName(i, "")
        val edittext = EditText(context)
        edittext.setText(linename)
        setEditText20(edittext)
        setPrefEditTextAlertDialog (textview1, textview2, context, edittext, key1, key2, title1, title2, linecolorlist, linecolorvalue)
    }

    //乗換時間を設定するDialogを表示する関数
    fun setPrefTransitTimeDialog(textview: TextView, context: Context, goorback: String, i: Int) {
        val edittext = EditText(context)
        val title: String = "${R.string.settingtransittimetitle.strings}${R.string.to.strings.changeWord(R.string.from.strings, i)} " +
                "${goorback.transitStation(i)}${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
        val key = "${goorback}transittime${i.e}"
        val hint: String = R.string.minite2hint.strings
        val savedtransittime: String = goorback.transitTime(i, "")
        edittext.setText(savedtransittime)
        setEditNumber2(edittext, hint)
        setPrefEditTimeAlertDialog(textview, context, edittext, title, key)
    }

    //乗車時間を設定するDialogを表示する関数
    fun setPrefRideTimeDialog(textview: TextView, context: Context, goorback: String, i: Int, intent: Intent) {
        val edittext = EditText(context)
        val key = "${goorback}line${(i + 1)}ridetime"
        val title = "${R.string.settingridetimetitle.strings}${goorback.lineName(i, "")}"
        val hint: String = R.string.minite2hint.strings
        val neutraltitle: String = R.string.timetablesetting.strings
        val savedridetime: String = goorback.rideTime(i,"")
        edittext.setText(savedridetime)
        setEditNumber2(edittext, hint)
        setPrefEditTextChangeActicityAlertDialog(textview, context, edittext, key, title, neutraltitle, intent)
    }

    //★リストを選択するラジオボタンDialogを表示する関数
    private fun setSingleChoiceItemsAlertDialog(textview: TextView, context: Context, key: String, title: String,
                                           arraylist: Array<String>, arrayvalue: Array<String>) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraylist, 0) { _, which ->
                prefSaveText(context, key, arrayvalue[which])
                textview.text = arraylist[which]
            }
            setNegativeButton(R.string.register, null)
            show()
        }
    }

    //★Neutralボタンで路線カラー設定用Dialogを表示するEditTextDialogを表示する関数
    private fun setPrefEditTextAlertDialog(textview1: TextView, textview2: TextView, context: Context, edittext: EditText,
                                      key1: String, key2: String, title1: String, title2: String,
                                      arraylist: Array<String>, arrayvalue: Array<String>) {
        AlertDialog.Builder(context).apply {
            setTitle(title1)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    prefSaveText(context, key1, edittext.text.toString())
                    textview1.text = edittext.text.toString()
                }
            }
            setNeutralButton(R.string.settinglinecolorbutton.strings) { _, _ ->
                if (edittext.text.toString() != "") {
                    prefSaveText(context, key1, edittext.text.toString())
                    textview1.text = edittext.text.toString()
                }
                setPrefChoiceColorAlertDialog(textview1, textview2, context, key2, title2, arraylist, arrayvalue)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //路線カラーの設定用Dialogを表示する関数
    private fun setPrefChoiceColorAlertDialog(textview1: TextView, textview2: TextView, context: Context, key: String, title: String,
                                          arraycolorlist: Array<String>, arraycolorvalue: Array<String>) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraycolorlist, 0) { _, which ->
                prefSaveText(context, key, arraycolorvalue[which])
                textview1.setTextColor(parseColor(arraycolorvalue[which]))
                textview2.setTextColor(parseColor(arraycolorvalue[which]))
            }
            setNegativeButton(R.string.register, null)
            show()
        }
    }

    //★標準的なEditTextDialogを表示する関数
    private fun setPrefEditTimeAlertDialog(textview: TextView, context: Context, edittext: EditText, title: String, key: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    prefSaveText(context, key, edittext.text.toString())
                    textview.text = edittext.text.toString().addMinites
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //★NeutralボタンでEditTextを保存し、別のActivityに遷移するEditTextDialogの設定
    private fun setPrefEditTextChangeActicityAlertDialog(textview: TextView, context: Context, edittext: EditText, key: String,
                                                   title: String, neutraltitle: String, intent: Intent) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    prefSaveText(context, key, edittext.text.toString())
                    textview.text = edittext.text.toString().addMinites
                }
            }
            setNeutralButton(neutraltitle) { _, _ ->
                if (edittext.text.toString() != "") {
                    prefSaveText(context, key, edittext.text.toString())
                    textview.text = edittext.text.toString().addMinites
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }
}