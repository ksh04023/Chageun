package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_search_result_show.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import java.util.regex.MatchResult

class SearchResultShow : AppCompatActivity() {
    lateinit var templist: ArrayList<FoodNutri>
    lateinit var sorttemplist : ArrayList<FoodNutri>
    lateinit var sortaddtemplist : ArrayList<FoodNutri>
    lateinit var addtemplist: ArrayList<FoodNutri>
    lateinit var adddirectlist: ArrayList<String>
    lateinit var adapter: FoodAdapter
    lateinit var addadapter: AddAdapter
    var baseUrl = "https://www.fatsecret.kr"
    var htmlPageUrl = ""
    var query = ""
    var foodindex = 0
    var foodkeyword = ""
    val SELECT_IMAGE = 100
    var permission = 0
    val clientId = "_RNqLYPQ03AdNLNU2doe"
    val clientSecret = "OpSpt8QLdo"
    lateinit var will_eat_food : FoodNutri
    lateinit var disease :String
    lateinit var get:UserInfo
    private var todayfood : FoodTable? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        templist = ArrayList()
        sorttemplist = ArrayList()
        addtemplist = ArrayList()
        sortaddtemplist = ArrayList()
        adddirectlist = ArrayList()

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_search_result_show)
        get = intent?.getSerializableExtra("user") as UserInfo
        disease = get.diseases
        todayfood = intent?.getSerializableExtra("todayfood") as? FoodTable
        foodkeyword = intent.getStringExtra("userfood")
        foodindex = intent.getIntExtra("foodindex", 0)
        searchfood.setText(foodkeyword)
        init()
    }

    fun tempsearch(tempname : String):FoodNutri{
        lateinit var userfood : FoodNutri
        var i = 0
        while (i != foodarr.size) {
            if (foodarr[i].f_name.equals(tempname)) {
                userfood = foodarr[i]
                break
            }
            i++
        }
        return userfood
    }

    fun yesorno(): Int {
        lateinit var userfood :FoodNutri
        var yesorno = 0
        var kcal = 0.0 //칼로리
        var fat = 0.0 //지방
        var carb = 0.0 //탄수화물
        var sugar = 0.0//당류
        var nat = 0.0 //나트륨
        var supply = 0.0 // 먹은 양

        var nat_max = 6 / 2.5
        var kcal_max = 0.0 //허용 칼로리 최대치
        var sugar_max = 25 //허용 당류 최대치
        var carb_max = 0.0 //허용 탄수화물 최대치

        var fat_max = 44.4 //지방 최대치
        if (todayfood?.breakfast?.size != 0 && todayfood?.breakfast?.keys != null) {
            for(i in todayfood?.breakfast!!.keys){
                userfood = tempsearch(i)
                supply = todayfood?.breakfast!!.get(i)!!.toDouble()
                supply = userfood.f_supply.toDouble() / supply
                kcal += supply * userfood.f_kcal.toDouble()
                fat += supply * userfood.f_fat.toDouble()
                carb += supply * userfood.f_carb.toDouble()
                sugar += supply * userfood.f_sugar.toDouble()
                nat += supply * userfood.f_Na.toDouble()
            }
        }

        if (todayfood?.launch?.size != 0 && todayfood?.launch?.keys != null ) {
            for(i in todayfood?.launch!!.keys){
                userfood = tempsearch(i)
                supply = todayfood?.launch!!.get(i)!!.toDouble()
                supply = userfood.f_supply.toDouble() / supply
                kcal += supply * userfood.f_kcal.toDouble()
                fat += supply * userfood.f_fat.toDouble()
                carb += supply * userfood.f_carb.toDouble()
                sugar += supply * userfood.f_sugar.toDouble()
                nat += supply * userfood.f_Na.toDouble()
            }
        }
        if (todayfood?.dinner?.size != 0 && todayfood?.dinner?.keys != null) {
            for(i in todayfood?.dinner!!.keys){
                userfood = tempsearch(i)
                supply = todayfood?.dinner!!.get(i)!!.toDouble()
                supply = userfood.f_supply.toDouble() / supply
                kcal += supply * userfood.f_kcal.toDouble()
                fat += supply * userfood.f_fat.toDouble()
                carb += supply * userfood.f_carb.toDouble()
                sugar += supply * userfood.f_sugar.toDouble()
                nat += supply * userfood.f_Na.toDouble()
            }
        }
        if (todayfood?.snack?.size != 0 && todayfood?.snack?.keys != null) {
            for(i in todayfood?.snack!!.keys){
                userfood = tempsearch(i)
                supply = todayfood?.snack!!.get(i)!!.toDouble()
                supply = userfood.f_supply.toDouble() / supply
                kcal += supply * userfood.f_kcal.toDouble()
                fat += supply * userfood.f_fat.toDouble()
                carb += supply * userfood.f_carb.toDouble()
                sugar += supply * userfood.f_sugar.toDouble()
                nat += supply * userfood.f_Na.toDouble()
            }
        }

        if (get.sexuality == "여") {
            kcal_max = (get.height.toDouble()) * (get.height.toDouble()) * 0.01 * 0.01 * 21

        } else if (get.sexuality == "남") {
            kcal_max = (get.height.toDouble()) * (get.height.toDouble()) * 0.01 * 0.01 * 30
        }

        carb_max = kcal_max * 0.6

        if (get.diseases == "당뇨" || get.diseases == "당뇨,고혈압"|| get.diseases == "당뇨고혈압") {
            if (kcal + will_eat_food.f_kcal.toDouble() <= kcal_max) {
                yesorno++
            }
            if (sugar + will_eat_food.f_sugar.toDouble() <= sugar_max) {
                yesorno++
            }
            if (carb * 4 <= carb_max) {
                yesorno++
            }
        }
        if (get.diseases == "고혈압" || get.diseases == ",고혈압"|| get.diseases == "당뇨,고혈압"|| get.diseases == "당뇨고혈압") {
            if (kcal + will_eat_food.f_kcal.toDouble() <= kcal_max) {
                yesorno++
            }
            if ((nat + will_eat_food.f_Na.toDouble()) / 1000 < nat_max) {
                yesorno++
            }
            if ((fat + will_eat_food.f_fat.toDouble()) < fat_max) {
                yesorno++
            }
        }
        if (get.diseases == "없음"){
            if (kcal + will_eat_food.f_kcal.toDouble() <= kcal_max) {
                yesorno = 3
            } else {
                yesorno = 2
            }
        }

        return yesorno
    }

    fun init() {
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        adapter = FoodAdapter(templist, 3)
        when(disease){
            "당뇨" -> adapter = FoodAdapter(templist, 0)
            ",고혈압" -> adapter = FoodAdapter(templist, 1)
            "당뇨고혈압" -> adapter = FoodAdapter(templist, 2)
            "당뇨,고혈압" -> adapter = FoodAdapter(templist, 2)
        }
        search(foodkeyword)
        foodlist.layoutManager = layoutManager
        foodlist!!.adapter = adapter

        searchbutton.setOnClickListener {
            templist.clear()
            if (searchfood.text.length == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("차건차건")
                builder
                    .setIcon(R.drawable.cklogo)
                    .setMessage("음식을 입력하세요.")
                    .setPositiveButton("확인") { _, _ -> }
                builder.show()
            } else {
                templist.clear()
                var text = searchfood.text.toString()
                search(text)
                foodlist!!.adapter = adapter
            }
        }

        adapter.shopClickListener = object : FoodAdapter.OnShopClickListener{
            override fun OnShopClick(holder: FoodAdapter.ViewHolder, view: View, data: FoodNutri, position: Int) {
                val builder = AlertDialog.Builder(this@SearchResultShow)
                val inflater = this@SearchResultShow.layoutInflater
                val layout = R.layout.yesornodialog
                val dialogview = inflater.inflate(layout, null)
                will_eat_food = data
                builder.setTitle("차건차건")
                if(get.diseases == "당뇨고혈압" || get.diseases == "당뇨,고혈압"){
                    if (yesorno() == 5 || yesorno() == 6) {
                        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
                        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                    } else if (yesorno() == 4 || yesorno() == 3) {
                        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.yello_light)
                        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                    } else if (yesorno() == 1 || yesorno() == 2 || yesorno() == 0) {
                        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.red_light)
                        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                    }
                } else {
                    if (yesorno() == 3) {
                        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
                        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                    } else if (yesorno() == 2) {
                        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.yello_light)
                        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                    } else if (yesorno() == 1) {
                        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.red_light)
                        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                    }
                }
                builder
                    .setView(dialogview)
                    .setIcon(R.drawable.cklogo)
                    .setPositiveButton("확인") { _, _ ->
                        var apiUrl = "https://openapi.naver.com/v1/search/shop.json?query=${data.f_name}"
                        val request = object : StringRequest(Request.Method.GET, apiUrl,
                            Response.Listener { response ->
                                Log.d("apiresult", "서버 Response 수신: $response")
                            }, Response.ErrorListener { error ->
                                Log.d("apiresult", "서버 Response 가져오기 실패: $error")
                            }) {
                            override fun getHeaders(): Map<String, String> {
                                val params = HashMap<String, String>()
                                params["X-Naver-Client-Id"] = clientId
                                params["X-Naver-Client-Secret"] = clientSecret
                                return params
                            }
                        }
                        val webPage =
                            Uri.parse("https://msearch.shopping.naver.com/search/all.nhn?query=${data.f_name}")
                        startActivity(Intent(Intent.ACTION_VIEW, webPage))
                    }
                    .setNegativeButton("취소"){_,_ ->
                    }
                builder.show()
            }
        }

        adapter.itemClickListener = object : FoodAdapter.OnItemClickListener {
            override fun OnItemClick(holder: FoodAdapter.ViewHolder, view: View, data: FoodNutri, position: Int) {
                if (position != 0) {
                    val builder = AlertDialog.Builder(this@SearchResultShow)
                    builder.setTitle("차건차건")
                    builder
                        .setIcon(R.drawable.cklogo)
                        .setMessage(data.f_name +"의 영양 정보입니다.\n\n" +
                                "                          탄수화물 : "+data.f_carb+"g\n" +
                                "                          당류 : "+data.f_sugar+"g\n" +
                                "                          단백질 : "+data.f_protein+"g\n" +
                                "                          지방 : "+data.f_fat+"g\n" +
                                "                          포화지방 : "+data.f_stfat+"g\n" +
                                "                          트랜스지방 : "+data.f_transfat+"g\n" +
                                "                          콜레스테롤 : "+data.f_Coll+"g\n" +
                                "                          나트륨 : "+data.f_Na+"g\n")
                        .setPositiveButton("확인"){_,_-> }
                    builder.show()

                }
            }
        }

        adapter.imageClickListener = object : FoodAdapter.OnImageClickListener {
            override fun OnImageClick(holder: FoodAdapter.ViewHolder, view: View, data: FoodNutri, position: Int) {
                if (position != 0) {
                    val builder = AlertDialog.Builder(this@SearchResultShow)
                    val inflater = this@SearchResultShow.layoutInflater
                    val layout = R.layout.yesornodialog
                    val dialogview = inflater.inflate(layout, null)
                    builder.setTitle("차건차건")
                    will_eat_food = data
                    if(get.diseases == "당뇨고혈압" || get.diseases == "당뇨,고혈압"){
                        if (yesorno() == 5 || yesorno() == 6) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 4 || yesorno() == 3) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.yello_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 1 || yesorno() == 2 || yesorno() == 0) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.red_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        }
                    } else {
                        if (yesorno() == 3) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 2) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.yello_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 1) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.red_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        }
                    }
                    builder
                        .setView(dialogview)
                        .setIcon(R.drawable.cklogo)
                        .setPositiveButton("확인") { _, _ ->
                            val intent = Intent(this@SearchResultShow, FoodTableActivity::class.java)
                            intent.putExtra("userId",get?.id)
                            intent.putExtra("food_info", data)
                            intent.putExtra("todayfood",todayfood)
                            startActivity(intent)
                        }
                        .setNegativeButton("취소"){_,_ ->
                        }
                    builder.show()
                } else {
                    val builder = AlertDialog.Builder(this@SearchResultShow)
                    val inflater = this@SearchResultShow.layoutInflater
                    val layout = R.layout.cdialog
                    val dialogview = inflater.inflate(layout, null)
                    builder
                        .setTitle("차건차건")
                        .setView(dialogview)
                        .setIcon(R.drawable.cklogo)
                        .setMessage("새로운 식품을 추가하시려면 하단의 입력창에 음식 이름을 입력하세요.")
                        .setPositiveButton("확인") { _, _ ->
                            val tempname = dialogview.findViewById<EditText>(R.id.foodname)?.text.toString()
                            if (tempname.length == 0) {
                                val builder = AlertDialog.Builder(this@SearchResultShow)
                                builder.setTitle("차건차건")
                                builder
                                    .setIcon(R.drawable.cklogo)
                                    .setMessage("음식을 입력하세요.")
                                    .setPositiveButton("확인") { _, _ -> }
                                builder.show()
                            } else {
                                query = tempname
                                htmlPageUrl = "$baseUrl/칼로리-영양소/search?q=$query"
                                val jsoupAsyncTask = JsoupAsyncTask()
                                jsoupAsyncTask.execute()
                            }
                        }
                    builder.show()
                }
            }
        }
    }

    fun traffic(Food : FoodNutri):Int{ // 알고리즘이 들어갈 곳
        // 신호등 식품 yes or no 판별
        var usertraffic = 0
        val builder = AlertDialog.Builder(this@SearchResultShow)
        val inflater = this@SearchResultShow.layoutInflater
        val layout = R.layout.yesornodialog
        val dialogview = inflater.inflate(layout, null)
        builder.setTitle("차건차건")
        dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
        dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
        builder
            .setView(dialogview)
            .setIcon(R.drawable.cklogo)
            .setPositiveButton("확인") { _, _ ->
                usertraffic = 1}
            .setNegativeButton("취소"){_,_ ->
                usertraffic = 2
            }
        builder.show()
        return usertraffic
    }

    fun carbselector(p: FoodNutri): Double{
        try{
            if(p.f_carb == null || p.f_carb == "NULL")
                p.f_carb = String.format("%.0f",0.0)
            else
                return p.f_carb.toDouble()
        }catch (e:NumberFormatException){
            p.f_carb = String.format("%.0f",0.0)
        }
        catch (e:Exception){
            p.f_carb = String.format("%.0f",0.0)
        }
        return p.f_carb.toDouble()
    }

    fun Naselector(p: FoodNutri): Double{
        try{
            if(p.f_Na == null || p.f_Na == "NULL")
                p.f_Na = String.format("%.0f",0.0)
            else
                return p.f_Na.toDouble()
        } catch (e:NumberFormatException){
            p.f_Na = String.format("%.0f",0.0)
        }catch (e:Exception){
            p.f_Na = String.format("%.0f",0.0)
        }
        return p.f_Na.toDouble()
    }

    fun search(temptext: String) {
        templist.clear()
        var i = 0
        while (i != foodarr.size) {
            if (foodarr[i].f_name.toLowerCase().contains(temptext))
                templist.add(foodarr[i])
            i++
        }
        if (templist.size == 0) {
            val builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val layout = R.layout.cdialog
            val dialogview = inflater.inflate(layout, null)
            dialogview.findViewById<EditText>(R.id.foodname)!!.setText(temptext)
            builder
                .setTitle("차건차건")
                .setView(dialogview)
                .setIcon(R.drawable.cklogo)
                .setMessage("검색 결과가 없습니다.\n새로운 식품을 추가하시려면 하단의 입력창에 음식 이름을 입력하세요.")
                .setPositiveButton("확인") { _, _ ->
                    val tempname = dialogview.findViewById<EditText>(R.id.foodname)?.text.toString()
                    if (tempname == null) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("차건차건")
                        builder
                            .setIcon(R.drawable.cklogo)
                            .setMessage("음식을 입력하세요.")
                            .setPositiveButton("확인") { _, _ -> }
                        builder.show()
                    } else {
                        query = tempname
                        htmlPageUrl = "$baseUrl/칼로리-영양소/search?q=$query"
                        val jsoupAsyncTask = JsoupAsyncTask()
                        jsoupAsyncTask.execute()
                    }
                }
                .setNegativeButton("취소") { _, _ -> }
            builder.show()
        }
        else {
            when (disease) {
                "당뇨" -> templist.sortBy({ carbselector(it)})
                ",고혈압" -> templist.sortBy ({Naselector(it)})
                "당뇨고혈압" -> templist.sortBy ({ Naselector(it) + carbselector(it)})
                "당뇨,고혈압" -> templist.sortBy ({ Naselector(it) + carbselector(it)})
            }
            templist.add(
                0, FoodNutri(
                    " ", " ", " ", " ", " ", " ", "웹에서 검색하기", "원하시는 정보가 없다면 이곳을 클릭하세요.",
                    " ", " ", " ", " ", " "
                )
            )
            adapter.notifyDataSetChanged()
        }
    }
    fun sugarRate() {
        lateinit var recentDate: DataSnapshot
        lateinit var validData: DataSnapshot

        val database = FirebaseDatabase.getInstance().getReference("user/userinfo/${get.id}/bloodSugar")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (date in dataSnapshot.children) {
                    recentDate = date
                }
                for (meal in recentDate.children) {
                    if (meal.childrenCount.toInt() == 2) {
                        validData = meal
                    }
                }
                lateinit var sugarRate: ArrayList<Double>
                lateinit var mealNutri: FoodNutri

                val foods = FirebaseDatabase.getInstance().
                    getReference("user/userinfo/${get.id}/foodtable/${recentDate.key}/${validData.key}")
                foods.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var foodname = ""
                        for(food in dataSnapshot.children) {
                            foodname = food.key!!
                        }
                        mealNutri = tempsearch(foodname)
                        var before = 0.0
                        var after = 0.0
                        for(data in validData.children) {
                            when(data.key) {
                                "식전" -> before = data.value.toString().toDouble()
                                "식후" -> after = data.value.toString().toDouble()
                            }
                        }
                        sugarRate = arrayListOf(mealNutri.f_carb.toDouble(),after-before)
                        val change = FirebaseDatabase.getInstance().getReference("user/userinfo/${get.id}/change")
                        change.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var val1 = 0.0
                                var val2 = 0.0
                                for( i in dataSnapshot.children) {
                                    when(i.key) {
                                        "1" -> val1 = i.value.toString().toDouble()
                                        "2" -> val2 = i.value.toString().toDouble()
                                    }
                                }
                                Log.d("change","이전 데이터:$val1/$val2|받은 데이터:${sugarRate[0]}/${sugarRate[1]}")
                                change.child("1").setValue(val1+sugarRate[0])
                                change.child("2").setValue(val2+sugarRate[1])
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    private inner class JsoupAsyncTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                var pattern = """([0-9.]+)\s*[a-zA-Z]+""".toRegex()
                lateinit var matchResult: MatchResult
                val parseList = listOf("탄수화물", "단백질", "지방", "나트륨")

                addtemplist.clear()
                Log.d("url", htmlPageUrl)

                var doc = Jsoup.connect(htmlPageUrl).get()
                var titles = doc.select("td.borderbottom")
                println(titles)
                for (e in titles) {
                    var name = e.select("a.prominent").text()
                    lateinit var calorie: String
                    var fat = 0.0
                    var protein = 0.0
                    var sugar = 0.0
                    var salt = 0.0

                    var temp = e.select("div.smallText").text()
                    calorie = temp.slice(0 until temp.indexOf("|")).trim()

                    val detailDoc = Jsoup.connect(baseUrl + e.select("a.prominent").attr("href")).get()
                    val nutpanel = detailDoc.select("div.nutpanel").select("tr")
                    for (tr in nutpanel) {
                        val ingredient = tr.select("td.label").text().trim()
                        if (ingredient in parseList) {
                            matchResult = pattern.find(tr.select("td.borderTop[align=right]").text())!!
                            val result = matchResult.groups[1]?.value?.toDouble()!!
                            when (ingredient) {
                                parseList[0] -> sugar = result
                                parseList[1] -> protein = result
                                parseList[2] -> fat = result
                                parseList[3] -> salt = result
                            }
                        }
                    }
                    addtemplist.add( FoodNutri(" ", salt.toString(), protein.toString(), " ", (foodindex + 1).toString(), " ", name,
                                     calorie, fat.toString(), " ", sugar.toString(), " ", " "))
                }
                when (disease) {
                    "당뇨" -> addtemplist.sortBy ({ carbselector(it)})
                    ",고혈압" -> addtemplist.sortBy ({ Naselector(it) })
                    "당뇨고혈압" -> addtemplist.sortBy ({ Naselector(it) + carbselector(it) })
                    "당뇨,고혈압"-> addtemplist.sortBy ({ Naselector(it) + carbselector(it) })
                }
                addtemplist.add( 0, FoodNutri(" "," "," ", " ", " "," ", "직접 추가하기", "원하시는 정보가 없다면 이곳을 클릭하세요.",
                    " ", " ", " ", " ",  " "))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        fun initpermission() {
            if (permission == 0) {
                if (checkAppPermission(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    val intent = Intent(this@SearchResultShow, GalleryAdd::class.java)
                    startActivity(intent)
                } else {
                    askPermission(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), SELECT_IMAGE)
                }
            } else if (permission == 1) {
                if (checkAppPermission(arrayOf(android.Manifest.permission.CAMERA))) {
                    val intent = Intent(this@SearchResultShow, FoodAddCamera::class.java)
                    startActivity(intent)
                } else {
                    askPermission(arrayOf(android.Manifest.permission.CAMERA), SELECT_IMAGE)
                }
            }
        }

        fun checkAppPermission(requestPermission: Array<String>): Boolean {
            val requestResult = BooleanArray(requestPermission.size)
            for (i in requestResult.indices) {
                requestResult[i] = ContextCompat.checkSelfPermission(
                    this@SearchResultShow, requestPermission[i]
                ) == PackageManager.PERMISSION_GRANTED
                if (!requestResult[i]) {
                    return false
                }
            }
            return true
        }

        fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int) {
            ActivityCompat.requestPermissions(this@SearchResultShow, requestPermission, REQ_PERMISSION)
            initpermission()
        }

        override fun onPostExecute(result: Void?) {
            val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            addadapter = AddAdapter(addtemplist, 3)
            when (disease) {
                "당뇨" -> addadapter = AddAdapter(addtemplist, 0)
                ",고혈압" -> addadapter = AddAdapter(addtemplist, 1)
                "당뇨고혈압" -> addadapter = AddAdapter(addtemplist, 2)
                "당뇨,고혈압" -> addadapter = AddAdapter(addtemplist, 2)
            }

            addadapter.aShoppingListenr = object : AddAdapter.OnShopClickListener{
                override fun OnShopClick(holder: AddAdapter.ViewHolder, view: View, data: FoodNutri, position: Int) {
                    val builder = AlertDialog.Builder(this@SearchResultShow)
                    val inflater = this@SearchResultShow.layoutInflater
                    val layout = R.layout.yesornodialog
                    val dialogview = inflater.inflate(layout, null)
                    builder.setTitle("차건차건")
                    will_eat_food = data
                    if(get.diseases == "당뇨고혈압" || get.diseases == "당뇨,고혈압"){
                        if (yesorno() == 5 || yesorno() == 6) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 4 || yesorno() == 3) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.yello_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 1 || yesorno() == 2 || yesorno() == 0) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.red_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        }
                    } else {
                        if (yesorno() == 3) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.green_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 2) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.yello_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        } else if (yesorno() == 1) {
                            dialogview.findViewById<ImageView>(R.id.traffic).setImageResource(R.drawable.red_light)
                            dialogview.findViewById<TextView>(R.id.trafficstmt).setText("\n대충 살자...")
                        }
                    }
                    builder
                        .setView(dialogview)
                        .setIcon(R.drawable.cklogo)
                        .setPositiveButton("확인") { _, _ ->
                            var apiUrl = "https://openapi.naver.com/v1/search/shop.json?query=${data.f_name}"
                            val request = object : StringRequest(Request.Method.GET, apiUrl,
                                Response.Listener { response ->
                                    Log.d("apiresult", "서버 Response 수신: $response")
                                }, Response.ErrorListener { error ->
                                    Log.d("apiresult", "서버 Response 가져오기 실패: $error")
                                }) {
                                override fun getHeaders(): Map<String, String> {
                                    val params = HashMap<String, String>()
                                    params["X-Naver-Client-Id"] = clientId
                                    params["X-Naver-Client-Secret"] = clientSecret
                                    return params
                                }
                            }
                            val webPage =
                                Uri.parse("https://msearch.shopping.naver.com/search/all.nhn?query=${data.f_name}")
                            startActivity(Intent(Intent.ACTION_VIEW, webPage))
                        }
                        .setNegativeButton("취소"){_,_ ->
                        }
                    builder.show()
                }
            }

            addadapter.AddClickListener = object : AddAdapter.OnAddClickListener {
                override fun OnAddClick(holder: AddAdapter.ViewHolder, view: View, data: FoodNutri, position: Int) {
                    adddirectlist.clear()
                    if (position == 0) {
                        adddirectlist.add(" ")
                        adddirectlist.add(" ")
                        adddirectlist.add(" ")
                        adddirectlist.add(" ")
                        adddirectlist.add(" ")
                        adddirectlist.add(" ")
                        adddirectlist.add(data.f_num)

                        val ListItems = ArrayList<String>()
                        ListItems.add("직접 입력하기")
                        ListItems.add("갤러리로부터 업로드")
                        ListItems.add("카메라로 찍어서 업로드")
                        val items = ListItems.toTypedArray()

                        val builder = AlertDialog.Builder(this@SearchResultShow)
                        builder.setTitle("차건차건")
                        builder.setIcon(R.drawable.cklogo)
                        builder.setItems(items, DialogInterface.OnClickListener { _, pos ->
                            val selectedText = items[pos]
                            if (selectedText == "갤러리로부터 업로드") {
                                permission = 0
                                initpermission()
                            } else if (selectedText == "카메라로 찍어서 업로드") {
                                permission = 1
                                initpermission()
                            } else if (selectedText == "직접 입력하기") {
                                val directintent = Intent(applicationContext, FoodAddDirect::class.java)
                                directintent.putStringArrayListExtra("fooddetail", adddirectlist)
                                startActivity(directintent)
                            }
                        })
                        builder.setNegativeButton(
                            "취소"
                        ) { _, _ -> }
                        builder.show()
                    } else {
                        adddirectlist.add(data.f_name)
                        adddirectlist.add(data.f_kcal)
                        adddirectlist.add(data.f_carb)
                        adddirectlist.add(data.f_protein)
                        adddirectlist.add(data.f_fat)
                        adddirectlist.add(data.f_Na)
                        adddirectlist.add(data.f_num)
                        val directintent = Intent(applicationContext, FoodAddDirect::class.java)
                        directintent.putStringArrayListExtra("fooddetail", adddirectlist)
                        startActivity(directintent)
                    }
                }
            }

            addadapter.itemClickListener = object : AddAdapter.OnItemClickListener {
                override fun OnItemClick(
                    holder: AddAdapter.ViewHolder,
                    view: View,
                    data: FoodNutri,
                    position: Int
                ) {
                    if (position != 0) {
                        val builder = AlertDialog.Builder(this@SearchResultShow)
                        builder.setTitle("차건차건")
                        builder
                            .setIcon(R.drawable.cklogo)
                            .setMessage(
                                data.f_name + "의 영양 정보입니다.\n\n" +
                                        "                          탄수화물 : " + data.f_carb + "g\n" +
                                        "                          단백질 : " + data.f_protein + "g\n" +
                                        "                          지방 : " + data.f_fat + "g\n" +
                                        "                          나트륨 : " + data.f_Na + "g\n"
                            )
                            .setPositiveButton("확인") { _, _ -> }
                        builder.show()
                    }
                }
            }
            foodlist.layoutManager = layoutManager
            foodlist.adapter = addadapter
        }
    }
}