package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("UNREACHABLE_CODE")
class SettingsFragment : Fragment() {

    //クラスの呼び出し
    private var auth: FirebaseAuth = Firebase.auth
    private val emailauth = EmailAuth(auth)
    private val setting = Setting()
    private val firebasefirestore = FirebaseFirestore()
    private val goorbackarray: Array<String> = arrayOf("back1", "go1", "back2", "go2")

    //ViewBinding
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        //乗換回数設定のボタンとしてのTextView
        val changelinebutton: Array<TextView> = arrayOf(
            binding.back1changelinebutton, binding.go1changelinebutton,
            binding.back2changelinebutton, binding.go2changelinebutton)

        //設定した乗換回数を表示するTextView
        val changelinetext: Array<TextView> = arrayOf(
            binding.back1changelinetext, binding.go1changelinetext,
            binding.back2changelinetext, binding.go2changelinetext)

        //表示する路線名のTextViewの設定
        val varioussettingbutton: Array<TextView> = arrayOf(
            binding.back1settingbutton, binding.go1settingbutton,
            binding.back2settingbutton, binding.go2settingbutton)

        //スイッチの状態を表示
        binding.back2switch.isChecked = MainView().getRoot2Switch("back2")
        binding.go2switch.isChecked = MainView().getRoot2Switch("go2")

        //スイッチの状態に応じて表示を変更
        binding.back2changelinelayout.isVisible = binding.back2switch.isChecked
        binding.go2changelinelayout.isVisible = binding.go2switch.isChecked
        binding.back2settinglayout.isVisible = binding.back2switch.isChecked
        binding.go2settinglayout.isVisible = binding.go2switch.isChecked

        //スイッチの変更による表示の変更
        binding.back2switch.setOnCheckedChangeListener { _, isChecked: Boolean ->
            setting.prefSaveBoolean(requireContext(), "back2switch", isChecked)
            binding.back2changelinelayout.isVisible = isChecked
            binding.back2settinglayout.isVisible = isChecked
        }
        binding.go2switch.setOnCheckedChangeListener { _, isChecked: Boolean ->
            setting.prefSaveBoolean(requireContext(), "go2switch", isChecked)
            binding.go2changelinelayout.isVisible = isChecked
            binding.go2settinglayout.isVisible = isChecked
        }

        for (i in 0..3) {
            //乗換回数の表示
            changelinetext[i].text = goorbackarray[i].changeLineString
            ////乗換回数の変更
            changelinebutton[i].setOnClickListener {
                setting.setChangeLineDialog(changelinetext[i], requireContext(), goorbackarray[i])
                changelinetext[i].text = goorbackarray[i].changeLineString
            }
            ////乗換回数の変更
            varioussettingbutton[i].setOnClickListener {
                val title: String = goorbackarray[i].variousSettingsTitle
                val bundle = Bundle()
                bundle.putString("BUNDLE_KEY_GOORBACK", goorbackarray[i])
                val fragment = VariousSettingsFragment()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                        .replace(R.id.main_settings, fragment)
                        .commitAllowingStateLoss()
                (activity as AppCompatActivity).supportActionBar?.title = title
            }
        }

        //サーバーにデータ保存
        binding.saveserverbutton.setOnClickListener {
            firebasefirestore.saveAlertFirestore(requireContext())
        }

        //サーバーからデータ取得
        binding.getserverdatabutton.setOnClickListener {
            firebasefirestore.getAlertFirestore(requireContext())
        }

        //サインアウト
        binding.signoutbutton.setOnClickListener {
            logoutAccount(requireContext())
        }

        //アカウントの削除
        binding.deleteaccountbutton.setOnClickListener {
            deleteAccount(requireContext())
        }

        //利用規約・プライバシーポリシー
        binding.privacypolicy.setOnClickListener {
            startActivity(emailauth.intentPrivacyPolicy())
        }

        //バージョンの表示
        binding.versionnumber.text = BuildConfig.VERSION_NAME
        //private val versionCodes: Int = BuildConfig.VERSION_CODE

        return binding.root
    }

    //ログアウト
    private fun logoutAccount(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.logout.strings)
            setMessage(R.string.logout_account.strings)
            setNegativeButton(R.string.ok) { _, _ ->
                emailauth.afterLogoutAccount()
                startActivity(emailauth.intentToEmailAuthActivity())
            }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }

    //アカウント削除
    private fun deleteAccount(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.delete_your_account.strings)
            setMessage(R.string.delete_account.strings)
            setNegativeButton(R.string.ok) { _, _ ->
                auth.currentUser?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emailauth.afterDeleteAccount()
                        startActivity(emailauth.intentToEmailAuthActivity())
                    } else {
                        emailauth.makeAuthAlert(
                            context,
                            R.string.delete_account_error.strings,
                            R.string.delete_account_unsuccessfully.strings
                        )
                    }
                }
            }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }
}
