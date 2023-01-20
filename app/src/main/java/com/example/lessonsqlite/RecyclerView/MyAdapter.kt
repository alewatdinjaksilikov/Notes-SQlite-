package com.example.lessonsqlite.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonsqlite.Activity.EditActivity
import com.example.lessonsqlite.R
import com.example.lessonsqlite.db.MyDbManager
import com.example.lessonsqlite.db.MyIntentConstants
import com.example.lessonsqlite.entity.ListItem
import kotlinx.android.synthetic.main.rc_item.view.*

class MyAdapter(listArray: ArrayList<ListItem>,contextM: Context) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    var contextM = contextM
    var list = listArray

    inner class MyViewHolder(itemView: View,contextVH: Context) : RecyclerView.ViewHolder(itemView) {
        var contextVH = contextVH

        fun setData(listItem:ListItem) {
            itemView.rc_tv_item.text = listItem.title
            itemView.rc_item_tv_time.text = listItem.time
            itemView.setOnClickListener {
                var intent = Intent(contextVH, EditActivity::class.java).apply {
                    putExtra(MyIntentConstants.I_TITLE_KEY,listItem.title)
                    putExtra(MyIntentConstants.I_CONTENT_KEY,listItem.content)
                    putExtra(MyIntentConstants.I_URI_KEY,listItem.uri)
                    putExtra(MyIntentConstants.I_ID_KEY,listItem.id)
                }
                contextVH.startActivity(intent)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.rc_item, parent, false)
        return MyViewHolder(inflate,contextM)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //обновление данных
    fun updateAdapter(listItems:List<ListItem>){
        list.clear()
        list.addAll(listItems)
        notifyDataSetChanged() //сообщаем к адаптеру что данные изменены
    }

    fun removeItem(position: Int,dbManager: MyDbManager){
        dbManager.removeItemFromDb(list[position].id.toString())
        list.removeAt(position)
        notifyItemRangeChanged(0,list.size)
        notifyItemRemoved(position)
    }
}