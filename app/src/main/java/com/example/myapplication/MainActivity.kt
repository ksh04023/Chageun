package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

var foodarr:ArrayList<FoodNutri> = ArrayList()

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val foodTableArray = ArrayList<FoodTable>()
    lateinit var searchresult:ArrayList<FoodNutri>
    lateinit var user: UserInfo
    var foodindex = 0
    var todayindex = -1

    var random_arr = ArrayList<String>()
    var random_arr2 = ArrayList<String>()

    var a_recommendUrl = ArrayList<String>()
    var a_recommendName = ArrayList<String>()
    var a_recommendAbout = ArrayList<String>()

    var d_recommendUrl = ArrayList<String>()
    var d_recommendName = ArrayList<String>()
    var d_recommendAbout = ArrayList<String>()

    var recommendTableArray = ArrayList<RecommendTable>()
    var kind_diseases = 2

    lateinit var name: String
    lateinit var id:String
    var pressure: Boolean=true
    var sugar:Boolean = false

    //var mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        foodarr = ArrayList()

        /* if (mAuth .currentUser != null) {
            mAuth.currentUser!!.reload()
        } else {
            mAuth.signInAnonymously().addOnCompleteListener {
                if(!it.isSuccessful){
                    Log.w("FirebaseAuth", "signInAnonymously", it.getException())
                } } } */

        searchresult= ArrayList()
        user = intent.getSerializableExtra("user") as UserInfo

        nav_view.getHeaderView(0).findViewById<TextView>(R.id.userName).setText(user?.name)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.userID).setText(user?.id)


        foodindex = intent.getIntExtra("foodindex",0)
        if(user.diseases == "없음" || user.diseases == "당뇨고혈압" || user.diseases == "당뇨,고혈압"){
            kind_diseases = 2 //당뇨병, 고혈압 둘다있는 경우, 없음
        } else if (user.diseases == "당뇨") {
            kind_diseases = 1 //당뇨병
        } else {
            kind_diseases = 0 //고혈압
        }
        /////////////////////////////
        if (user.diseases == "없음") {
            nav_view.menu.removeItem(R.id.nav_input_dialog)
        }

        setSupportActionBar(toolbar)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        initRecommendDatabase()
        initUserDatabase() //식단 읽어옴
        initDatabase()



        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_camera)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        foodsearch.setOnClickListener {
            if(userfood.text.length == 0){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("차건차건")
                builder
                    .setIcon(R.drawable.cklogo)
                    .setMessage("음식을 입력하세요.")
                    .setPositiveButton("확인"){_,_-> }
                builder.show()
            }else{
                var intent = Intent(this, SearchResultShow::class.java)
                if(todayindex != -1)
                    intent.putExtra("todayfood",foodTableArray[todayindex])
                intent.putExtra("user",user)
                intent.putExtra("userfood", userfood!!.text.toString())
                intent.putExtra("foodindex",foodindex)
                startActivityForResult(intent, 0)
            }
        }
    }

    fun initDatabase():Boolean{
        FirebaseApp.initializeApp(this)

        val database = FirebaseDatabase.getInstance()
        val databaseRef = database.getReference("/food/foodinfo")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (fileSnapshot in dataSnapshot.children) {
                    foodindex++
                    var f_num = fileSnapshot.child("번호").getValue().toString()
                    var f_supply= fileSnapshot.child("1회제공량").getValue().toString()
                    var f_carb = fileSnapshot.child("탄수화물").getValue().toString()
                    var f_protein = fileSnapshot.child("단백질").getValue().toString()
                    var f_stfat = fileSnapshot.child("포화지방산").getValue().toString()
                    var f_fat = fileSnapshot.child("지방").getValue().toString()
                    var f_Na = fileSnapshot.child("나트륨").getValue().toString()
                    var f_sugar = fileSnapshot.child("당류").getValue().toString()
                    var f_name = fileSnapshot.child("식품이름").getValue().toString()
                    var f_kcal = fileSnapshot.child("열량").getValue().toString()
                    var f_Coll = fileSnapshot.child("콜레스테롤").getValue().toString()
                    var f_transfat = fileSnapshot.child("트랜스지방산").getValue().toString()
                    var f_part = fileSnapshot.child("식품군").getValue().toString()

                    foodarr.add(FoodNutri(f_supply!!,f_Na!!,f_protein!!, f_sugar!!,  f_num!!,  f_part!!,  f_name!!,  f_kcal!!,
                        f_fat!!, f_Coll!!, f_carb!!, f_transfat!!,  f_stfat!!))

                    Log.d("^^",f_name)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException())
            }
        })

        return true
    }

    fun initRecommendDatabase() {
        var count = 0
        while (count < 5) {
            var random = (1..15).random()
            if (random_arr.contains(random.toString())) {
                continue
            } else {
                random_arr.add((random).toString())
                count++
            }
        }
        for (i in 0..random_arr.size - 1) {
            a_recommendUrl.add("/afood/a" + random_arr[i] + ".PNG")
        }
        count = 0
        while (count < 5) {
            var random = (1..15).random()
            if (random_arr2.contains(random.toString())) {
                continue
            } else {
                random_arr2.add((random).toString())
                count++
            }
        }
        for (i in 0..random_arr2.size - 1) {
            d_recommendUrl.add("/dfood/d" + random_arr2[i] + ".PNG")
        }


        when (kind_diseases) {
            0 -> { //고혈압
                var db = FirebaseDatabase.getInstance().getReference("/arecommend") //고혈압
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            var name = postSnapshot.child("name").value.toString()
                            var about = postSnapshot.child("about").value.toString()
                            a_recommendName.add(name)
                            a_recommendAbout.add(about)
                        }
                        initRecommendTable()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
            1 -> { //당뇨
                var db = FirebaseDatabase.getInstance().getReference("/drecommend") //고혈압
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            var name = postSnapshot.child("name").value.toString()
                            var about = postSnapshot.child("about").value.toString()
                            d_recommendName.add(name)
                            d_recommendAbout.add(about)
                        }
                        initRecommendTable()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
            2 -> { //둘다, 없음
                var db = FirebaseDatabase.getInstance().getReference("/arecommend") //고혈압
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            var name = postSnapshot.child("name").value.toString()
                            var about = postSnapshot.child("about").value.toString()
                            a_recommendName.add(name)
                            a_recommendAbout.add(about)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                db = FirebaseDatabase.getInstance().getReference("/drecommend") //고혈압
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            var name = postSnapshot.child("name").value.toString()
                            var about = postSnapshot.child("about").value.toString()
                            d_recommendName.add(name)
                            d_recommendAbout.add(about)
                        }
                        initRecommendTable()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
        }

    }


    fun initRecommendTable() {
        when (kind_diseases) {
            0 -> { //고혈압
                for (i in 0..4) {
                    recommendTableArray.add(
                        RecommendTable(
                            a_recommendUrl[i],
                            a_recommendName[random_arr[i].toInt() - 1],
                            a_recommendAbout[random_arr[i].toInt() - 1]
                        )
                    )
                }
            }
            1 -> { //당뇨
                for (i in 0..4) {
                    recommendTableArray.add(
                        RecommendTable(
                            d_recommendUrl[i],
                            d_recommendName[random_arr2[i].toInt() - 1],
                            d_recommendAbout[random_arr2[i].toInt() - 1]
                        )
                    )
                }
            }
            2 -> {
                for (i in 0..2) {
                    recommendTableArray.add(
                        RecommendTable(
                            a_recommendUrl[i],
                            a_recommendName[random_arr[i].toInt() - 1],
                            a_recommendAbout[random_arr[i].toInt() - 1]
                        )
                    )
                }
                for (i in 0..2) {
                    recommendTableArray.add(
                        RecommendTable(
                            d_recommendUrl[i],
                            d_recommendName[random_arr2[i].toInt() - 1],
                            d_recommendAbout[random_arr2[i].toInt() - 1]
                        )
                    )
                }
            }
        }
        recommendTableArray.shuffle()

        val recommedadapter = RecommendAdapter(getLayoutInflater(), recommendTableArray)
        pager2.adapter = recommedadapter
        pager2.setCurrentItem(recommedadapter.count - 1, true)

    }

    fun initUserDatabase(){
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val databaseFoodTable = database.getReference("/user/userinfo/${user.id}/foodtable")
        databaseFoodTable.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    todayindex++
                    val date = postSnapshot.child("date").value.toString()
                    val breakfast = HashMap<String,String>()
                    val launch = HashMap<String,String>()
                    val dinner = HashMap<String,String>()
                    val snack = HashMap<String,String>()
                    for (child in postSnapshot.child("아침").children) {
                        breakfast[child.key!!] = child.value.toString()
                    }
                    for (child in postSnapshot.child("점심").children) {
                        launch[child.key!!] = child.value.toString()
                    }
                    for (child in postSnapshot.child("저녁").children) {
                        dinner[child.key!!] = child.value.toString()
                    }
                    for (child in postSnapshot.child("간식").children) {
                        snack[child.key!!] = child.value.toString()
                    }
                    Log.d("^^",date)
                    foodTableArray.add(FoodTable(date,breakfast,launch,dinner,snack))
                }
                initFoodTable()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_my_page -> {
                if(kind_diseases == 1) {//당뇨병
                    val intent = Intent(this, MyPageActivity::class.java)
                    intent.putExtra("user", user)
                    startActivityForResult(intent, 0)
                }else if(kind_diseases == 0){//고혈압
                    val intent = Intent(this, MyPressurePageActivity::class.java)
                    intent.putExtra("user", user)
                    startActivityForResult(intent, 0)
                }else{
                    val intent = Intent(this, MyBothPageActivity::class.java)
                    intent.putExtra("user", user)
                    startActivityForResult(intent, 0)
                }
            }
            R.id.nav_alarm -> {
                val intent = Intent(this, ShowAlarmActivity::class.java)
                intent.putExtra("user_id",user?.id)
                startActivityForResult(intent, 0)
            }
            R.id.nav_input_dialog -> {
                val intent = Intent(this, ShowInputDialogActivity::class.java)
                intent.putExtra("user", user)
                startActivityForResult(intent, 0)
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun initFoodTable() {
        /*foodTableArray.add(FoodTable("5월 26일","아침1","점심1","저녁1","간식1"))
        foodTableArray.add(FoodTable("5월 27일","아침1","점심1","저녁1","간식1"))
        foodTableArray.add(FoodTable("5월 28일","아침1","점심1","저녁1","간식1"))
        foodTableArray.add(FoodTable("5월 29일","아침1","점심1","저녁1","간식1"))
        foodTableArray.add(FoodTable("5월 30일","아침1","점심1","저녁1","간식1"))*/
        val adapter = FoodTableAdapter(getLayoutInflater(),foodTableArray)
        pager.adapter = adapter
        pager.setCurrentItem(adapter.count-1, true)
    }

    fun btnOnClick(v: View) {
        val position = pager.currentItem
        val position2 = pager2.currentItem
        when(v.id) {
            R.id.leftBtn -> pager.setCurrentItem(position-1,true)
            R.id.rightBtn -> pager.setCurrentItem(position+1,true)
            R.id.leftBtn2 -> pager2.setCurrentItem(position2 - 1, true)
            R.id.rightBtn2 -> pager2.setCurrentItem(position2 + 1, true)
        }
    }
}

