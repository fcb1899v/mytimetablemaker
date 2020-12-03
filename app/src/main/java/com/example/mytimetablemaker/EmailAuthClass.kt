package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import com.example.mytimetablemaker.Application.Companion.context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class EmailAuth(private val auth: FirebaseAuth) {

    private val firebasefirestore = FirebaseFirestore()

    // ユーザー登録処理
    fun createAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                createAccountMessage(task, email, password)
                if (task.isSuccessful) { sendEmailVerrification() }
            }
        }
    }

    //ユーザー登録時のメッセージ
    private fun createAccountMessage(task: Task<AuthResult>, email: String, password: String) {
        println(String.format("create email=%s, password%s", email, password))
        if (task.isSuccessful) {
            println(R.string.auth_successed.strings)
        } else {
            printAndMakeToast(R.string.failed_to_creat_account.strings)
        }
    }

    //サインイン時のメッセージ
    fun signInMessage(task: Task<AuthResult>, email: String, password: String) {
        println(String.format("signin email=%s, password%s", email, password))
        if (task.isSuccessful) {
            if (auth.currentUser!!.isEmailVerified) {
                printAndMakeToast(R.string.auth_successed.strings)
            } else {
                printAndMakeToast(R.string.auth_mail_check.strings)
            }
        } else {
            printAndMakeToast(R.string.auth_failed.strings)
        }
    }

    //サインアウト
    fun intentSignOut(): Intent {
        auth.signOut()
        Toast.makeText(context, R.string.signed_out.strings, Toast.LENGTH_SHORT).show()
        firebasefirestore.resetPreferenceData()
        return Intent(context, EmailAuthActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    //アカウント削除
    fun intentDeleteAccount(context: Context): Intent? {
        var intent: Intent? = null
        AlertDialog.Builder(context).apply {
            setTitle(R.string.delete_your_account_q.strings)
            setNegativeButton(R.string.yes) { _, _ ->
                auth.currentUser?.delete()?.addOnCompleteListener {
                    intent = intentSignOut()
                }
            }
            setPositiveButton(R.string.no, null)
            show()
        }
        return intent
    }

    //サインイン後の画面遷移
    fun intentSignInChangeActivity(): Intent {
        return Intent(context, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    //パスワードリセットメールの送信
    private fun passwordReset(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void> ->
            toastSuccessOrFail (task, R.string.sent_reset_mail.strings, R.string.failed_to_sent_reset_mail.strings)
        }
    }

    //パスワードリセット
    fun sendPasswordResetMailDialog(context: Context) {
        val edittext: EditText = EditText(context).apply {
            setHint(R.string.hint_email)
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 20F
        }
        AlertDialog.Builder(context).apply {
            setTitle(R.string.reset_password.strings)
            setView(edittext)
            setPositiveButton(R.string.yes) { _, _ ->
                if (edittext.text.toString().isNotEmpty()) {
                    passwordReset(edittext.text.toString())
                }
            }
            setNegativeButton(R.string.no, null)
            show()
        }
    }

    //認証メールの送信
    private fun sendEmailVerrification() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task: Task<Void> ->
            toastSuccessOrFail(task, R.string.sent_authmail.strings, R.string.failed_to_sent_authmail.strings)
        }
    }

    //タスクの成功または失敗のメッセージ
    private fun toastSuccessOrFail (task: Task<Void>, successmessage: String, failmessage: String) {
        if (task.isSuccessful) {
            printAndMakeToast(successmessage)
        } else {
            printAndMakeToast(failmessage)
        }
    }

    //
    private fun printAndMakeToast(message: String) {
        println(message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    //プライバシーポリシーページへの遷移
    fun intentPrivacyPolicy(): Intent {
        val url = "https://landingpage-mytimetablemaker.web.app/privacypolicy.html"
        return Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    }
}
