package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AlarmAdapter(context: Context, val resouce: Int, var list: ArrayList<alarm>, var user_id: String) :
    ArrayAdapter<alarm>(context, resouce, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //데이터만 교체하면 되면 교체하는구문이 실행되고, 생성해야 하면 생성해줘야 한다.
        var v: View? = convertView
        if (v == null) {
            val vi = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            //layout inflater객체 얻음
            v = vi.inflate(R.layout.alarm_row, null)
        } //없으면 layout 객체에 해당하는 것을 새로 만든다.
        val p = list.get(position) //mywords에서 하나 가져온다.
        v!!.findViewById<TextView>(R.id.alarm_name).text = p.name //!!의미는 null이 아니면

        var p_days = p.days.split(",")

        if (p_days.size == 1) {
            v!!.findViewById<TextView>(R.id.days).text = p_days[0]
        } else {
            var p_str = ""
            for (i in 0 until p_days.size - 1) {
                if(i == p_days.size -1 )
                    p_str += p_days[i]
                else
                    p_str += p_days[i] + ","
            }
            //p_str += p_days[p_days.size - 1]
            v!!.findViewById<TextView>(R.id.days).text = p_str
        }

        v!!.findViewById<TextView>(R.id.time).text = p.time



        v!!.findViewById<Button>(R.id.deletebtn).setOnClickListener {
            if (timers[p.name] != null) {
                timers[p.name]?.cancel()

            }
            if (timers2[p.name] != null) {
                timers2[p.name]?.cancel()

            }
            val delete = FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id).child("alarm")
            delete.child(p.name).setValue(null)
            notifyDataSetChanged()
        }

        //생성하고 listener를 adpater에서 달아주면 된다.

        return v //한줄이 채워진 상태,view의 값을 반환한다
        //return super.getView(position, convertView, parent)
    }//

}