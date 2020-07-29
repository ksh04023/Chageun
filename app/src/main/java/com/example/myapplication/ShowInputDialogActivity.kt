package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_show_input_dialog.*

class ShowInputDialogActivity : AppCompatActivity() {
    var time1: String = ""
    var time2: String = ""
    lateinit var user: UserInfo
    var kind_diseases = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_input_dialog)
        init()
    }

    fun init() {
        setToggle()
    }


    fun setToggle() {
        user = intent.getSerializableExtra("user") as UserInfo

        if (user.diseases == "없음" || user.diseases == "당뇨고혈압" || user.diseases == "당뇨,고혈압") {
            kind_diseases = 2 //당뇨병, 고혈압 둘다있는 경우, 없음
        } else if (user.diseases == "당뇨") {
            kind_diseases = 1 //당뇨병
        } else if (user.diseases == "고혈압" || user.diseases == ",고혈압") {
            kind_diseases = 0 //고혈압
        }
        when (kind_diseases) {
            0 -> { //고혈압
//                toggle.visibility = View.INVISIBLE
//                toggle.isClickable = false
                pressureB.visibility = View.INVISIBLE
                pressureB.isClickable = false
                val transaction = supportFragmentManager.beginTransaction()
                val frag = InputPressureDialog()
                transaction.replace(R.id.frame, frag)
                transaction.commit()
            }
            1 -> { //당뇨
//                toggle.visibility = View.INVISIBLE
//                toggle.isClickable = false
                sugarB.visibility = View.INVISIBLE
                sugarB.isClickable = false
                val transaction = supportFragmentManager.beginTransaction()
                val frag = InputSugarDialog()
                transaction.replace(R.id.frame, frag)
                transaction.commit()
            }
            2 -> {
//                if (toggle.isChecked) {
//                    toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on))
//                    val transaction = supportFragmentManager.beginTransaction()
//                    val frag = InputSugarDialog()
//                    transaction.replace(R.id.frame, frag)
//                    transaction.commit()
//                } else {
//                    toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off))
//                    val transaction = supportFragmentManager.beginTransaction()
//                    val frag = InputPressureDialog()
//                    transaction.replace(R.id.frame, frag)
//                    transaction.commit()
//                }
//                toggle.setOnClickListener {
//                    if (toggle.isChecked) { //당뇨가 입력되어 있는 상태
//                        toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on))
//
//                    } else { //혈압이 입력되어 있는 상태
//                        toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off))
//                        val transaction = supportFragmentManager.beginTransaction()
//                        val frag = InputPressureDialog()
//                        transaction.replace(R.id.frame, frag)
//                        transaction.commit()
//                    }
//                }
                val transaction = supportFragmentManager.beginTransaction()
                val frag = InputSugarDialog()
                transaction.replace(R.id.frame, frag)
                transaction.commit()
                sugarB.setBackgroundResource(R.drawable.textview_white_left_pressed)
                pressureB.setBackgroundResource(R.drawable.textview_white_right)
                sugarB.setOnClickListener {
                    //if (user_sugar) makeSugarStat1Fragment(1) else if (user_pressrue) makePressureStat1Fragment(1)
                    val transaction = supportFragmentManager.beginTransaction()
                    val frag = InputSugarDialog()
                    transaction.replace(R.id.frame, frag)
                    transaction.commit()
                    sugarB.setBackgroundResource(R.drawable.textview_white_left_pressed)
                    pressureB.setBackgroundResource(R.drawable.textview_white_right)

                }
                pressureB.setOnClickListener {
                    //if (user_sugar) makeSugarStat2Fragment(1) else if (user_pressrue) makePressureStat2Fragment(1)
                    val transaction = supportFragmentManager.beginTransaction()
                    val frag = InputPressureDialog()
                    transaction.replace(R.id.frame, frag)
                    transaction.commit()
                    sugarB.setBackgroundResource(R.drawable.textview_white_left)
                    pressureB.setBackgroundResource(R.drawable.textview_white_right_pressed)
                }
            }
        }
    }
}