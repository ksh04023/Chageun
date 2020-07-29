package com.example.myapplication


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.

 */
class SugarStat2Fragment : Fragment() {
    lateinit var day: String
    lateinit var month: String
    lateinit var date_today: String
    lateinit var chart: LineChart
    lateinit var average: ArrayList<Float>
    lateinit var dateSpinner:Spinner
    lateinit var tempEndDate:LocalDate
    lateinit var tempStartDate:LocalDate

    var valueArr: MutableList<MutableList<Int>> = ArrayList(7)

    var dateArr = ArrayList<String>()
    var statArr = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sugar_stat2, container, false)
        chart = view!!.findViewById(R.id.linechart2)
        dateSpinner = view!!.findViewById(R.id.dateSpinner1)
        init()
        return view
    }
    fun spinnerAddListener() {
        dateSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selected = parent!!.getItemAtPosition(position).toString()
                var start = selected.substring(0..1)+selected.substring(3..4)
                var month = selected.substring(0..1)
                var day = selected.substring(3..4)
//                if (day.startsWith("0")){
//                    day = day.split("0")[1]
//                }
                var year = LocalDate.now().year.toString()

                var startDate = LocalDate.parse(year +"-"+month+"-"+day)
                var startD = startDate.format(DateTimeFormatter.ofPattern("MMdd"))
                dateArr = ArrayList()
                for (i in 0..6) {
                    var temp = startDate.plusDays(i.toLong())
                    var temps = temp.format(DateTimeFormatter.ofPattern("MMdd"))
                    dateArr.add(temps)
                    Log.d("dateArr", dateArr[i])
                }
                initChart()
            }
        }
    }
    fun init() {
        for(i in 0..6){
            valueArr.add(ArrayList())
        }
        setDate()
        initChart()
        addItemDateSpinner()
    }

    fun initChart() {
        var user_id = "greenjoa"
        val dataRef = FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id + "/bloodSugar")
        Log.d("breakfast", "yes")
        dataRef.child(date_today).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //var user_gender = dataSnapshot.child("sexuality").getValue().toString()
                //befor breakfast


                val sugarData = FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id + "/bloodSugar")
                sugarData.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for(i in dateArr){
                            Log.d("dataSnapshot", dataSnapshot.child(i).exists().toString())
                            if (dataSnapshot.child(i).exists()){
                                for (mealSnapshot in dataSnapshot.child(i).children) {//아침점심저녁
                                    Log.d("value",mealSnapshot.key.toString())
                                    for (snapshot in mealSnapshot.children) {
                                        when (mealSnapshot.key) {
                                            "아침" -> {
                                                if (snapshot.key == "식전")
                                                    valueArr[0].add(snapshot.getValue(Long::class.java)!!.toInt())
                                                if (snapshot.key == "식후")
                                                    valueArr[1].add(snapshot.getValue(Long::class.java)!!.toInt())
                                                Log.d("valueArr","아침")
                                            }
                                            "점심" -> {
                                                if (snapshot.key == "식전")
                                                    valueArr[2].add(snapshot.getValue(Long::class.java)!!.toInt())
                                                if (snapshot.key == "식후")
                                                    valueArr[3].add(snapshot.getValue(Long::class.java)!!.toInt())
                                                Log.d("valueArr","점심")

                                            }
                                            "저녁" -> {
                                                if (snapshot.key == "식전")
                                                    valueArr[4].add(snapshot.getValue(Long::class.java)!!.toInt())
                                                if (snapshot.key == "식후")
                                                    valueArr[5].add(snapshot.getValue(Long::class.java)!!.toInt())
                                                Log.d("valueArr","저녁")

                                            }
                                        }
                                    }
                                    if (mealSnapshot.key == "취침전")
                                        valueArr[6].add(mealSnapshot.getValue(Long::class.java)!!.toInt())
                                }
                            }
                            setData()
                        }

                    }
                })

            }
        })

        //val l = chart.getLegend()
    }
    fun addItemDateSpinner(){
        val adapter = ArrayAdapter<String>(activity!!.baseContext,
            android.R.layout.simple_spinner_dropdown_item,ArrayList<String>())

        var now = LocalDate.now()

        for(i in 0..30){
            tempEndDate = now.minusDays(((7*i+1).toLong()))
            tempStartDate = now.minusDays((7*i+7).toLong())
            adapter.add(tempStartDate.format(DateTimeFormatter.ofPattern("MM/dd")) + "~"+tempEndDate.format(DateTimeFormatter.ofPattern("MM/dd")))
        }

        dateSpinner.adapter = adapter

    }
    fun setDate() {
        val instance = Calendar.getInstance()//오늘날짜설정하기
        day = instance.get(Calendar.DAY_OF_MONTH).toString()
        if (day.length == 1) {
            day = "0" + day
            Log.d("database_day", day.toString())
        }
        month = (instance.get(Calendar.MONTH) + 1).toString()
        if (month.length == 1) {
            month = "0" + month
            Log.d("database_month", month.toString())
        }
        date_today = month + day

        for (i in 7.downTo(1)) {
            var now = LocalDate.now()
            var Strnow = now.format(DateTimeFormatter.ofPattern("MMdd"))
            var tempDate = now.minusDays(i.toLong()).format(DateTimeFormatter.ofPattern("MMdd"))
            dateArr.add(tempDate.toString())
            Log.d("dateArr",tempDate.toString())
        }
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
        var dataVals = ArrayList<Entry>()
        average = ArrayList<Float>()

        Log.d("value",valueArr.size.toString())
        for (i in 0..valueArr.size-1) {
            var tempVal = 0f
            for(j in valueArr.get(i)){
                tempVal += j
            }
            if(tempVal!= null)
                tempVal /= valueArr.get(i).size
            tempVal = String.format("%.2f",tempVal).toFloat()
            average.add(tempVal)
        }
        for(i in average){
            Log.d("average",i.toString())
        }
        dataVals.add(Entry(0f, average.get(0)))
        dataVals.add(Entry(1f, average.get(1)))
        dataVals.add(Entry(2f, average.get(2)))
        dataVals.add(Entry(3f, average.get(3)))
        dataVals.add(Entry(4f, average.get(4)))
        dataVals.add(Entry(5f, average.get(5)))
        dataVals.add(Entry(6f, average.get(6)))

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

        var dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet1)
        var data = LineData(dataSets)
        chart.data = data

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.textSize = 10f
        xAxis.setLabelRotationAngle(45f)
        xAxis.granularity = 1f
        val xLabel = setXAxisValues()
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            xLabel[value.toInt()]
        }
        //xAxis.textColor = ContextCompat.getColor(context!!,R.color.abc_hint_foreground_material_dark)
        xAxis.setLabelCount(7, true)


        val yLeft = chart.axisLeft // y축 왼쪽 데이터 가져오기.
        yLeft.setTextColor(Color.BLACK); // y축 텍스트 컬러 설정
        //yLeft.valueFormatter = yAxisValueFormatter(yAxisValue)
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
        spinnerAddListener()

    }
}
