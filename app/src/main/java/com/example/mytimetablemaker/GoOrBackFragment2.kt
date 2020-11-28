package com.example.mytimetablemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentGoorback2Binding

class GoOrBack2Fragment : Fragment() {

    //MainActivityから受け渡されるデータのパラメータ
    private val ARG_currentday = "currentday"
    private val ARG_currenthhmmss = "currenthhmmss"
    private val ARG_goorback2 = "back2"

    //MainActivityから受け渡されるデータの初期化
    private var currentday: Int = 0
    private var currenthhmmss: Int = 0
    private var goorback2: String = ""

    //クラスの呼び出し
    private val mainfragment = MainFragment()

    //ViewBinding
    private lateinit var binding: FragmentGoorback2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoorback2Binding.inflate(layoutInflater, container, false)

        //表示する出発地名・目的地名のTextViewの設定
        val departurepointview2: TextView = binding.stationName120
        val arrivalpointview2: TextView = binding.stationName12e

        //表示する乗車駅名のTextViewの設定
        val departstationview2: Array<TextView> =
            arrayOf(binding.stationName121, binding.stationName123, binding.stationName125)

        //表示する降車駅名のTextViewの設定
        val arrivestationview2: Array<TextView> =
            arrayOf(binding.stationName122, binding.stationName124, binding.stationName126)

        //表示する路線名のTextViewの設定
        val linenameview2: Array<TextView> =
            arrayOf(binding.lineName121, binding.lineName123, binding.lineName125)

        //表示する路線のViewの設定
        val linelineview2: Array<View> =
            arrayOf(binding.lineLine121, binding.lineLine123, binding.lineLine125)

        //表示する移動手段方法のTextViewの設定
        val transportview2: Array<TextView> =
            arrayOf(binding.lineName12e, binding.lineName120, binding.lineName122, binding.lineName124)

        //表示する移動手段のViewの設定
        val transitline2: Array<View> =
            arrayOf(binding.lineLine12e, binding.lineLine120, binding.lineLine122, binding.lineLine124)

        //表示するレイアウトの設定
        val layoutview2: Array<LinearLayout> =
            arrayOf(binding.linearlayout122, binding.linearlayout123)

        //表示する時刻のTextViewの設定
        val timeview2: Array<TextView> =
            arrayOf(binding.startTime120, binding.startTime12e, binding.startTime121, binding.startTime122,
                binding.startTime123, binding.startTime124, binding.startTime125, binding.startTime126)

        //表示するカウントダウンのTextViewの設定
        val countdownview2: TextView = binding.countdown12

        //MainActivityから渡されたデータ
        val currentday: Int = currentday
        val currenthhmmss: Int = currenthhmmss
        val goorback2: String = goorback2

        mainfragment.mainFun(requireContext(), currentday, currenthhmmss, goorback2,
            departurepointview2, departstationview2, arrivestationview2, arrivalpointview2,
            linenameview2, linelineview2, transportview2, transitline2, layoutview2, timeview2, countdownview2)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentday = it.getInt(ARG_currentday)
            currenthhmmss = it.getInt(ARG_currenthhmmss)
            goorback2 = it.getString(ARG_goorback2)!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currentday: Int, currenthhmmss: Int, goorback2: String) = GoOrBack2Fragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_currentday, currentday)
                putInt(ARG_currenthhmmss, currenthhmmss)
                putString(ARG_goorback2, goorback2)
            }
        }
    }
}
