package com.example.myapplication


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
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
class PressureStat1Fragment : Fragment() {
    lateinit var day: String
    lateinit var month: String
    lateinit var date_today: String
    lateinit var chart: LineChart
    lateinit var average: ArrayList<Float>
    lateinit var dateSpinner: Spinner
    lateinit var valueArr: MutableList<Int>
    var dateArr = ArrayList<String>()
    var statArr = ArrayList<Int>()
    lateinit var xVals: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pressure_stat1, container, false)
        chart = view!!.findViewById(R.id.linechart3) as LineChart
        dateSpinner = view!!.findViewById(R.id.dateSpinner2)
        init()
        return view
    }

    fun init() {
        setDate()
        initChart()
        addItemDateSpinner()
    }

    fun initChart() {
        var user_id = "greenjoa"
        val dataRef = FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id + "/bloodPressure")
        Log.d("dataRef", dataRef.key)
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                valueArr = ArrayList()
                Log.d("dataSnapshot.key", dataSnapshot.key)
                Log.d("dataSnapshot.size", dateArr.size.toString())
                for (i in dateArr) {
                    Log.d("dataSnapshot2.key", dataSnapshot.key)
                    Log.d("dataSnapshot", dataSnapshot.child(i).exists().toString())
                    if (dataSnapshot.child(i).exists()) {//날짜존재
                        for (stateSnapshot in dataSnapshot.child(i).children) {//수축기이완기
                            Log.d("dataSnapshot.stateSnapshot", stateSnapshot.key)
                            when (stateSnapshot.key) {
                                "수축기" -> {
                                    valueArr.add(stateSnapshot.getValue(Long::class.java)!!.toInt())
                                    Log.d("valueArr", "수축기" + i + valueArr.get(valueArr.size-1).toString())
                                }
                                "이완기" -> {
                                    valueArr.add(stateSnapshot.getValue(Long::class.java)!!.toInt())
                                    Log.d("valueArr", "이완기"+i + valueArr.get(valueArr.size.toInt()-1).toString())
                                }
                            }
                        }
                    }else{
                        valueArr.add(0)
                    }
                }
                setData()
            }
        })
    }


    fun addItemDateSpinner() {
        val adapter = ArrayAdapter<String>(
            activity!!.baseContext,
            android.R.layout.simple_spinner_dropdown_item, ArrayList<String>()
        )
        var now = LocalDate.now()

        for (i in 0..30){
            var tempStartDate = now.minusDays((7*i+6).toLong()).format(DateTimeFormatter.ofPattern("MM/dd"))
            var tempEndDate = now.minusDays((7*i).toLong()).format(DateTimeFormatter.ofPattern("MM/dd"))
            adapter.add(tempStartDate.toString() + "~" + tempEndDate.toString())
        }
        dateSpinner.adapter = adapter
    }

    private fun setXAxisValues(): ArrayList<String> {
        xVals = ArrayList()
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
        for(i in 0..6){
            xVals.add(dateArr[i]+"이완")
            xVals.add(dateArr[i]+"수축")
            Log.d("xVals",xVals[i*2] + "/" + xVals[i*2+1])

        }
        return xVals
    }

    fun spinnerAddListener() {
        dateSpinner.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selected = parent!!.getItemAtPosition(position).toString()
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

    fun setDate(){
        var now = LocalDate.now()
        var Strnow = now.format(DateTimeFormatter.ofPattern("MM/dd"))
        for (i in 6.downTo(0)) {
            var tempDate = now.minusDays(i.toLong()).format(DateTimeFormatter.ofPattern("MMdd"))
            dateArr.add(tempDate.toString())
            Log.d("dateArr", tempDate.toString())
        }
    }

    fun dataValues1(): ArrayList<Entry> {
        var dataVals = ArrayList<Entry>()
        for (i in 0 until valueArr.size) {
            dataVals.add(Entry(i.toFloat(), valueArr.get(i).toFloat()))
            Log.d("value", i.toFloat().toString() + valueArr.get(i).toFloat().toString())

        }
        return dataVals
    }
    private fun setData() {
        chart.setBorderColor(Color.DKGRAY)
        val lineDataSet1 = LineDataSet(dataValues1(), "혈압")
        lineDataSet1.color = Color.BLUE
        lineDataSet1.circleRadius = 3f
        lineDataSet1.setDrawCircleHole(false)
        lineDataSet1.valueTextSize = 10f
        lineDataSet1.setCircleColor(Color.BLACK)

        var dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet1)
        var data = LineData(dataSets)
        chart.data = data
        chart.description = null
//        //val xAxis = chart.xAxis
//        xAxis.position = XAxis.XAxisPosition.TOP
//        xAxis.textSize = 20f
//        //xAxis.labelRotationAngle = 30f
//        val xLabel = setXAxisValues()
//        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
//            xLabel[value.toInt()]
//        }
        val xAxisBottom = chart.xAxis
        xAxisBottom.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        val xLabel = setXAxisValues()
        for(i in 0..13){
            Log.d("xLavel",xLabel[i].toString())

        }
        xAxisBottom.valueFormatter = IAxisValueFormatter{value, axis ->
            var t = value
            //Log.d("floatValue",t.toString().format(0.2f))
            var temp = xLabel[t.toInt()]
            var tmonth = temp.substring(0..1).split("0")[1]
            var tday = temp.substring(2..3)
            var iwan = temp.substring(4..5)
            if (tday.startsWith("0")){
                    tday.split("0")[1]
                }
            "$tmonth/$tday$iwan"
        }
        xAxisBottom.labelRotationAngle = 90f

        //xAxis.textColor = ContextCompat.getColor(context!!,R.color.abc_hint_foreground_material_dark)
        xAxisBottom.setLabelCount(dataValues1().size, true)
        xAxisBottom.granularity = 1f
        val yLeft = chart.axisLeft // y축 왼쪽 데이터 가져오기.
        yLeft.setTextColor(Color.BLACK); // y축 텍스트 컬러 설정
        //yLeft.valueFormatter = yAxisValueFormatter(yAxisValue)

// y축 오른쪽 비활성화
        val yRight = chart.axisRight
        yRight.setDrawLabels(false)
        yRight.setDrawAxisLine(false)
        yRight.setDrawGridLines(false)
        val ll = LimitLine(140f, "수축기 위험")
        ll.lineColor = Color.RED
        ll.lineWidth = 2f
        ll.textColor = Color.RED
        ll.textSize = 12f
// .. and more styling options

        yLeft.addLimitLine(ll)

        val ll2 = LimitLine(90f, "이완기 위험")
        ll2.lineColor = Color.RED
        ll2.lineWidth = 2f
        ll2.textColor = Color.RED
        ll2.textSize = 12f
// .. and more styling options

        yLeft.addLimitLine(ll2)
        chart.invalidate()
        spinnerAddListener()

    }
}
