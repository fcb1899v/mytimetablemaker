package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: AppCompatActivity() {

    //クラスの定義
    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val mainview = MainView()
    private val mainviewdialog = MainViewDialog()
    private val admobclass = AdMobClass()
    private val calendar: Calendar = Calendar.getInstance()

    //時刻を止めるためのフラグ
    private var timeflag = true

    //帰宅と外出を示すフラグ
    private var gobackflag = true
    private var goorback1 = "back1"
    private var goorback2 = "back2"

    //ViewBinding
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //設定画面に移動
        binding.settingimageview.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //＜日付および時刻に関する設定＞

        //時刻の開始ボタンの設定（時刻が止まっている場合）
        binding.timestartbutton.setOnClickListener {
            if (!timeflag) {
                timeflag = true
                binding.timestartbutton.isEnabled = true
                binding.timestopbutton.isEnabled = false
            }
        }
        //時刻の停止ボタンの設定（時刻が止まってない場合）
        binding.timestopbutton.setOnClickListener {
            if (timeflag) {
                timeflag = false
                binding.timestartbutton.isEnabled = false
                binding.timestopbutton.isEnabled = true
            }
        }
        //日付の選択および表示（時刻が止まっている場合）
        binding.currentdate.setOnClickListener {
            if (!timeflag) {
                mainviewdialog.setDatePickerDialog(binding.currentdate, this)
            }
        }
        //時刻の選択および表示（時刻が止まっている場合）
        binding.currenttime.setOnClickListener {
            if (!timeflag) {
                mainviewdialog.setTimePickerDialog(binding.currenttime, this)
            }
        }

        //帰宅画面から外出画面に遷移するときの表示変更
        binding.gobutton.setOnClickListener {
            if (gobackflag) {
                gobackflag = false
                binding.backbutton.isEnabled = false
                binding.gobutton.isEnabled = true
                goorback1 = "go1"
                goorback2 = "go2"
            }
        }

        //外出画面から帰宅画面に遷移するときの表示変更
        binding.backbutton.setOnClickListener {
            if (!gobackflag) {
                gobackflag = true
                binding.backbutton.isEnabled = true
                binding.gobutton.isEnabled = false
                goorback1 = "back1"
                goorback2 = "back2"
            }
        }

        runnable = Runnable {

            //現在時刻および現在日時を表示
            if (timeflag) {
                binding.currentdate.text = mainview.localizeDateString(Date(), "EEEMMMdyyyy")
                binding.currenttime.text = mainview.localizeDateString(Date(), "HH:mm:ss")
            }

            val displaydate: Date = mainview.getLocalizeDate(binding.currentdate.text.toString(), "EEEMMMdyyyy")!!
            val displaytime: Date = mainview.getLocalizeDate(binding.currenttime.text.toString(), "HH:mm:ss")!!

            //表示されている日時の曜日をInt型で取得(日：0 、月：1、火：2、水：3、木：4、金：5、土：6)
            calendar.time = displaydate
            val currentday: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1
            //表示されている時刻をInt型で取得
            val currenthhmmss: Int = SimpleDateFormat("HHmmss").format(displaytime).toInt()

            //帰宅または外出ルート1の表示（GoorBack1Fragmentの呼び出し）
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.goorback1fragment, GoOrBack1Fragment.newInstance(currentday, currenthhmmss, goorback1))
                    .commitAllowingStateLoss()
            }
            //帰宅または外出ルート2の表示（Go2Fragmentの呼び出し）
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.goorback2fragment, GoOrBack2Fragment.newInstance(currentday, currenthhmmss, goorback2))
                    .commitAllowingStateLoss()
            }

            val back2display: Boolean = mainview.getRoot2Switch(goorback2)
            binding.goorback2fragment.visibility = if (back2display) { VISIBLE } else { GONE }
            binding.centerLine.visibility = if (back2display) { VISIBLE } else { GONE }

            //1秒ごとに更新:
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)

        //Admob広告
        admobclass.setAdMob(binding.adview, this)
    }
}