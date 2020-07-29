package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import com.github.mikephil.charting.charts.LineChart
import kotlinx.android.synthetic.main.activity_mypage.*

class MyPressurePageActivity : AppCompatActivity() {
    lateinit var chart: LineChart
    var stat1Flag = true
    var imgNum = 0
    lateinit var user:UserInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pressure_page)
        user = intent.getSerializableExtra("user") as UserInfo
        userGender?.setText(user.sexuality)
        userAge?.text = user.age
        userHeight?.text = user.height
        userWeight.text = user.kg
        userName2.text = user.name
        userID2.text = user.id
        init()
    }

    fun init() {
        makePressureStat1Fragment(1)
        // if(user_sugar) makeSugarStat1Fragment(1) else if (user_pressrue) makePressureStat1Fragment(1)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }


    fun makePressureStat1Fragment(num: Int) {//주간그래프
        val fragment = supportFragmentManager.findFragmentById(R.id.frame1)
        val stat1Transaction = supportFragmentManager.beginTransaction()
        val stat1Frag = PressureStat1Fragment()
        stat1Transaction.replace(R.id.frame1, stat1Frag, "press_stat1Frag")//태그값을 imgFrag로 준거임
        stat1Transaction.commit()
        stat1Flag = true
    }
}