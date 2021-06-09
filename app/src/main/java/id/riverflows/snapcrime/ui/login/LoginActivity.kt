package id.riverflows.snapcrime.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.riverflows.snapcrime.app.SnapCrime
import id.riverflows.snapcrime.databinding.ActivityLoginBinding
import id.riverflows.snapcrime.ui.home.HomeActivity
import id.riverflows.snapcrime.util.UtilConstants
import id.riverflows.snapcrime.util.UtilDummyData.PASSWORD
import id.riverflows.snapcrime.util.UtilDummyData.USERNAME

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private val bind get() = binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // for login
        val gmail = bind.etEmail
        val password = bind.etPassword
        bind.btnLogin.setOnClickListener {
            if(gmail.text.trim().isEmpty() || password.text.trim().isEmpty()){
                Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show()
            }else if (gmail.text.toString() == USERNAME && password.text.toString() == PASSWORD){
                SnapCrime.getDefaultSharedPreference(this).edit().apply {
                    putString(UtilConstants.PREF_AUTH_TOKEN, "lOginToken")
                }.apply()
                startActivity(
                    Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }else
                Toast.makeText(this,"Something wrong",Toast.LENGTH_LONG).show()
        }
    }
}