package com.example.rishikeshproject.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.example.rishikeshproject.R
import com.example.rishikeshproject.base.BaseActivity
import com.example.rishikeshproject.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val splashDuration: Long = 2000 // 2 seconds
    override val bindingInflater: (LayoutInflater) -> ActivitySplashBinding
        get(){
            return  ActivitySplashBinding::inflate
        }

    override fun setup() {
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

        }, splashDuration)
    }

}