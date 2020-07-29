package com.example.myapplication

import android.graphics.BitmapFactory
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage

class RecommendAdapter(val inflater: LayoutInflater, val array: ArrayList<RecommendTable>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = inflater.inflate(R.layout.recommend_page, null)
        var storage = FirebaseStorage.getInstance()

        var storageRef = storage.getReferenceFromUrl("gs://chageun.appspot.com" + "${array[position].url}")
        var ByteSize: Long = 1024 * 1024 * 5
        storageRef.getBytes(ByteSize).addOnSuccessListener {
            var bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.findViewById<ImageView>(R.id.Rimage)!!.setImageBitmap(bitmap)
        }
        view.findViewById<TextView>(R.id.Rtext)!!.text = "${array[position].name}\n"
        view.findViewById<TextView>(R.id.Rabout)!!.text = "\n${array[position].about}\n"

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