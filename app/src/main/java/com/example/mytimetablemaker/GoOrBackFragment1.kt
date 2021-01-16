package com.example.mytimetablemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentGoorback1Binding

class GoOrBack1Fragment : Fragment() {

    //MainActivityから受け渡されるパラメータ
    private val ARG_currentday = "currentday"
    private val ARG_currenthhmmss = "currenthhmmss"
    private val ARG_goorback1 = "goorback1"

    //MainActivityから渡されたデータ
    private var currentday: Int = 0
    private var currenthhmmss: Int = 0
    private var goorback1: String = ""

    //クラスの呼び出し
    private val mainfragment = MainFragment()

    //ViewBinding
    private lateinit var binding: FragmentGoorback1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoorback1Binding.inflate(layoutInflater, container, false)

        //表示する出発地名・目的地名のTextViewの設定
        val departurepointview1: TextView = binding.stationName110
        val arrivalpointview1: TextView = binding.stationName11e

        //表示する乗車駅名のTextViewの設定
        val departstationview1: Array<TextView> =
            arrayOf(binding.stationName111, binding.stationName113, binding.stationName115)

        //表示する降車駅名のTextViewの設定
        val arrivestationview1: Array<TextView> =
            arrayOf(binding.stationName112, binding.stationName114, binding.stationName116)

        //表示する路線名のTextViewの設定
        val linenameview1: Array<TextView> =
            arrayOf(binding.lineName111, binding.lineName113, binding.lineName115)

        //表示する路線のViewの設定
        val linelineview1: Array<View> =
            arrayOf(binding.lineLine111, binding.lineLine113, binding.lineLine115)

        //表示する移動手段方法のTextViewの設定
        val transportview1: Array<TextView> =
            arrayOf(binding.lineName11e, binding.lineName110, binding.lineName112, binding.lineName114)

        //表示する移動手段のViewの設定
        val transitline1: Array<View> =
            arrayOf(binding.lineLine11e, binding.lineLine110, binding.lineLine112, binding.lineLine114)

        //表示するレイアウトの設定
        val layoutview1: Array<LinearLayout> =
            arrayOf(binding.linearlayout112, binding.linearlayout113)

        //表示する時刻のTextViewの設定
        val timeview1: Array<TextView> =
            arrayOf(binding.startTime110, binding.startTime11e, binding.startTime111, binding.startTime112,
                binding.startTime113, binding.startTime114, binding.startTime115, binding.startTime116)

        //表示するカウントダウンのTextViewの設定
        val countdownview1: TextView = binding.countdown11

        //MainActivityから渡されたデータ
        val currentday: Int = currentday
        val currenthhmmss: Int = currenthhmmss
        val goorback1: String = goorback1

        mainfragment.mainFun(requireContext(), currentday, currenthhmmss, goorback1,
            departurepointview1, departstationview1, arrivestationview1, arrivalpointview1,
            linenameview1, linelineview1, transportview1, transitline1, layoutview1, timeview1, countdownview1)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentday = it.getInt(ARG_currentday)
            currenthhmmss = it.getInt(ARG_currenthhmmss)
            goorback1 = it.getString(ARG_goorback1)!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currentday: Int, currenthhmmss: Int, goorback1: String) =
            GoOrBack1Fragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_currentday, currentday)
                    putInt(ARG_currenthhmmss, currenthhmmss)
                    putString(ARG_goorback1, goorback1)
                }
            }
    }
}