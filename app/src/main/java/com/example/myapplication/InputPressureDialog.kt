package com.example.myapplication


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_input_pressure_dialog.*
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class InputPressureDialog : Fragment() {
    lateinit var user: UserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_pressure_dialog, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //fragment가 액티비티에 잘 붙고, 액티비티가 잘 만들어진 상태
        super.onActivityCreated(savedInstanceState)
        setView()
    }

    fun setView() {
        if (activity != null) {
            val intent = activity!!.intent
            if (intent != null) {
                user = intent.getSerializableExtra("user") as UserInfo
            }
        }
        val instance = Calendar.getInstance()//오늘날짜설정하기
        var day = instance.get(Calendar.DAY_OF_MONTH).toString()
        if (day.length == 1) {
            day = "0" + day
            Log.d("database_day", day.toString())
        }
        var month = (instance.get(Calendar.MONTH) + 1).toString()
        if (month.length == 1) {
            month = "0" + month
            Log.d("database_month", month.toString())

        }

        var date_today = month + day
        inputPressure.setOnClickListener {
            var database =
                FirebaseDatabase.getInstance()
                    .getReference("/user/userinfo/" + user.id + "/bloodPressure/" + date_today)
            database.child("수축기").setValue(sbp_num.text.toString())
            database.child("이완기").setValue(dbp_num.text.toString())
        }
    }

}

