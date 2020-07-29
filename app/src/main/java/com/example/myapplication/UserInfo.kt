package com.example.myapplication

import java.io.Serializable

data class UserInfo(
    var id: String,
    var passwd: String,
    var name: String,
    var age: String,
    var kg: String,
    var height: String,
    var diseases: String,
    var sexuality: String
): Serializable  {
    constructor() : this("","","","","","","","")
}