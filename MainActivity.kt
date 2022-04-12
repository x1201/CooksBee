package com.example.capstone
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var code : String ="ixpayhTvaQr46BKBbLFk"

    var db = FirebaseFirestore.getInstance()
    var DBLists: ArrayList<NameInfo>? = null // id, name, 재료를 넣은 리스트
    lateinit var adapterCatchLists : ArrayList<NameInfo> //searchAdapter에서 리스트 받아오는 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.startbtn.setOnClickListener {
            val intent = Intent(this, UtubeSearchActivity::class.java)
            //intent.putExtra("code", code)
            startActivity(intent)
        }
        var nextIntent = Intent(this, SearchActivity::class.java)


        binding.MainSearchButton.setOnClickListener(View.OnClickListener {
            var searchText = binding.MainSearchText.text.toString()
            //searchAdapter에게 searchText를 보내는 과정
            if(searchText.equals("")) Log.d(TAG,"null")
            else{
                val mAdapter : RecyclerView.Adapter<*> = SearchAdapter(DBLists!!)//searchAdapter의 searchList에 데이터베이스의 모든 레시피를 보내 filter에서 검색을 돌리도록하는것
                (mAdapter as SearchAdapter).filter(searchText) //searchText는 searchAdapter의 filter의 searchText가되어 검색어가된다.
                adapterCatchLists = (mAdapter).returnRecipe() //검색해서 나온 레시피를 받아오는 코드
                nextIntent.putExtra("sendList", adapterCatchLists)
                startActivity(nextIntent)
            }
        })
        db.collection("recipe")
            .get()
            .addOnSuccessListener{result->
                DBLists = ArrayList()
                for(document in result){
                    DBLists!!.add(NameInfo(document.id, document.data["name"].toString(), document.data["ingredient"].toString(), document.data["picture"].toString()))
                }
            }
    }
}
