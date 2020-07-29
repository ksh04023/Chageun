package com.example.myapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class FoodAdapter(var items: ArrayList<FoodNutri>, var what: Int) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var f_name: TextView
        var f_supply: TextView
        var fooddb: ImageView
        var shopping:ImageView
        var background:LinearLayout
        init {
            f_name = itemView.findViewById(R.id.f_name)
            f_supply = itemView.findViewById(R.id.f_supply)
            fooddb = itemView.findViewById(R.id.fooddb)
            shopping = itemView.findViewById(R.id.shopping)
            background = itemView.findViewById(R.id.backgrounddecide)

            fooddb.setOnClickListener {
                val position = adapterPosition
                imageClickListener?.OnImageClick(this, it, items[position], position)
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }

            shopping.setOnClickListener {
                val position = adapterPosition
                shopClickListener?.OnShopClick(this,it,items[position],position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.f_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.f_name.text = items[position].f_name
        holder.setIsRecyclable(false)
        if (position == 0) {
            holder.background.background = null
            holder.f_supply.text = items[position].f_supply + items[position].f_kcal
            holder.fooddb.setImageResource(R.drawable.modify)
            holder.shopping.setImageResource(0)
        } else {
            when(what){
                0->{
                    holder.f_supply.text = items[position].f_supply + "g당 " + items[position].f_kcal + "kcal \n( 당류 함량 : "+items[position].f_carb +"g )"
                }
                1->{
                    holder.f_supply.text = items[position].f_supply + "g당 " + items[position].f_kcal + "kcal \n( 나트륨 함량 : "+items[position].f_Na +"mg )"
                }
                2->{
                    holder.f_supply.text = items[position].f_supply + "g당 " + items[position].f_kcal + "kcal \n( 나트륨 함량 : " +items[position].f_Na +"mg, " +
                            "당류 함량 : "+items[position].f_carb +"g )"
                }
                3->{
                    holder.f_supply.text = items[position].f_supply + "g당 " + items[position].f_kcal + "kcal"
                }
            }
            holder.fooddb.setImageResource(R.drawable.cutlery)
            holder.shopping.setImageResource(R.drawable.shopping)
        }
    }

    interface OnShopClickListener {
        fun OnShopClick(holder: ViewHolder, view: View, data: FoodNutri, position: Int)
    }

    interface OnImageClickListener {
        fun OnImageClick(holder: ViewHolder, view: View, data: FoodNutri, position: Int)
    }

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View, data: FoodNutri, position: Int)
    }

    var shopClickListener: OnShopClickListener?= null
    var itemClickListener: OnItemClickListener? = null
    var imageClickListener: OnImageClickListener? = null
}