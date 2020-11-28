package com.example.mytimetablemaker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityTimetableBinding
import java.io.InputStream

class TimetableActivity: AppCompatActivity() {

    //フラグメントから渡されるデータの初期化
    private var goorback: String = ""
    private var linenumber: Int = 0
    private var currentday: Int = 1

    //ViewBinding
    private lateinit var binding: ActivityTimetableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //フラグメントから受け渡されたデータ
        val intentgoorback = "goorback"
        val intentlinenumber = "linenumber"
        val intentcurrentday = "currentday"

        //時刻表の時刻表示のTextViewを配列として初期取得
        var weekdayhour: Array<TextView?> = arrayOf()
        weekdayhour += arrayOf(binding.weekdayhour04)
        weekdayhour += arrayOf(binding.weekdayhour05)
        weekdayhour += arrayOf(binding.weekdayhour06)
        weekdayhour += arrayOf(binding.weekdayhour07)
        weekdayhour += arrayOf(binding.weekdayhour08)
        weekdayhour += arrayOf(binding.weekdayhour09)
        weekdayhour += arrayOf(binding.weekdayhour10)
        weekdayhour += arrayOf(binding.weekdayhour11)
        weekdayhour += arrayOf(binding.weekdayhour12)
        weekdayhour += arrayOf(binding.weekdayhour13)
        weekdayhour += arrayOf(binding.weekdayhour14)
        weekdayhour += arrayOf(binding.weekdayhour15)
        weekdayhour += arrayOf(binding.weekdayhour16)
        weekdayhour += arrayOf(binding.weekdayhour17)
        weekdayhour += arrayOf(binding.weekdayhour18)
        weekdayhour += arrayOf(binding.weekdayhour19)
        weekdayhour += arrayOf(binding.weekdayhour20)
        weekdayhour += arrayOf(binding.weekdayhour21)
        weekdayhour += arrayOf(binding.weekdayhour22)
        weekdayhour += arrayOf(binding.weekdayhour23)
        weekdayhour += arrayOf(binding.weekdayhour24)
        weekdayhour += arrayOf(binding.weekdayhour25)
        //フラグメントから渡されたデータ
        intent?.apply {
            goorback = getStringExtra(intentgoorback).toString()
            linenumber = getIntExtra(intentlinenumber, 1)
            currentday = getIntExtra(intentcurrentday, 1)
        }

        //アクションバーの表示設定
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.timetabletitle)

        //時刻表のタイトル
        val departstation: String = goorback.departStation(linenumber, "${R.string.depsta.strings}${linenumber + 1}")
        val linename: String = goorback.lineName(linenumber, "${R.string.line.strings}${linenumber + 1}")
        val arrivestation: String = goorback.arriveStation(linenumber, "${R.string.arrsta.strings}${linenumber + 1}")
        val timetabletitle: String = linename.timeTableTitle(arrivestation)
        binding.timetablestationname.text = departstation
        binding.timetablestationname2.text = timetabletitle

        //平日または土日の表示
        binding.tablelineweekday.text = currentday.dayOrEndString(getString(R.string.weekend), getString(R.string.weekday))
        binding.tablelineweekday.setTextColor(currentday.dayOrEndColor(Color.RED, Color.WHITE))
        binding.daybutton.text = currentday.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings)
        binding.daybutton.setTextColor(currentday.dayOrEndColor(R.string.primarydark.setColor, Color.RED))

        val timetable = Timetable()

        //時刻の表示
        val timetablearray: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, currentday)
        for (i: Int in 0..21) { weekdayhour[i]?.text = timetablearray[i] }

        //平日と土日祝の表示の切替
        binding.daybutton.setOnClickListener {
            when (binding.daybutton.text) {
                getString(R.string.weekday) -> {
                    timetable.getTimetableText(binding.tablelineweekday, 1)
                    timetable.getTimetableButton(binding.daybutton, 1)
                    val timetablechange: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, 1)
                    for (i: Int in 0..21) { weekdayhour[i]?.text = timetablechange[i] }
                }
                else -> {
                    timetable.getTimetableText(binding.tablelineweekday, 0)
                    timetable.getTimetableButton(binding.daybutton, 0)
                    val timetablechange: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, 0)
                    for (i: Int in 0..21) { weekdayhour[i]?.text = timetablechange[i] }
                }
            }
        }

        //時刻表の時刻の設定
        for (i: Int in 0..21) {
            weekdayhour[i]?.setOnClickListener {
                val daynumber: Int = when (binding.daybutton.text) { getString(R.string.weekday) -> 0 else -> 1 }
                timetable.makeTimetableDialog (weekdayhour[i]!!, this, goorback, linenumber, i + 4, daynumber, weekdayhour)
            }
        }

        val pictureselectbutton: Button = binding.pictureselectbutton
        pictureselectbutton.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)
        }
    }

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    data?.data?.also { uri: Uri ->
                        val inputStream: InputStream? = contentResolver?.openInputStream(uri)
                        val picture: Bitmap = BitmapFactory.decodeStream(inputStream)
                        val pictureview: ImageView = binding.PictureView
                        pictureview.setImageBitmap(picture)
                    }
                } catch (e: Exception) {
                    //Toast.makeText(this, "エラーが発生しました"), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}