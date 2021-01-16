package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentVarioussettingsBinding

//各種設定
@Suppress("SameParameterValue")
class VariousSettingsFragment: Fragment() {

    private val setting = Setting()
    private val mainviewdialog = MainViewDialog()

    //ViewBinding
    private lateinit var binding: FragmentVarioussettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVarioussettingsBinding.inflate(layoutInflater, container, false)

        val goorback: String? = arguments?.getString("BUNDLE_KEY_GOORBACK")
        val changeline: Int = goorback!!.changeLine

        //出発地・到着地を設定するボタンの設定
        val departpointbutton: TextView = binding.departpointbutton
        val arrivepointbutton: TextView = binding.arrivepointbutton

        //設定した出発地・到着地を表示するテキストの設定
        val departpointtext: TextView = binding.departpointtext
        val arrivepointtext: TextView = binding.arrivepointtext

        //乗車駅を設定するボタンの設定
        val departstationbutton: Array<TextView> =
            arrayOf(binding.departstation1button, binding.departstation2button, binding.departstation3button)

        //設定した乗車駅を表示するテキストの設定
        val departstationtext: Array<TextView> =
            arrayOf(binding.departstation1text, binding.departstation2text, binding.departstation3text)

        //降車駅を設定するボタンの設定
        val arrivestationbutton: Array<TextView> =
            arrayOf(binding.arrivestation1button, binding.arrivestation2button, binding.arrivestation3button)

        //設定した降車駅を表示するテキストの設定
        val arrivestationtext: Array<TextView> =
            arrayOf(binding.arrivestation1text, binding.arrivestation2text, binding.arrivestation3text)

        //路線名・路線カラーを設定するボタンの設定
        val linenamebutton: Array<TextView> =
            arrayOf(binding.linename1button, binding.linename2button, binding.linename3button)

        //設定した路線名・路線カラーを表示するテキストの設定
        val linenametext: Array<TextView> =
            arrayOf(binding.linename1text, binding.linename2text, binding.linename3text)

        //乗車時間を設定するボタンの設定
        val ridetimebutton: Array<TextView> =
            arrayOf(binding.ridetime1button, binding.ridetime2button, binding.ridetime3button)

        //設定した乗車時間を表示するテキストの設定
        val ridetimetext: Array<TextView> =
            arrayOf(binding.ridetime1text, binding.ridetime2text, binding.ridetime3text)

        //移動方法を設定するボタンの設定
        val transportationbutton: Array<TextView> = arrayOf(
            binding.transportationebutton, binding.transportation1button,
            binding.transportation2button, binding.transportation3button)

        //設定した移動方法を表示するテキストの設定
        val transportationtext: Array<TextView> = arrayOf(
            binding.transportationetext, binding.transportation1text,
            binding.transportation2text, binding.transportation3text)

        //乗換時間を設定するボタンの設定
        val transittimebutton: Array<TextView> = arrayOf(
            binding.transittimeebutton, binding.transittime1button,
            binding.transittime2button, binding.transittime3button)

        //設定した乗換時間を表示するテキストの設定
        val transittimetext: Array<TextView> = arrayOf(
            binding.transittimeetext, binding.transittime1text,
            binding.transittime2text, binding.transittime3text)

        //路線2に関するレイアウト
        val station2layout: Array<LinearLayout> = arrayOf(
            binding.station2layout, binding.linename2layout, binding.ridetime2layout,
            binding.transportation2layout, binding.transittime2layout)

        //路線3に関するレイアウト
        val station3layout: Array<LinearLayout> = arrayOf(
            binding.station3layout, binding.linename3layout, binding.ridetime3layout,
            binding.transportation3layout, binding.transittime3layout)

        //乗換回数に応じたレイアウトの表示・非表示
        for (i: Int in 0..4) {
            station2layout[i].visibility = if (changeline > 0) {View.VISIBLE} else {View.GONE}
            station3layout[i].visibility = if (changeline > 1) {View.VISIBLE} else {View.GONE}
        }

        //設定値の表示
        departpointtext.text = goorback.departPoint(R.string.notset.strings, R.string.notset.strings)
        arrivepointtext.text = goorback.arrivePoint(R.string.notset.strings, R.string.notset.strings)
        transportationtext[0].text  = goorback.transportation(0, R.string.notset.strings)
        transittimetext[0].text = goorback.transitTime(0, R.string.notset.strings).addMinites
        for (i: Int in 0..changeline) {
            departstationtext[i].text = goorback.departStation(i, R.string.notset.strings)
            arrivestationtext[i].text = goorback.arriveStation(i, R.string.notset.strings)
            linenametext[i].text = goorback.lineName(i, R.string.notset.strings)
            linenametext[i].setTextColor(parseColor(goorback.lineColor(i, R.string.lightgray.strings)))
            ridetimetext[i].text = goorback.rideTime(i, R.string.notset.strings).addMinites
            ridetimetext[i].setTextColor(parseColor(goorback.lineColor(i, R.string.lightgray.strings)))
            transportationtext[i + 1].text = goorback.transportation(i + 1, R.string.notset.strings)
            transittimetext[i + 1].text = goorback.transitTime(i + 1, R.string.notset.strings).addMinites
        }

        val intentarray: Array<Intent> =
                changeTimetableActivity(requireContext(), goorback, 1)

        //出発地名の設定
        departpointbutton.setOnClickListener {
            mainviewdialog.setDeparturePointDialog(departpointtext, requireContext(), goorback)
        }
        //到着地名の設定
        arrivepointbutton.setOnClickListener {
            mainviewdialog.setArrivalPointDialog(arrivepointtext, requireContext(), goorback)
        }

        for (i: Int in 0..changeline) {
            //乗車駅名の設定
            departstationbutton[i].setOnClickListener {
                mainviewdialog.setDepartStationDialog(departstationtext[i], requireContext(), goorback, i)
            }
            //降車駅名の設定
            arrivestationbutton[i].setOnClickListener {
                mainviewdialog.setArriveStationDialog(arrivestationtext[i], requireContext(), goorback, i)
            }
            //路線名の設定
            linenamebutton[i].setOnClickListener {
                setting.setPrefLineNameDialog(linenametext[i], ridetimetext[i], requireContext(), goorback, i)
            }
            //乗車時間の設定
            ridetimebutton[i].setOnClickListener {
                setting.setPrefRideTimeDialog(ridetimetext[i], requireContext(), goorback, i, intentarray[i])
            }
        }

        for (i: Int in 0..changeline + 1) {
            //移動手段の設定
            transportationbutton[i].setOnClickListener {
                mainviewdialog.setTransportationDialog(transportationtext[i], requireContext(), goorback, i)
            }
            //乗換時間の設定
            transittimebutton[i].setOnClickListener {
                setting.setPrefTransitTimeDialog(transittimetext[i], requireContext(), goorback, i)
            }
        }
        return binding.root
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