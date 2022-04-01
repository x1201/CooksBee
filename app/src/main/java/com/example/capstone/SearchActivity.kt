package com.example.capstone

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.databinding.SearchpageBinding

class SearchActivity : AppCompatActivity(){
    var db = FirebaseFirestore.getInstance()
    //
    lateinit var firstGetList: ArrayList<NameInfo> // <- SearchPageActivity에서 searchsList를 받아올 ArrayList + 타입 일치를 위해 lateinit사용 ? = null 사용하면 타입안맞아서 오류발생
    //
    var searchRecipeList: ArrayList<NameInfo>? = null // id, name, 재료, 이미지url 을 넣은 리스트
    private val binding by lazy { SearchpageBinding.inflate(layoutInflater) }
    lateinit var secondSearchList : ArrayList<NameInfo> // 검색결과를 담을 리스트 선언
    //
    lateinit var beforeKeywordSearchList : ArrayList<NameInfo> // 임시 검색결과를 담을 리스트 선언
    lateinit var afterKeywordSearchList : ArrayList<NameInfo> // 임시 검색결과를 담을 리스트 선언
    //


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firstGetList = intent.getSerializableExtra("firstSearchList") as ArrayList<NameInfo> //<- getList에 getSerializableExtra로 searchsList의 리스트내용을 전부 받음
        //타입 일치위해 getSerializableExtra 뒤에 ad ArrayList<NameInfo>를 붙임
        binding.SearchPageRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.SearchPageRecyclerView.setHasFixedSize(true)
        binding.SearchPageRecyclerView.adapter = SearchAdapter(firstGetList)
        Log.d(TAG,"$firstGetList")


        binding.SearchButton.setOnClickListener(View.OnClickListener {
            secondSearchList.clear()
            beforeKeywordSearchList?.clear()
            var searchText = binding.SearchText.text.toString()
            //
            var searchKeywordList: List<String> //키워드 검색어가 들어가는 리스트
            //
            if (searchText.equals("")) { Log.d(TAG, "null") }
            // 재료는 #을 넣었을 때만 적용되도록
            else if(searchText.contains('#') == true){// 여기서 searchTextList에 들어간 재료들이 들어가는 레시피를 띄워줘야함
                searchKeywordList = searchText.split('#')// #재료로 검색했을 때 searchTextList에 재료배열이 들어가게 함

                var searchKeywordListSize = searchKeywordList.size -1 // 검색재료가 몇개인지 판단

                if(searchKeywordListSize == 1) { //검색재료가 1개일때
                    for (i in 0 until searchRecipeList?.size!!) {//레시피의 개수만큼 검색돌림
                        if (searchRecipeList!![i].ingredient.contains(searchKeywordList[1])) {//searchKeywordList에 한개밖에 없기때문에 1을 넣는다
                            secondSearchList?.add(NameInfo(searchRecipeList!![i].id, searchRecipeList!![i].name, searchRecipeList!![i].ingredient, searchRecipeList!![i].url))
                        }
                    }
                }
                else{ // 검색재료가 1개가 아닐때
                    beforeKeywordSearchList = secondSearchList
                    /*for (j in 1 .. searchKeywordListSize) {
                        for (i in 0 until beforeKeywordSearchList?.size!!) {//레시피의 개수만큼 검색돌림
                            if(beforeKeywordSearchList!![i].ingredient.contains(searchKeywordList[j])){
                                afterKeywordSearchList?.add(NameInfo(searchRecipeList!![i].id, searchRecipeList!![i].name, searchRecipeList!![i].ingredient, searchRecipeList!![i].url))
                            }
                        }
                        beforeKeywordSearchList = afterKeywordSearchList
                    }*/
                    Log.d(TAG,"$beforeKeywordSearchList")
                }
                binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.SearchPageRecyclerView.setHasFixedSize(true)
                binding.SearchPageRecyclerView.adapter = SearchAdapter(secondSearchList)
            }
            else{ // 레시피 제목을 기준으로 검색
                for(i in 0 until searchRecipeList?.size!!) {
                    Log.d(TAG, "false")
                    if (searchRecipeList!![i].name.contains(searchText)) { // 이름을 기준으로 검색을 함
                        secondSearchList?.add(
                            NameInfo(searchRecipeList!![i].id, searchRecipeList!![i].name, searchRecipeList!![i].ingredient, searchRecipeList!![i].url)
                        )
                        binding.SearchPageRecyclerView.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.SearchPageRecyclerView.setHasFixedSize(true)
                        binding.SearchPageRecyclerView.adapter = SearchAdapter(secondSearchList)
                        Log.d(TAG, "$secondSearchList")
                    }
                }
            }
        })

        db.collection("recipe")
            .get()
            .addOnSuccessListener{result->
                searchRecipeList = ArrayList()
                secondSearchList = ArrayList()
                beforeKeywordSearchList = ArrayList()
                afterKeywordSearchList = ArrayList()
                for(document in result){
                    searchRecipeList!!.add(
                        NameInfo(
                            document.id,
                            document.data["name"].toString(),
                            document.data["ingredient"].toString(),
                            document.data["picture"].toString()
                        )
                    )
                }
            }
    }
}


