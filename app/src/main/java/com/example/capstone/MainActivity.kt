package com.example.capstone

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.capstone.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var code : String ="ixpayhTvaQr46BKBbLFk"

    var db = FirebaseFirestore.getInstance()
    var firstNameList: ArrayList<NameInfo>? = null // id, name, 재료를 넣은 리스트
    lateinit var firstSearchList : ArrayList<NameInfo> // 검색결과를 담을 리스트 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.startbtn.setOnClickListener {
            val intent = Intent(this, UtubeSearchActivity::class.java)
           // intent.putExtra("code", code)
            startActivity(intent)
        }

        var nextIntent = Intent(this, SearchActivity::class.java)

        binding.MainSearchButton.setOnClickListener(View.OnClickListener {
            firstSearchList.clear()
            var searchText = binding.MainSearchText.text.toString()
            for(i in 0 until firstNameList?.size!!){
                if(searchText.equals(null)){
                    Log.d(ContentValues.TAG,"null")
                    break
                }
                else if(firstNameList!![i].name.contains(searchText)){ //이름으로 검색
                    firstSearchList?.add(NameInfo(firstNameList!![i].id,firstNameList!![i].name,firstNameList!![i].ingredient,firstNameList!![i].url))
                    //이 값을 intent를 사용해 SearchActivity로 전달해 onCreate할 때 띄워져서 보여지게 해야함
                    //intent에 데이터를 넣는법 = nextIntent.putExtra(String name, Serializable value)
                    nextIntent.putExtra("firstSearchList", firstSearchList)
                }
            }
            startActivity(nextIntent) //검색 버튼을 누르면 SearchActivity로 넘어감

        })
        db.collection("recipe")
            .get()
            .addOnSuccessListener{result->
                firstNameList = ArrayList()
                firstSearchList = ArrayList()
                for(document in result){
                    firstNameList!!.add(
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