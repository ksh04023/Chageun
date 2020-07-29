package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_alarm.*
import java.util.*

var timers: MutableMap<String, TimerTask> = mutableMapOf()
var timers2: MutableMap<String, Timer> = mutableMapOf()

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        init()
    }

    fun init() {
        val user_id = intent.getStringExtra("user_id")
        var week = booleanArrayOf(
            false, false, false, false, false, false, false
        )
        button7.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[0] = true
        }
        button8.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[1] = true
        }
        button9.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[2] = true
        }
        button10.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[3] = true
        }
        button11.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[4] = true
        }
        button12.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[5] = true
        }
        button13.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                week[6] = true
        }
        button14.setOnClickListener {
            var isRepeat = false
            var isNull = true
            var len = week.size
            for (i in 0..len - 1) {
                if (week[i]) {
                    isRepeat = true
                    break
                }
            }
            if (editText9.text.toString() == null) {
                Toast.makeText(applicationContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                isNull = false
            }
            if (isRepeat && !isNull) {
                val cal = Calendar.getInstance()
                week[cal.get(Calendar.DAY_OF_WEEK)]
                val curTime = Calendar.getInstance()
                for (i in 0..len - 1) {
                    if (week[i]) {
                        timers[editText9.text.toString()] = object : TimerTask() {
                            override fun run() {
                                makeNotification()
                            }
                        }

                        val atime = System.currentTimeMillis()
                        curTime.set(Calendar.DAY_OF_WEEK, i + 1)
                        curTime.set(Calendar.HOUR_OF_DAY, this.timePicker.hour)
                        curTime.set(Calendar.MINUTE, this.timePicker.minute)
                        curTime.set(Calendar.SECOND, 0)
                        curTime.set(Calendar.MILLISECOND, 0)
                        val btime = curTime.getTimeInMillis()
                        var triggerTime = btime
                        if (atime > btime)
                            triggerTime += (1000 * 60 * 60 * 24 * 7).toLong()
                        timers2[editText9.text.toString()] = Timer()
                        timers2[editText9.text.toString()]!!.schedule(
                            timers[editText9.text.toString()],
                            triggerTime - atime,
                            1000 * 60 * 60 * 24 * 7
                        )

                    }
                }
                var database =
                    FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id).child("alarm")
                        .child(editText9.text.toString())
                var alarm_days: String = ""
                if (week[0] == true)
                    alarm_days += "일,"
                if (week[1] == true)
                    alarm_days += "월,"
                if (week[2] == true)
                    alarm_days += "화,"
                if (week[3] == true)
                    alarm_days += "수,"
                if (week[4] == true)
                    alarm_days += "목,"
                if (week[5] == true)
                    alarm_days += "금,"
                if (week[6] == true)
                    alarm_days += "토,"
                database.child("name").setValue(editText9.text.toString())
                database.child("days").setValue(alarm_days)
                database.child("times")
                    .setValue(timePicker.hour.toString() + "시" + timePicker.minute.toString() + "분")
                Toast.makeText(this, "알람을 설정했습니다.", Toast.LENGTH_SHORT).show()
                finish()

            } else if(!isRepeat&&!isNull){
                timers[editText9.text.toString()] = object : TimerTask() {
                    override fun run() {
                        makeNotification()
                    }
                }

                val atime = System.currentTimeMillis()
                val curTime = Calendar.getInstance()
                curTime.set(Calendar.HOUR_OF_DAY, this.timePicker.hour)
                curTime.set(Calendar.MINUTE, this.timePicker.minute)
                curTime.set(Calendar.SECOND, 0)
                curTime.set(Calendar.MILLISECOND, 0)
                val btime = curTime.getTimeInMillis()
                var triggerTime = btime
                if (atime > btime)
                    triggerTime += (1000 * 60 * 60 * 24).toLong()
                timers2[editText9.text.toString()] = Timer()
                timers2[editText9.text.toString()]!!.schedule(
                    timers[editText9.text.toString()],
                    triggerTime - atime
                )

                var database =
                    FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id).child("alarm")
                        .child(editText9.text.toString())
                database.child("name").setValue(editText9.text.toString())
                when (curTime.get(Calendar.DAY_OF_WEEK)) {
                    1 -> database.child("days").setValue("일")
                    2 -> database.child("days").setValue("월")
                    3 -> database.child("days").setValue("화")
                    4 -> database.child("days").setValue("수")
                    5 -> database.child("days").setValue("목")
                    6 -> database.child("days").setValue("금")
                    7 -> database.child("days").setValue("토")
                }
                database.child("times")
                    .setValue(timePicker.hour.toString() + "시" + timePicker.minute.toString() + "분")
                Toast.makeText(this, "알람을 설정했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ShowAlarmActivity::class.java);
                intent.putExtra("user_id",user_id)
                startActivity(intent)
            }

        }

    }

    fun makeNotification() {
        val CHANNELID = "Notification1"
        val notificationChannel = NotificationChannel(
            CHANNELID,
            "Time Check Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE

        val soundUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION)
        val audioBuilder = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build()
        notificationChannel.setSound(soundUri, audioBuilder)

        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val hour = timePicker.hour
        val minute = timePicker.minute
        val alramTime = hour.toString() + "시 " + minute.toString() + "분입니다."

        val builder = Notification.Builder(this, CHANNELID)
        builder.setSmallIcon(R.drawable.medicine)
        builder.setContentTitle(editText9.text.toString())
        builder.setContentText(alramTime)
        builder.setSmallIcon(R.drawable.medicine)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.medicine))
        builder.setAutoCancel(true)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("time", alramTime)

        val pIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)

        val notification = builder.build()
        manager.notify(2, notification)

    }

}