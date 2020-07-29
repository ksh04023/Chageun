package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_food_table.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FoodTableActivity : AppCompatActivity() {
    lateinit var user_id:String
    lateinit var date_today:String
    lateinit var meal:String
    lateinit var amount:String
    lateinit var input:String
    lateinit var day:String
    lateinit var month:String
    private var f_name : String? = ""
    private var f_info :FoodNutri? = null
    private var todayfood : FoodTable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_table)
        todayfood = intent?.getSerializableExtra("todayfood") as? FoodTable
        user_id = intent.getStringExtra("userId")
        f_info = intent?.getSerializableExtra("food_info") as? FoodNutri
        f_name = f_info?.f_name
        init()
    }
    fun init(){
        var today:String = "\n<" + todayfood?.date + ">\n\n 아침 : "
        if(todayfood?.breakfast != null)
            today = today + todayfood?.breakfast + ")\n 점심 : "
        else
            today = today + todayfood?.breakfast + "\n 점심 : "
        if(todayfood?.launch?.size != 0)
            today = today + todayfood?.launch + ")\n 저녁 : "
        else
            today = today + todayfood?.launch + "\n 저녁 : "
        if(todayfood?.dinner?.size != 0)
            today = today + todayfood?.dinner + ")\n 간식 : "
        else
            today = today + todayfood?.dinner + "\n 간식 : "
        if(todayfood?.snack?.size != 0)
            today = today + todayfood?.snack + ")\n\n"
        else
            today = today + todayfood?.snack + "\n\n"

        today = today.replace("{","",true)
        today = today.replace("}","",true)
        today = today.replace("=", " (", true)
        if(todayfood == null)
            foodTable.text = "\n오늘의 식단을 추가해주세요!"
        else{
            foodTable.text = today
        }

        searchfood.setText(f_name)
        setDate()
        addItemMealSpinner()
        addItemAmountSpinner()
        addFoodToTable()
    }
    fun addItemMealSpinner(){
        val adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,
            ArrayList<String>()
        )

        adapter.add("아침")
        adapter.add("점심")
        adapter.add("저녁")
        adapter.add("간식")
        mealSpinner.adapter = adapter
    }
    fun addItemAmountSpinner(){
        val adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,ArrayList<String>())
        adapter.add("g")
        adapter.add("1회제공량")
        amountSpinner.adapter = adapter
    }
    fun setDate(){
        val instance = Calendar.getInstance()//오늘날짜설정하기
        day = instance.get(Calendar.DAY_OF_MONTH).toString()
        if(day.length == 1){
            day = "0" + day
            Log.d("database_day",day.toString())
        }
        month = (instance.get(Calendar.MONTH)+1).toString()
        if(month.length == 1){
            month = "0" + month
            Log.d("database_month",month.toString())

        }
        date_today = month + day
    }
    fun addFoodToTable(){
        foodAddButton.setOnClickListener {
            meal = mealSpinner.selectedItem.toString()//spinner에서 받아오기
            amount = amountSpinner.selectedItem.toString()
            val inputAmount = findViewById(R.id.inputAmount) as EditText
            input = inputAmount.text.toString()
            val foodname = searchfood.text.toString()

            val addingDate = HashMap<String,String>()
            val addingMeal = HashMap<String,String>()
            var diseases = ""
            if (mealSpinner.selectedItem.toString() != "" && amountSpinner.selectedItem.toString() != "" && inputAmount.text.toString() != "" ) {
                var database =
                    FirebaseDatabase.getInstance().getReference("/user/userinfo/" + user_id + "/foodtable/"+date_today+"/date")

                database.setValue(month + "월 "+ day + "일")
                var mealBase = FirebaseDatabase.getInstance().getReference("user/userinfo/"+user_id+"/foodtable/"+date_today+"/"+meal)

                mealBase.updateChildren(mapOf(Pair(foodname,input)))
                //mealBase.updateChildren(mapOf(Pair(foodname,input+amount)))

                Toast.makeText(applicationContext,"추가되었습니다",Toast.LENGTH_SHORT).show()
                //database.setValue(new_user)
            } else {
            }
        }

    }


}
