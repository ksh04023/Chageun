package com.example.myapplication

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class FoodTableAdapter(val inflater: LayoutInflater, val array: ArrayList<FoodTable>): PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = inflater.inflate(R.layout.viewpager_childview, null)
        val date = view.findViewById<TextView>(R.id.date)
        val breakfast = view.findViewById<TextView>(R.id.breakfast)
        val launch = view.findViewById<TextView>(R.id.launch)
        val dinner = view.findViewById<TextView>(R.id.dinner)
        val snack = view.findViewById<TextView>(R.id.snack)
        breakfast.text = "아침: "
        launch.text = "점심: "
        dinner.text = "저녁: "
        snack.text = "간식: "
        date.text = String.format("%s자 식단\n",array[position].date)
        var it = array[position].breakfast.keys.iterator()
        while(it.hasNext()) {
            breakfast.text = String.format("%s%s",breakfast.text.toString(),it.next())
            breakfast.text = String.format("%s%s",breakfast.text.toString(),if(it.hasNext()) ", " else "")
        }
        it = array[position].launch.keys.iterator()
        while(it.hasNext()) {
            launch.text = String.format("%s%s",launch.text.toString(),it.next())
            launch.text = String.format("%s%s",launch.text.toString(),if(it.hasNext()) ", " else "")
        }
        it = array[position].dinner.keys.iterator()
        while(it.hasNext()) {
            dinner.text = String.format("%s%s",dinner.text.toString(),it.next())
            dinner.text = String.format("%s%s",dinner.text.toString(),if(it.hasNext()) ", " else "")
        }
        it = array[position].snack.keys.iterator()
        while(it.hasNext()) {
            snack.text = String.format("%s%s",snack.text.toString(),it.next())
            snack.text = String.format("%s%s",snack.text.toString(),if(it.hasNext()) ", " else "")
        }
        container.addView(view)
        return view
        //return super.instantiateItem(container, position)
    }

    override fun getCount(): Int {
        return array.size
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        container.removeView(item as View)
    }
}