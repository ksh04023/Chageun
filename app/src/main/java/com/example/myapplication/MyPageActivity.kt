package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import com.github.mikephil.charting.charts.LineChart
import kotlinx.android.synthetic.main.activity_mypage.*

class MyPageActivity : AppCompatActivity() {
    lateinit var chart: LineChart
    var stat1Flag = true
    var imgNum = 0
    lateinit var user:UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
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
        val button1 = findViewById<Button>(R.id.buttonStat1)
        val button2 = findViewById<Button>(R.id.buttonStat2)
        button1.setOnClickListener {
            //if (user_sugar) makeSugarStat1Fragment(1) else if (user_pressrue) makePressureStat1Fragment(1)
            makeSugarStat1Fragment(1)
            button1.setBackgroundResource(R.drawable.textview_white_left_pressed)
            button2.setBackgroundResource(R.drawable.textview_white_right)

        }
        button2.setOnClickListener {
            //if (user_sugar) makeSugarStat2Fragment(1) else if (user_pressrue) makePressureStat2Fragment(1)
            makeSugarStat2Fragment(1)
            button1.setBackgroundResource(R.drawable.textview_white_left)
            button2.setBackgroundResource(R.drawable.textview_white_right_pressed)
        }

    }

    /*
    fun setUserData() {
        var user_name = "김지효"
        var user_id = "greenjoa"

        val userName = findViewById<TextView>(R.id.userName2)
        userName.text = user_name
        val userID = findViewById<TextView>(R.id.userID2)
        userID.text = user_id
        val dataRef = FirebaseDatabase.getInstance().getReference("/user/userinfo")
        dataRef.child(user_id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userGender = findViewById<TextView>(R.id.userGender)
                val userAge = findViewById<TextView>(R.id.userAge)
                val userHeight = findViewById<TextView>(R.id.userHeight)
                val userWeight = findViewById<TextView>(R.id.userWeight)
                var user_gender = dataSnapshot.child("sexuality").getValue().toString()
                var user_age = dataSnapshot.child("age").getValue().toString()
                var user_height = dataSnapshot.child("height").getValue().toString()
                var user_weight = dataSnapshot.child("weight").getValue().toString()

                userGender?.setText(user_gender)
                userAge?.text = user_age
                userHeight?.text = user_height
                userWeight.text = user_weight
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }*/

    fun makeSugarStat2Fragment(num: Int) {//월별그래프
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

    fun makeSugarStat1Fragment(num: Int) {//주간그래프
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
        val stat1Frag = SugarStat1Fragment()
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

