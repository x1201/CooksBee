package com.example.capstone

import android.content.ContentValues
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

class SearchAdapter(val searchList: ArrayList<NameInfo>) : RecyclerView.Adapter<SearchAdapter.CustomViewHoder>() {

    lateinit var findLists : ArrayList<NameInfo>
    lateinit var copyLists : ArrayList<NameInfo>
    lateinit var beforeKeywordSearchList : ArrayList<NameInfo> // 임시 검색결과를 담을 리스트 선언
    lateinit var afterKeywordSearchList : ArrayList<NameInfo> // 임시 검색결과를 담을 리스트 선언

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
    }
    fun filter(searchText: String) {
        val SearchText = searchText
        var searchKeywordList: List<String>
        findLists = ArrayList()
        beforeKeywordSearchList = ArrayList()
        afterKeywordSearchList = ArrayList()

        Log.d(ContentValues.TAG,"searchText = $SearchText")
        Log.d(ContentValues.TAG,"searchList $searchList")

        if(SearchText.contains('#') == true){
            searchKeywordList = SearchText.split('#')

            if(searchKeywordList.size - 1 == 1){
                for(i in 0 until searchList.size){
                    if(searchList[i].ingredient.contains(searchKeywordList[1])){
                        findLists.add(NameInfo(searchList[i].id,searchList[i].name,searchList[i].ingredient,searchList[i].url))
                    }else Log.d(ContentValues.TAG,"searchList[1] notFound")
                }
            }
            else{
                copyLists = searchList
                beforeKeywordSearchList.addAll(copyLists)

                for(j in 1 .. searchKeywordList.size - 1){
                    for(i in 0 until beforeKeywordSearchList.size){
                        if(beforeKeywordSearchList[i].ingredient.contains(searchKeywordList[j].replace(" ",""))){
                            afterKeywordSearchList.add(NameInfo(beforeKeywordSearchList[i].id,beforeKeywordSearchList[i].name,beforeKeywordSearchList[i].ingredient,beforeKeywordSearchList[i].url))
                        }
                        else Log.d(ContentValues.TAG,"searchKeywordList'S notFound")
                    }
                    beforeKeywordSearchList.clear() //beforeKeywordSearchList초기화
                    beforeKeywordSearchList.addAll(afterKeywordSearchList)
                    afterKeywordSearchList.clear()
                }
                findLists.addAll(beforeKeywordSearchList)
            }
        }
        else{
            for(i in 0 until searchList.size){
                if(searchList[i].name.contains(SearchText)){
                    findLists.add(NameInfo(searchList[i].id,searchList[i].name,searchList[i].ingredient,searchList[i].url))
                }
            }
        }
    }

    fun returnRecipe(): ArrayList<NameInfo>{
        return findLists
    }
}