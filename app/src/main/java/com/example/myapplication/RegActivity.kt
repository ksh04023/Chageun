package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_reg.*

class RegActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
        init()
    }

    fun init() {
        var bool = true
        button3.setOnClickListener {
            FirebaseApp.initializeApp(this)
            val sortbyId = FirebaseDatabase.getInstance().getReference("/user/userinfo")

            sortbyId.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        var key = postSnapshot.key
                        var get = postSnapshot.getValue(UserInfo::class.java)
                        var user_id = get?.id
                        if (editId.text.toString() == user_id) {
                            Toast.makeText(applicationContext, "중복입니다", Toast.LENGTH_SHORT).show()
                            bool = false
                        } else {
                            Toast.makeText(applicationContext, "사용 가능합니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
        button4.setOnClickListener {
            var diseases = ""
            if (check1.isChecked) {
                diseases += "당뇨"
            }
            if (check2.isChecked) {
                if (diseases == "") {
                    diseases += ",고혈압"
                } else {
                    diseases += "고혈압"
                }

            }
            if (check3.isChecked) {
                diseases = "없음"
            }
            if(editPass.text.length < 4){
                Toast.makeText(this,"비밀번호는 4자 이상입니다",Toast.LENGTH_SHORT).show()
            }
            if (bool == true && editId.text.toString() != "" && editPass.text.toString() != "" && editName.text.toString() != "" && editAge.text.toString() != "" && editWeight.text.toString() != "" && editHeight.text.toString() != "") {
                var database =
                    FirebaseDatabase.getInstance().getReference("/user/userinfo").child(editId.text.toString())
                var new_user: UserInfo = UserInfo(
                    editId.text.toString(),
                    editPass.text.toString(),
                    editName.text.toString(),
                    editAge.text.toString(),
                    editWeight.text.toString(),
                    editHeight.text.toString(),
                    diseases,
                    toggleButton.text.toString()
                )
                database.setValue(new_user)

                Toast.makeText(this, "회원가입을 완료했습니다", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(applicationContext, "빠진 부분이 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
