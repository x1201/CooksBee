package com.example.capstone

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    //
    var utubeText: String? = null
    //
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var code : String ="ixpayhTvaQr46BKBbLFk"

    var db = FirebaseFirestore.getInstance()
    var DBLists: ArrayList<NameInfo>? = null // id, name, 재료를 넣은 리스트
    lateinit var adapterCatchLists : ArrayList<NameInfo> //searchAdapter에서 리스트 받아오는 리스트
    lateinit var kategoriCatchLists : ArrayList<NameInfo> //searchAdapter에서 리스트 받아오는 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }

        binding.startbtn.setOnClickListener {
            val intent = Intent(this, UtubeSearchActivity::class.java)
            //intent.putExtra("code", code)
            startActivity(intent)
        }

        // 이쪽코드가 ****검색창**** 이미지뷰 코드입니다. MainSearchText를 이미지뷰id값을 넣으시면 작동됩니다.
        binding.MainSearchText.setOnClickListener({
            var nextIntent = Intent(this, SearchActivity::class.java)
            startActivity(nextIntent)
        })

        /*
        binding.MainSearchButton.setOnClickListener(View.OnClickListener {
            var nextIntent = Intent(this, SearchActivity::class.java)
            var searchText = binding.MainSearchText.text.toString()
            utubeText = searchText

            if(searchText.equals("")) Log.d(TAG,"null")
            else{

                val mAdapter : RecyclerView.Adapter<*> = SearchAdapter(DBLists!!)//searchAdapter의 searchList에 데이터베이스의 모든 레시피를 보내 filter에서 검색을 돌리도록하는것
                (mAdapter as SearchAdapter).filter(searchText) //searchText는 searchAdapter의 filter의 searchText가되어 검색어가된다.
                adapterCatchLists = (mAdapter).returnRecipe()  //adapterCatchList로 searchAdapter에서 나온 결과리스트를 받아옴
                nextIntent.putExtra("sendList", adapterCatchLists)
                //
                nextIntent.putExtra("sendUtubeText", utubeText)
                //
                startActivity(nextIntent)
            }
        })
        */

        val intent = Intent(this,CategoriActivity::class.java)

        binding.koreaFood.setOnClickListener{
            val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
            (mAdapter as CategoriAdapter).kategori("한식")
            kategoriCatchLists = (mAdapter).returnKategori()
            intent.putExtra("sendKategori",kategoriCatchLists)
            startActivity(intent)
        }
        binding.chinaFood.setOnClickListener{
            val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
            (mAdapter as CategoriAdapter).kategori("중식")
            kategoriCatchLists = (mAdapter).returnKategori()
            intent.putExtra("sendKategori",kategoriCatchLists)
            startActivity(intent)
        }
        binding.japanFood.setOnClickListener{
            val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
            (mAdapter as CategoriAdapter).kategori("일식")
            kategoriCatchLists = (mAdapter).returnKategori()
            intent.putExtra("sendKategori",kategoriCatchLists)
            startActivity(intent)
        }
        binding.americaFood.setOnClickListener{
            val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
            (mAdapter as CategoriAdapter).kategori("양식")
            kategoriCatchLists = (mAdapter).returnKategori()
            intent.putExtra("sendKategori",kategoriCatchLists)
            startActivity(intent)
        }

        db.collection("recipe")
            .get()
            .addOnSuccessListener{result->
                DBLists = ArrayList()
                for(document in result){
                    DBLists!!.add(NameInfo(document.id, document.data["name"].toString(), document.data["ingredient"].toString(), document.data["picture"].toString(),document.data["tag"].toString()))
                }
            }
    }
    override fun onDestroy() {
        super.onDestroy()
        val audioManager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 5,0)//음성인식때문에 음소거되어있던 알림소리 정상화
    }

}