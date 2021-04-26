package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.mytimetablemaker.databinding.ActivityEmailauthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailAuthActivity : AppCompatActivity() {

    //クラスの定義
    private var auth: FirebaseAuth = Firebase.auth
    private var emailauth = EmailAuth(auth)
    private var firebasefirestore = FirebaseFirestore()
    private var admobclass = AdMobClass()
    private var pref = PreferenceManager.getDefaultSharedPreferences(Application.context)
    private var loggedin = pref.getBoolean("loggedin", false)

    //ViewBinding
    private lateinit var binding: ActivityEmailauthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailauthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //一度サインインしたらログイン画面をスキップ
        if (loggedin) {
            startActivity(emailauth.intentToMainActivity())
        }

        //サインイン
        binding.emailSignInButton.setOnClickListener {
            loginAccount(
                this,
                binding.fieldEmail.text.toString(),
                binding.fieldPassword.text.toString()
            )
        }

        //アカウント登録
        binding.emailSignUpButton.setOnClickListener {
            startActivity(Intent(this, EmailSignUpActivity::class.java))
        }

        //パスワードリセット
        binding.sendresetpassbutton.setOnClickListener {
            emailauth.sendPasswordResetMailDialog(this)
        }

        //AdMob
        admobclass.setAdMob(binding.topadview, this)
    }

    // ログイン処理
    private fun loginAccount(context: Context, email: String, password: String) {
        if (emailauth.loginToast(email, password)) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                if (emailauth.loginMessage(this, task)) {
                    firebasefirestore.getFirestoreAtSignIn(context)
                    startActivity(emailauth.intentToMainActivity())
                }
            }
        }
    }
}

