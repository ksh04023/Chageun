package com.example.myapplication

import java.io.Serializable

data class FoodNutri(var f_supply:String, var f_Na:String, var f_protein:String, var f_sugar:String, var f_num:String, var f_part:String, var f_name:String, var f_kcal:String,
                     var f_fat:String, var f_Coll:String, var f_carb:String, var f_transfat:String, var f_stfat:String):Serializable{
    constructor() : this("","","","","","","","","","","","","")
}

