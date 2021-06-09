package id.riverflows.snapcrime.ui.splash

import android.content.Intent
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
            val pref = SnapCrime.getDefaultSharedPreference(this@SplashActivity)
            val token = pref.getString(UtilConstants.PREF_AUTH_TOKEN, "") ?: ""
            if(token.isBlank()){
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            }
            finish()
        }
    }
}