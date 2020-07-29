package com.example.myapplication

data class alarm(val name:String, val days:String,val time:String) {
    constructor() : this("","","")
}