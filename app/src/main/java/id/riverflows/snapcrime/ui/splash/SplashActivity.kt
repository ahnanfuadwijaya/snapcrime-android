package id.riverflows.snapcrime.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.databinding.ActivitySplashBinding
import id.riverflows.snapcrime.ui.home.HomeActivity
import id.riverflows.snapcrime.util.UtilConfigs.SPLASH_DURATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding
        get() = _binding as ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch(Dispatchers.Main){
            delay(SPLASH_DURATION)
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            finish()
        }
    }
}