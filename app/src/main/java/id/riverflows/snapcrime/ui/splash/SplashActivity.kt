package id.riverflows.snapcrime.ui.splash

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.riverflows.snapcrime.app.SnapCrime
import id.riverflows.snapcrime.databinding.ActivitySplashBinding
import id.riverflows.snapcrime.ui.home.HomeActivity
import id.riverflows.snapcrime.ui.login.LoginActivity
import id.riverflows.snapcrime.util.UtilConfigs.SPLASH_DURATION
import id.riverflows.snapcrime.util.UtilConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch(Dispatchers.Main){
            delay(SPLASH_DURATION)
            val pref = SnapCrime.getDefaultSharedPreference(applicationContext)
            val token = pref.getString(UtilConstants.PREF_AUTH_TOKEN, "") ?: ""
            if(token.isBlank()){
                Intent(this@SplashActivity, LoginActivity::class.java)
            }else{
                Intent(this@SplashActivity, HomeActivity::class.java)
            }.apply {
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }.also { startActivity(it) }
        }
    }
}