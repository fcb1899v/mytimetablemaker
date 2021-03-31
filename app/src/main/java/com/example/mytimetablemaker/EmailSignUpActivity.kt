package com.example.mytimetablemaker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityEmailsignupBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailSignUpActivity : AppCompatActivity() {

    //クラスの定義
    private var auth: FirebaseAuth = Firebase.auth
    private var emailauth = EmailAuth(auth)
    private val admobclass = AdMobClass()

    //ViewBinding
    private lateinit var binding: ActivityEmailsignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailsignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //アカウント登録
        binding.emailCreateAccountButton.setOnClickListener {
            signupAccount(
                this,
                binding.fieldEmail.text.toString(),
                binding.fieldPassword.text.toString(),
                binding.fieldConfirmPassword.text.toString()
            )
        }

        //利用規約・プライバシーポリシー
        binding.signupprivacypolicy.setOnClickListener {
            startActivity(emailauth.intentPrivacyPolicy())
        }

        //AdMob
        admobclass.setAdMob(binding.signupadview, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    // ユーザー登録処理
    private fun signupAccount(context: Context, email: String, password: String, confirmpassword: String) {
        if (emailauth.signupToast(email, password, confirmpassword)) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                emailauth.signupMessage(context, task)
                finish()
            }
        }
    }


}

