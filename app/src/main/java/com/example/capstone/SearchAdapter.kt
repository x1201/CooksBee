package com.example.capstone

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchAdapter(val searchList: ArrayList<NameInfo>) : RecyclerView.Adapter<SearchAdapter.CustomViewHoder>() {

    inner class CustomViewHoder(ItemView : View) : RecyclerView.ViewHolder(ItemView) {
        val searchPhoto = itemView.findViewById<ImageView>(R.id.searchRecipeImage)
        val searchName = itemView.findViewById<TextView>(R.id.searchRecipeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : SearchAdapter.CustomViewHoder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent,false)
        return CustomViewHoder(view)
    }
    override fun getItemCount(): Int {
        return searchList.size
    }
    override fun onBindViewHolder(holder: CustomViewHoder, position: Int){
        Glide.with(holder.itemView.context).load(searchList.get(position).url).into(holder.searchPhoto)
        holder.searchName.text = searchList.get(position).name
        val clickListId = searchList.get(position).id

        // 리사이클러뷰 아이템 클릭 부분
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView?.context, RecipePage::class.java) //<-intent에 보내고 싶은 데이터를 넣고 startActivity 에 넣는 과정
            intent.putExtra("clickListId",clickListId)//아이디만 보내주면 됨됨
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
        //
    }
}