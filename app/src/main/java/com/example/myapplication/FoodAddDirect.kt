package com.example.myapplication

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_food_add_direct.*

class FoodAddDirect : AppCompatActivity() {
    lateinit var fooddetail : ArrayList<String?>
    var foodindex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_add_direct)
        fooddetail =  ArrayList()
        fooddetail = intent.getStringArrayListExtra("fooddetail")
        init()
    }

    fun writefooddb(){
        var database = FirebaseDatabase.getInstance().getReference("/food/foodinfo").child((fooddetail[6]).toString())
        database.child("번호").setValue(foodindex)
        database.child("1회제공량").setValue(e_fsupply.text.toString())
        database.child("탄수화물").setValue(e_fcarb.text.toString())
        database.child("단백질").setValue(e_fprotein.text.toString())
        database.child("포화지방산").setValue(e_fstfat.text.toString())
        database.child("지방").setValue(e_ffat.text.toString())
        database.child("나트륨").setValue(e_fNa.text.toString())
        database.child("당류").setValue(e_fsugar.text.toString())
        database.child("식품이름").setValue(e_fname.text.toString())
        database.child("열량").setValue(e_fkcal.text.toString())
        database.child("콜레스테롤").setValue(e_fColl.text.toString())
        database.child("트랜스지방산").setValue(e_ftransfat.text.toString())
    }

    fun checkInput():Boolean{
        val errorColor: Int

        errorColor = ContextCompat.getColor(applicationContext, R.color.errorColor)
        val errorString = "숫자로 입력하세요"
        val foregroundColorSpan = ForegroundColorSpan(errorColor)
        val spannableStringBuilder = SpannableStringBuilder(errorString)
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length, 0)
        val regex = Regex("((0|[1-9][0-9]*)(\\.[0-9]*)?)")
        if (regex.find(e_fsupply.text.toString())?.value.toString() != e_fsupply.text.toString())
            e_fsupply.error = spannableStringBuilder
        else if (regex.find(e_fkcal.text.toString())?.value.toString() != e_fkcal.text.toString())
            e_fkcal.error = spannableStringBuilder
        else if( regex.find(e_fcarb.text.toString())?.value.toString() != e_fcarb.text.toString())
            e_fcarb.error = spannableStringBuilder
        else if (e_fsugar.text.length!=0 && regex.find(e_fsugar.text.toString())?.value.toString()!= e_fsugar.text.toString())
            e_fsugar.error = spannableStringBuilder
        else if (regex.find(e_fprotein.text.toString())?.value.toString() != e_fprotein.text.toString())
            e_fprotein.error = spannableStringBuilder
        else if (regex.find(e_ffat.text.toString())?.value.toString()!= e_ffat.text.toString())
            e_ffat.error = spannableStringBuilder
        else if (e_fstfat.text.length!=0 && regex.find(e_fstfat.text.toString())?.value.toString() != e_fstfat.text.toString())
            e_fstfat.error = spannableStringBuilder
        else if (e_ftransfat.text.length!=0 && regex.find(e_ftransfat.text.toString())?.value.toString() != e_ftransfat.text.toString())
            e_ftransfat.error = spannableStringBuilder
        else if (e_fColl.text.length!=0 && regex.find(e_fColl.text.toString())?.value.toString() != e_fColl.text.toString())
            e_fColl.error = spannableStringBuilder
        else if (e_fNa.text.length!=0 && regex.find(e_fNa.text.toString())?.value.toString()!= e_fNa.text.toString())
            e_fNa.error = spannableStringBuilder
        else
            return true
        return false
    }

    fun checkEmpty():Boolean{
        val errorColor: Int
        errorColor = ContextCompat.getColor(applicationContext, R.color.errorColor)
        val errorString = "값을 입력하세요"
        val foregroundColorSpan = ForegroundColorSpan(errorColor)
        val spannableStringBuilder = SpannableStringBuilder(errorString)
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length, 0)
        if (e_fname.text.length == 0)
            e_fname.error = spannableStringBuilder
        else if (e_fsupply.text.length == 0)
            e_fsupply.error = spannableStringBuilder
        else if (e_fkcal.text.length == 0)
            e_fkcal.error = spannableStringBuilder
        else if( e_fcarb.text.length == 0 )
            e_fcarb.error = spannableStringBuilder
        else if (e_fprotein.text.length == 0)
            e_fprotein.error = spannableStringBuilder
        else if (e_ffat.text.length == 0)
            e_ffat.error = spannableStringBuilder
        else if (e_fNa.text.length == 0)
            e_fNa.error = spannableStringBuilder
        else
            return true
        return false
    }

    fun init(){
        e_fname.setText(fooddetail[0])
        val regex1 = Regex("([0-9]+)(ml|g| g| ml)")
        val regex2 = Regex("([0-9]+)(kcal| kcal)")
        if (fooddetail[1] == null) {
            e_fsupply.setText("")
            e_fkcal.setText("")
        }
        else {
            val result1 = regex1.find(fooddetail[1]!!.trim())
            val result2 = regex2.find(fooddetail[1]!!.trim())
            if (result1 == null) {
                e_fsupply.setText("")
            }
            else {
                e_fsupply.setText(result1.groups[1]?.value.toString())
            }
            if (result2 == null) {
                e_fkcal.setText("")
            }
            else {
                e_fkcal.setText(result2.groups[1]?.value.toString())
            }
        }
        e_fcarb.setText(fooddetail[2])
        e_fprotein.setText(fooddetail[3])
        e_ffat.setText(fooddetail[4])
        e_fNa.setText(fooddetail[5])
        e_fsugar.setText(fooddetail[6])
        e_fstfat.setText(fooddetail[7])
        e_ftransfat.setText(fooddetail[8])
        e_fColl.setText(fooddetail[9])

        addbutton.setOnClickListener {
            if(checkEmpty()){
                if(checkInput()){
                    writefooddb()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("차건차건")
                    builder
                        .setIcon(R.drawable.cklogo)
                        .setMessage("음식 추가가 완료되었습니다.")
                        .setPositiveButton("확인") { _, _ ->
                            finish()
                        }
                    builder.show()
                }
            }
        }
    }
}
