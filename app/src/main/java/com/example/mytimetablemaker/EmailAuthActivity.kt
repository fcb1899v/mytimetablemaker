package com.example.mytimetablemaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityEmailauthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailAuthActivity : AppCompatActivity() {

    //クラスの定義
    private var auth: FirebaseAuth = Firebase.auth
    private var userid = auth.currentUser?.email.toString()
    private var emailauth = EmailAuth(auth)
    private var firebasefirestore = FirebaseFirestore()
    private val admobclass = AdMobClass()

    //ViewBinding
    private lateinit var binding: ActivityEmailauthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailauthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //一度サインインしたらログイン画面をスキップ
        if (userid != "null") {
            startActivity(emailauth.intentSignInChangeActivity())
        }

        //アカウント登録
        binding.emailCreateAccountButton.setOnClickListener {
            emailauth.createAccount(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
        }

        //サインイン
        binding.emailSignInButton.setOnClickListener {
            signInAccount(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
        }

        //パスワードリセット
        binding.sendresetpassbutton.setOnClickListener {
            emailauth.sendPasswordResetMailDialog(this)
        }

        //利用規約・プライバシーポリシー
        binding.topprivacypolicy.setOnClickListener {
            startActivity(emailauth.intentPrivacyPolicy())
        }

        //AdMob
        admobclass.setAdMob(binding.topadview, this)
    }

    // サインイン処理
    private fun signInAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                emailauth.signInMessage(task, email, password)
                if (task.isSuccessful && auth.currentUser!!.isEmailVerified) {
                    firebasefirestore.getFirestore()
                    startActivity(emailauth.intentSignInChangeActivity())
                }
            }
        }
    }
}

