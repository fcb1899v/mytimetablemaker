package com.example.mytimetablemaker

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.mytimetablemaker.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsActivity: AppCompatActivity() {

    private var auth: FirebaseAuth = Firebase.auth
    private var emailauth = EmailAuth(auth)
    private var pref = PreferenceManager.getDefaultSharedPreferences(Application.context)
    private var loggedin = pref.getBoolean("loggedin", false)

    //ViewBinding
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ログアウトおよびアカウント削除したらログイン画面に移る
        if (!loggedin) {
            startActivity(emailauth.intentToEmailAuthActivity())
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_settings, SettingsFragment())
                .commitAllowingStateLoss()
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.settingtitle)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }
}