package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import java.lang.Thread.sleep



class SplashActivity : AppCompatActivity() {
    var foodindex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var get = intent.getSerializableExtra("user") as UserInfo
        sleep(2000)
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("state","launch")
            intent.putExtra("user",get)
            intent.putExtra("foodindex",foodindex)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        ActivityCompat.finishAffinity(this)
    }
}
