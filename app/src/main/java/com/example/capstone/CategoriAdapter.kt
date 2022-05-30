package com.example.capstone

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoriAdapter(val searchList: ArrayList<NameInfo>) : RecyclerView.Adapter<CategoriAdapter.CustomViewHolder>() {

    lateinit var findLists: ArrayList<NameInfo>

    class CustomViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val searchPhoto = itemView.findViewById<ImageView>(R.id.titleImage)
        val searchName = itemView.findViewById<TextView>(R.id.titleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_utube, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(searchList.get(position).url)
            .into(holder.searchPhoto)
        holder.searchName.text = searchList.get(position).name
        val clickListId = searchList.get(position).id
        // 리사이클러뷰 아이템 클릭 부분
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, RecipePage::class.java) //<-intent에 보내고 싶은 데이터를 넣고 startActivity 에 넣는 과정
            intent.putExtra("clickListId",clickListId) //아이디만 보내주면 됨됨
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    fun kategori(searchText: String) {
        val SearchText = searchText
        findLists = ArrayList()
        if (SearchText.equals("한식")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("한식")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "한식이 없어요!")
            }
        }
        if (SearchText.equals("중식")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("중식")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "중식이 없어요!")
            }
        }
        if (SearchText.equals("일식")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("일식")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "일식이 없어요!")
            }
        }
        if (SearchText.equals("양식")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("양식")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "양식이 없어요!")
            }
        }
        if (SearchText.equals("동남아")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("동남아")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "동남아요리가 없어요!")
            }
        }
        if (SearchText.equals("멕시칸")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("멕시칸")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "멕시칸요리가 없어요!")
            }
        }
        if (SearchText.equals("퓨전")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("퓨전")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "퓨전요리가 없어요!")
            }
        }
        if (SearchText.equals("이국적")) {
            for (i in 0 until searchList.size) {
                if (searchList[i].tag.contains("이국적")) {
                    findLists.add(
                        NameInfo(searchList[i].id, searchList[i].name, searchList[i].ingredient, searchList[i].url, searchList[i].tag)
                    )
                } else Log.d(TAG, "이국적인요리가 없어요!")
            }
        }

    }


    fun returnKategori():ArrayList<NameInfo> {
        return findLists
    }
}