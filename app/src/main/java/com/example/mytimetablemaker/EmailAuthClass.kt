package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import com.example.mytimetablemaker.Application.Companion.context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class EmailAuth(private val auth: FirebaseAuth) {

    private val setting = Setting()
    private val firebasefirestore = FirebaseFirestore()

    //サインアップ入力のエラー処理
    fun signupToast(email: String, password: String, confirmpassword: String): Boolean {
        println(String.format("signup email=%s, password%s, confirmpassword%s", email, password, confirmpassword))
        var toastflag = false
        when {
            email.isEmpty() -> makeAuthToast(R.string.hint_email.strings)
            password.isEmpty() -> makeAuthToast(R.string.hint_password.strings)
            confirmpassword.isEmpty() -> makeAuthToast(R.string.hint_confirmpassword.strings)
            password != confirmpassword -> makeAuthToast(R.string.confirmpassword_not_match.strings)
            else -> toastflag = true
        }
        return toastflag
    }

    //サインアップ時のメッセージ表示及び処理
    fun signupMessage(context: Context, task: Task<AuthResult>) {
        if (task.isSuccessful) {
            signupSuccessMessage()
        } else {
            signupErrorMessage(context, task)
        }
    }

    //サインアップ成功時のメッセージ表示及び処理
    private fun signupSuccessMessage() {
        makeAuthToast(R.string.signup_successfully.strings)
        setting.prefSaveBoolean(context, "savepref", false)
        sendEmailVerrification()
    }

    //認証メールの送信
    private fun sendEmailVerrification() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                makeAuthToast(R.string.send_auth_mail_successfully.strings)
            } else {
                makeAuthAlert(
                    context,
                    R.string.signup_error.strings,
                    R.string.send_auth_mail_unsuccessfully.strings
                )
            }
        }
    }

    //サインアップエラーのメッセージ表示
    private fun signupErrorMessage(context: Context, task: Task<AuthResult>) {
        val title: String = R.string.signup_error.strings
        val message: String = when (task.exception!!) {
            is FirebaseAuthUserCollisionException -> R.string.email_already_registered.strings
            is FirebaseAuthWeakPasswordException -> R.string.password_over_6_characters.strings
            else -> ""
        }
        makeAuthAlert(context, title, message)
    }

    //ログイン入力のエラー処理
    fun loginToast(email: String, password: String): Boolean {
        println(String.format("login email=%s, password%s", email, password))
        var toastflag = false
        when {
            email.isEmpty() -> makeAuthToast(R.string.hint_email.strings)
            password.isEmpty() -> makeAuthToast(R.string.hint_password.strings)
            else -> toastflag = true
        }
        return toastflag
    }

    //ログイン時のメッセージ表示
    fun loginMessage(context: Context, task: Task<AuthResult>): Boolean {
        return if (task.isSuccessful) {
            loginSuccessMessage(context)
        } else {
            loginErrorMessage(context, task)
        }
    }

    //ログイン成功のメッセージ表示
    private fun loginSuccessMessage(context: Context): Boolean {
        return if (auth.currentUser!!.isEmailVerified) {
            makeAuthToast(R.string.login_successfully.strings)
            true
        } else {
            makeAuthAlert(
                context,
                R.string.not_verified_account.strings,
                R.string.auth_mail_check.strings
            )
            false
        }
    }

    //ログインエラーのメッセージ表示
    private fun loginErrorMessage(context: Context, task: Task<AuthResult>): Boolean {
        val title: String = R.string.login_error.strings
        val message: String = when (task.exception!!) {
            is FirebaseAuthInvalidCredentialsException -> R.string.incorrect_email_or_password.strings
            is FirebaseAuthInvalidUserException -> {
                when ((task.exception!! as FirebaseAuthInvalidUserException).errorCode) {
                    "ERROR_INVALID_EMAIL" -> R.string.incorrect_email_format.strings
                    "ERROR_USER_NOT_FOUND" -> R.string.email_not_registered.strings
                    "ERROR_WRONG_PASSWORD" -> R.string.incorrect_email_or_password.strings
                    "ERROR_USER_DISABLED" -> R.string.account_disabled.strings
                    else -> ""
                }
            }
            else -> ""
        }
        makeAuthAlert(context, title, message)
        return false
    }

    // ログイン後の画面遷移
    fun intentToMainActivity(): Intent {
        return Intent(context, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    //ログアウト後の処理
    fun afterLogoutAccount() {
        auth.signOut()
        firebasefirestore.resetPreferenceData()
        makeAuthToast(R.string.logged_out.strings)
    }

    //ログアウト後の画面遷移
    fun intentToEmailAuthActivity(): Intent {
        return Intent(context, EmailAuthActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    //アカウント削除後の処理
    fun afterDeleteAccount() {
        auth.signOut()
        firebasefirestore.resetPreferenceData()
        makeAuthToast(R.string.delete_account_successfully.strings)
    }

    //パスワードリセットメールの送信
    private fun passwordReset(context: Context, email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                makeAuthToast(R.string.send_auth_mail_successfully.strings)
            } else {
                makeAuthAlert(
                    context,
                    R.string.password_reset_error.strings,
                    R.string.send_reset_mail_unsuccessfully.strings
                )
            }
        }
    }

    //パスワードリセット
    fun sendPasswordResetMailDialog(context: Context) {
        val edittext = EditText(context)
        setting.setEditEmail(edittext)
        AlertDialog.Builder(context).apply {
            setTitle(R.string.password_reset.strings)
            setMessage(R.string.reset_password.strings)
            setView(edittext)
            setPositiveButton(R.string.ok) { _, _ ->
                if (edittext.text.toString().isNotEmpty()) {
                    passwordReset(context, edittext.text.toString())
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Toastの表示
    private fun makeAuthToast(message: String) {
        println(message)
        Log.d(TAG, message)
        val ts = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
    }

    //Alertの表示
    fun makeAuthAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok, null)
            show()
        }
    }

    //プライバシーポリシーページへの遷移
    fun intentPrivacyPolicy(): Intent {
        val url = "https://landingpage-mytimetablemaker.web.app/privacypolicy.html"
        return Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    }
}

//enum class FirebaseAuthError(val errorcode: String) {
//    USER_NOT_FOUND("ERROR_USER_NOT_FOUND"),
//    INVALID_EMAIL("ERROR_INVALID_EMAIL"),
//    WRONG_PASSWORD("ERROR_WRONG_PASSWORD"),
//    USER_DISABLED("ERROR_USER_DISABLED"),
//    INVALID_CUSTOM_TOKEN("ERROR_INVALID_CUSTOM_TOKEN"), //"The custom token format is incorrect. Please check the documentation."
//    CUSTOM_TOKEN_MISMATCH("ERROR_CUSTOM_TOKEN_MISMATCH"), //"The custom token corresponds to a different audience."));
//    INVALID_CREDENTIAL("ERROR_INVALID_CREDENTIAL"), //"The supplied auth credential is malformed or has expired."));
//    USER_MISMATCH("ERROR_USER_MISMATCH"), //"The supplied credentials do not correspond to the previously signed in user."));
//    REQUIRES_RECENT_LOGIN("ERROR_REQUIRES_RECENT_LOGIN"), //"This operation is sensitive and requires recent authentication. Log in again before retrying this request."));
//    ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"), //"An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."));
//    EMAIL_ALREADY_IN_USE("ERROR_EMAIL_ALREADY_IN_USE"), //"The email address is already in use by another account."));
//    CREDENTIAL_ALREADY_IN_USE("ERROR_CREDENTIAL_ALREADY_IN_USE"), //"This credential is already associated with a different user account."));
//    USER_TOKEN_EXPIRED("ERROR_USER_TOKEN_EXPIRED"), //"The user\'s credential is no longer valid. The user must sign in again."));
//    INVALID_USER_TOKEN("ERROR_INVALID_USER_TOKEN"), //"The user\'s credential is no longer valid. The user must sign in again."));
//    OPERATION_NOT_ALLOWED("ERROR_OPERATION_NOT_ALLOWED"), //"This operation is not allowed. You must enable this service in the console."));
//    WEAK_PASSWORD("ERROR_WEAK_PASSWORD"), //"The given password is invalid."));
//    MISSING_EMAIL("ERROR_MISSING_EMAIL") //"An email address must be provided.";
//}