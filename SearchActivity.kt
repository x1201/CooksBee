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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.databinding.SearchpageBinding

class SearchActivity : AppCompatActivity(){
    var db = FirebaseFirestore.getInstance()
    lateinit var catchLists: ArrayList<NameInfo> // <- SearchPageActivity에서 searchsList를 받아올 ArrayList + 타입 일치를 위해 lateinit사용 ? = null 사용하면 타입안맞아서 오류발생
    var DBLists: ArrayList<NameInfo>? = null // id, name, 재료, 이미지url 을 넣은 리스트
    private val binding by lazy { SearchpageBinding.inflate(layoutInflater) }
    lateinit var adapterCatchLists : ArrayList<NameInfo>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        catchLists = intent.getSerializableExtra("sendList") as ArrayList<NameInfo> //<- getList에 getSerializableExtra로 searchsList의 리스트내용을 전부 받음
        //타입 일치위해 getSerializableExtra 뒤에 ad ArrayList<NameInfo>를 붙임
        binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.SearchPageRecyclerView.setHasFixedSize(true)
        binding.SearchPageRecyclerView.adapter = SearchAdapter(catchLists)

        binding.SearchButton.setOnClickListener(View.OnClickListener {
            var searchText = binding.SearchText.text.toString()
            if(searchText.equals("")) {
                Log.d(TAG,"null")
                adapterCatchLists.clear()
            }
            else{
                val mAdapter : RecyclerView.Adapter<*> = SearchAdapter(DBLists!!) //searchAdapter의 searchList에 데이터베이스의 모든 레시피를 보내 filter에서 검색을 돌리도록하는것
                (mAdapter as SearchAdapter).filter(searchText) //searchText는 searchAdapter의 filter의 searchText가되어 검색어가된다.
                adapterCatchLists = (mAdapter).returnRecipe()
            }
            binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.SearchPageRecyclerView.setHasFixedSize(true)
            binding.SearchPageRecyclerView.adapter = SearchAdapter(adapterCatchLists)
        })

        db.collection("recipe")
            .get()
            .addOnSuccessListener{result->
                DBLists = ArrayList()
                for(document in result){
                    DBLists!!.add(
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


