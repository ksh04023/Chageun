package com.example.myapplication


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_input_sugar_dialog.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class InputSugarDialog : Fragment() {
    lateinit var user: UserInfo

    var time1: String = ""
    var time2: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_sugar_dialog, container, false)
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

        val instance = Calendar.getInstance()
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

        mealGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_breakfast -> {
                    time1 = "아침"
                    whenGroup.visibility = View.VISIBLE
                }
                R.id.radio_lunch -> {
                    time1 = "점심"
                    whenGroup.visibility = View.VISIBLE
                }
                R.id.radio_dinner -> {
                    time1 = "저녁"
                    whenGroup.visibility = View.VISIBLE
                }
                R.id.radio_sleeping -> {
                    time1 = "취침전"
                    whenGroup.visibility = View.INVISIBLE
                }
            }
        }
        whenGroup.setOnCheckedChangeListener { group, checkedId ->
            if (whenGroup.visibility == View.VISIBLE) {
                when (checkedId) {
                    R.id.before_eating -> {
                        time2 = "식전"
                    }
                    R.id.after_eating -> {
                        time2 = "식후"
                    }
                }
            }
        }
        var date_today = month + day

        inputSugar.setOnClickListener {
            var database =
                FirebaseDatabase.getInstance()
                    .getReference("/user/userinfo/" + user.id + "/bloodSugar/" + date_today + "/" + time1)
            if (time2 == "") { //radio_sleeping
                database.setValue(bloodSugar.text.toString())
            } else { //아침, 점심, 저녁 일 때
                database.updateChildren(mapOf(Pair(time2, bloodSugar.text.toString())))
            }
        }
    }


}