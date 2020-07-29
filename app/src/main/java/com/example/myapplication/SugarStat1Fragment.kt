package com.example.myapplication


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList




class SugarStat1Fragment : Fragment() {
    lateinit var chart: LineChart
    var today = ArrayList<Int>()
    lateinit var todayDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sugar_stat1, container, false)
        chart = view!!.findViewById(R.id.linechart1) as LineChart
        init()
        return view
    }

    fun init() {
        setDate()
        initChart()
    }

    fun initChart() {
        val user_id = "greenjoa"
        val dataRef = FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id + "/bloodSugar")
        Log.d("breakfast", "yes")
        dataRef.child(todayDate).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //var user_gender = dataSnapshot.child("sexuality").getValue().toString()
                //befor breakfast

                val beforeB = dataSnapshot.child("아침").child("식전").value?.toString()
                val afterB = dataSnapshot.child("아침").child("식후").value?.toString()
                val beforeL = dataSnapshot.child("점심").child("식전").value?.toString()
                val afterL = dataSnapshot.child("점심").child("식후").value?.toString()
                val beforeD = dataSnapshot.child("저녁").child("식전").value?.toString()
                val afterD = dataSnapshot.child("저녁").child("식후").value?.toString()
                val beforeS = dataSnapshot.child("취침전").value?.toString()
                if (beforeB != null)
                    today.add(beforeB.toInt())
                else {
                    today.add(0)
                }
                if (afterB != null)
                    today.add(afterB.toInt())
                else {
                    today.add(0)
                }
                if (beforeL != null) {
                    Log.d("before", "is not null")
                    Log.d("before", beforeL)
                    today.add(beforeL.toInt())
                } else {
                    today.add(0)
                }

                if (afterL != null)
                    today.add(afterL.toInt())
                else {
                    today.add(0)
                }
                if (beforeD != null)
                    today.add(beforeD.toInt())
                else {
                    today.add(0)
                }
                if (afterD != null)
                    today.add(afterD.toInt())
                else {
                    today.add(0)
                }
                if (beforeS != null)
                    today.add(beforeS.toInt())
                else {
                    today.add(0)
                }
                setData()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //val l = chart.getLegend()
    }


    fun setDate() {
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
        todayDate = month + day
    }

    private fun setXAxisValues(): ArrayList<String> {
        val xVals = ArrayList<String>()
        xVals.add("아침 식전")
        xVals.add("아침 식후")
        xVals.add("점심 식전")
        xVals.add("점심 식후")
        xVals.add("저녁 식전")
        xVals.add("저녁 식후")
        xVals.add("취침 전")
        return xVals
    }

    fun dataValues1(): ArrayList<Entry> {
        val dataVals = ArrayList<Entry>()
        Log.d("todaySize", today.size.toString())
        for (i in 0 until today.size) {
            dataVals.add(Entry(i.toFloat(), today[i].toFloat()))
            Log.d("datvalue", dataVals[i].toString())
        }

    return dataVals
}

private fun setData() {
    chart.setBorderColor(Color.DKGRAY)
    val lineDataSet1 = LineDataSet(dataValues1(), "혈당")
    lineDataSet1.color = Color.BLUE
    lineDataSet1.circleRadius = 3f
    lineDataSet1.setDrawCircleHole(false)
    lineDataSet1.valueTextSize = 10f
    lineDataSet1.setCircleColor(Color.BLACK)
    val dataSets = ArrayList<ILineDataSet>()
    dataSets.add(lineDataSet1)
    Log.d("chart", "dataset")
    val data = LineData(dataSets)
    chart.data = data

    val xAxis = chart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
    xAxis.textSize = 10f
    xAxis.setLabelRotationAngle(45f)
    //xAxis.textColor = ContextCompat.getColor(context!!,R.color.abc_hint_foreground_material_dark)
    xAxis.setLabelCount(7, true)
    val xLabel = setXAxisValues()
    xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
        xLabel[value.toInt()]
    }
    xAxis.granularity = 1f
    val yLeft = chart.axisLeft // y축 왼쪽 데이터 가져오기.
    yLeft.setTextColor(Color.BLACK) // y축 텍스트 컬러 설정
    yLeft.textSize = 12f

    val ll = LimitLine(150f, "위험")
    ll.lineColor = Color.RED
    ll.lineWidth = 2f
    ll.textColor = Color.RED
    ll.textSize = 12f
// .. and more styling options

    yLeft.addLimitLine(ll)

// y축 오른쪽 비활성화
    val yRight = chart.axisRight
    yRight.setDrawLabels(false)
    yRight.setDrawAxisLine(false)
    yRight.setDrawGridLines(false)
    chart.invalidate()
}
}

