package com.example.myapplication

import java.io.Serializable


data class FoodTable(val date:String, val breakfast:HashMap<String,String>, val launch:HashMap<String,String>,
                     val dinner:HashMap<String,String>, val snack:HashMap<String,String>):Serializable{

}