package com.example.myapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class AddAdapter(var items: ArrayList<FoodNutri>, var what: Int) : RecyclerView.Adapter<AddAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var f_name: TextView
        var f_supply: TextView
        var f_add: ImageView
        var background:LinearLayout
        var shopping:ImageView

        init {
            f_name = itemView.findViewById(R.id.f_name)
            f_supply = itemView.findViewById(R.id.f_supply)
            f_add = itemView.findViewById(R.id.f_add)
            shopping = itemView.findViewById(R.id.ashopping)
            background = itemView.findViewById(R.id.abackgrounddecide)

            f_add.setOnClickListener {
                val position = adapterPosition
                AddClickListener?.OnAddClick(this, it, items[position], position)
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }

            shopping.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.add_layout, parent, false)
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
            holder.shopping.setImageResource(0)
            holder.f_add.setImageResource(R.drawable.camera)
            holder.f_supply.text = items[position].f_kcal
        } else {
            holder.f_add.setImageResource(R.drawable.add)
            holder.shopping.setImageResource(R.drawable.shopping)
            when(what){
                0->{
                    holder.f_supply.text = items[position].f_kcal + " \n( 당류 함량 : "+items[position].f_carb +"g )"
                }
                1->{
                    holder.f_supply.text = items[position].f_kcal + "\n( 나트륨 함량 : "+items[position].f_Na +"mg )"
                }
                2->{
                    holder.f_supply.text = items[position].f_kcal + " \n( 나트륨 함량 : " +items[position].f_Na +"mg, " +
                            "당류 함량 : "+items[position].f_carb+"g )"
                }
                3->{
                    holder.f_supply.text = items[position].f_kcal
                }
            }
        }
    }

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View, data: FoodNutri, position: Int)
    }

    interface OnAddClickListener {
        fun OnAddClick(holder: ViewHolder, view: View, data: FoodNutri, position: Int)
    }

    interface OnShopClickListener{
        fun OnShopClick(holder: ViewHolder, view: View, data: FoodNutri, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null
    var AddClickListener: OnAddClickListener? = null
    var aShoppingListenr: OnShopClickListener? = null
}