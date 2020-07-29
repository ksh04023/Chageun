package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import com.github.mikephil.charting.charts.LineChart
import kotlinx.android.synthetic.main.activity_my_both_page.*
import kotlinx.android.synthetic.main.activity_mypage.userAge
import kotlinx.android.synthetic.main.activity_mypage.userGender
import kotlinx.android.synthetic.main.activity_mypage.userHeight
import kotlinx.android.synthetic.main.activity_mypage.userWeight

class MyBothPageActivity : AppCompatActivity() {
    lateinit var chart: LineChart
    var stat1Flag = true
    var imgNum = 0
    lateinit var user:UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_both_page)
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

        makeSugarStat1Fragment(1)
        // if(user_sugar) makeSugarStat1Fragment(1) else if (user_pressrue) makePressureStat1Fragment(1)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        bothButton1.setOnClickListener {
            //if (user_sugar) makeSugarStat1Fragment(1) else if (user_pressrue) makePressureStat1Fragment(1)
            makeSugarStat1Fragment(1)
            bothButton1.setBackgroundResource(R.drawable.textview_white_left_pressed)
            bothButton2.setBackgroundResource(R.drawable.textview_white_middle)
            bothButton3.setBackgroundResource(R.drawable.textview_white_right)

        }
        bothButton2.setOnClickListener {
            //if (user_sugar) makeSugarStat2Fragment(1) else if (user_pressrue) makePressureStat2Fragment(1)
            makeSugarStat2Fragment(1)
            bothButton1.setBackgroundResource(R.drawable.textview_white_left)
            bothButton2.setBackgroundResource(R.drawable.textview_white_middle_pressed)
            bothButton3.setBackgroundResource(R.drawable.textview_white_right)
        }
        bothButton3.setOnClickListener {
            //if (user_sugar) makeSugarStat2Fragment(1) else if (user_pressrue) makePressureStat2Fragment(1)
            makePressureStat1Fragment(1)
            bothButton1.setBackgroundResource(R.drawable.textview_white_left)
            bothButton2.setBackgroundResource(R.drawable.textview_white_middle)
            bothButton3.setBackgroundResource(R.drawable.textview_white_right_pressed)
        }

    }


    fun makeSugarStat2Fragment(num: Int) {//주간 혈당
        val stat2Fragment = supportFragmentManager.findFragmentByTag("stat2Frag")
//        if(stat2Fragment==null){//새로 생성
        val stat2Transaction = supportFragmentManager.beginTransaction()
        val stat2Frag = SugarStat2Fragment()
        stat2Transaction.replace(R.id.frame, stat2Frag, "sugar_stat2Frag")
        stat2Transaction.commit()
        stat1Flag = false
//        }else{//이미지만바꾸는거
//            //(stat2Fragment as Stat2Fragment).setTextView(num)
//        }
    }

    fun makeSugarStat1Fragment(num: Int) {//일간 혈당
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        val stat1Transaction = supportFragmentManager.beginTransaction()
        val stat1Frag = SugarStat1Fragment()
        stat1Transaction.replace(R.id.frame, stat1Frag, "sugar_stat1Frag")//태그값을 imgFrag로 준거임
        stat1Transaction.commit()
        stat1Flag = true
//            }else{//이미지만바꾸는거
//                //(stat1Fragment as Stat1Fragment).setImageView(num)
//            }
    }

    fun makePressureStat1Fragment(num: Int) {//주간그래프
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        val stat1Transaction = supportFragmentManager.beginTransaction()
        val stat1Frag = PressureStat1Fragment()
        stat1Transaction.replace(R.id.frame, stat1Frag, "press_stat1Frag")//태그값을 imgFrag로 준거임
        stat1Transaction.commit()
        stat1Flag = true
    }

    fun makePressureStat2Fragment(num: Int) {//주간그래프
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        val stat1Transaction = supportFragmentManager.beginTransaction()
        val stat1Frag = SugarStat1Fragment()
        stat1Transaction.replace(R.id.frame, stat1Frag, "press_stat1Frag")//태그값을 imgFrag로 준거임
        stat1Transaction.commit()
        stat1Flag = true
//            }else{//이미지만바꾸는거
//                //(stat1Fragment as Stat1Fragment).setImageView(num)
//            }
    }
}

