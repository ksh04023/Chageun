package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_show_alarm.*

class ShowAlarmActivity : AppCompatActivity() {
    var list: ArrayList<alarm> = arrayListOf()
    lateinit var adapter: AlarmAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_alarm)
        init()
    }

    fun init() {
        val user_id=intent.getStringExtra("user_id")

        alarmbtn.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java);
            intent.putExtra("user_id",user_id)
            startActivity(intent) //로그인
        }
        FirebaseApp.initializeApp(this)
        val sort = FirebaseDatabase.getInstance().getReference("/user/userinfo/"+user_id).child("alarm")
        adapter = AlarmAdapter(this, R.layout.alarm_row, list,user_id)
        sort.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapter.clear()
                for (postSnapshot in dataSnapshot.children) {
                    var key = postSnapshot.key
                    var name = postSnapshot.child("name").getValue(String::class.java)
                    var days = postSnapshot.child("days").getValue(String::class.java)
                    var times = postSnapshot.child("times").getValue(String::class.java)
                    if(times!=null){
                        var data = alarm(name!!, days!!, times!!)
                        list.add(data)
                    }
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        listView.adapter = adapter
    }
}