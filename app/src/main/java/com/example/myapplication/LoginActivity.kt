package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_init.*

class LoginActivity : AppCompatActivity() {
    lateinit var get: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        val regBtn = findViewById<Button>(R.id.button)
        val loginBtn = findViewById<Button>(R.id.button2)

        regBtn.setOnClickListener {
            val intent = Intent(applicationContext, RegActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            var idcheck = 0
            if (editText7.text != null && editText8.text != null) {
                val sortbyId = FirebaseDatabase.getInstance().getReference("/user/userinfo")
                sortbyId.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            var key = postSnapshot.key
                            get = postSnapshot.getValue(UserInfo::class.java)!!
                            var user_id = get?.id
                            var user_passwd = get?.passwd
                            if (editText7.text.toString() == user_id) {
                                if (editText8.text.toString() == user_passwd) {
                                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(editText8.getWindowToken(), 0)
                                    idcheck = 1
                                    //Toast.makeText(applicationContext, "로그인을 성공했습니다.", Toast.LENGTH_SHORT).show()
                                    finish();
                                    val intent = Intent(applicationContext, SplashActivity::class.java)
                                    intent.putExtra("user", get)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(applicationContext, "패스워드를 다시 입력해주시기 바랍니다.", Toast.LENGTH_SHORT)
                                        .show()
                                    return
                                }
                            }
                        }
                        if (idcheck == 0) {
                            Toast.makeText(applicationContext, "없는 아이디입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
        }

    }
}