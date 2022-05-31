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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.capstone.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.tensorflow.lite.examples.detection.DetectorActivity
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
    var logdb : LogDatabase? = null
    var recipeLogList = ArrayList<NameInfo>()
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

        /*binding.startbtn.setOnClickListener {
            val intent = Intent(this, UtubeSearchActivity::class.java)
            //intent.putExtra("code", code)
            startActivity(intent)
        }*/

        // 이쪽코드가 ****검색창**** 이미지뷰 코드입니다. MainSearchText를 이미지뷰id값을 넣으시면 작동됩니다.
        binding.MainSearchText.setOnClickListener({
            var nextIntent = Intent(this, SearchActivity::class.java)
            startActivity(nextIntent)
        })


        val intent = Intent(this,CategoriActivity::class.java)




        binding.favorites.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        db.collection("recipe")
            .get()
            .addOnSuccessListener{result->
                DBLists = ArrayList()
                for(document in result){
                    DBLists!!.add(NameInfo(document.id, document.data["name"].toString(), document.data["ingredient"].toString(), document.data["picture"].toString(),document.data["tag"].toString()))
                }
                //랜덤추천 레시피
                binding.RandomViewPager.adapter = RandomRecipeAdapter(DBLists!!)
                binding.RandomViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                //카테고리 버튼
                binding.koreaFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("한식")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "한식 요리")
                    startActivity(intent)
                }
                binding.chinaFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("중식")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "중식 요리")
                    startActivity(intent)
                }
                binding.japanFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("일식")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "일식 요리")
                    startActivity(intent)
                }
                binding.americaFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("양식")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "양식 요리")
                    startActivity(intent)
                }
                binding.asianFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("동남아")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "아시안 요리")
                    startActivity(intent)
                }
                binding.mexicanFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("멕시칸")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "멕시칸 요리")
                    startActivity(intent)
                }
                binding.fusionFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("퓨전")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "퓨전 요리")
                    startActivity(intent)
                }
                binding.globalFood.setOnClickListener{
                    val mAdapter : RecyclerView.Adapter<*> = CategoriAdapter(DBLists!!)
                    (mAdapter as CategoriAdapter).kategori("이국적")
                    kategoriCatchLists = (mAdapter).returnKategori()
                    intent.putExtra("sendKategori",kategoriCatchLists)
                    intent.putExtra("CategoriName", "이국적인 요리")
                    startActivity(intent)
                }
            }

        binding.cameraSearch.setOnClickListener{
            val intent = Intent(this,DetectorActivity::class.java)
            startActivity(intent)
        }

        logdb = LogDatabase.getInstance(this)
        var savedLogs = logdb!!.RecipeLogDao().getAll()
        val logsId = ArrayList<String>()
        for (i in savedLogs.size-1 downTo 0){
            logsId.add(savedLogs[i].id)
        }
        if (logsId.isNotEmpty()){
            for (i in 0..logsId.size-1){
                db.collection("recipe")
                    .document(logsId[i])
                    .get()
                    .addOnSuccessListener { document ->
                        recipeLogList!!.add(
                            NameInfo(
                                logsId[i],
                                document["name"] as String,
                                document["ingredient"] as String,
                                document["picture"] as String,
                                document["tag"] as String
                            )
                        )
                        binding.rvRecipeLog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                        binding.rvRecipeLog.setHasFixedSize(true)
                        binding.rvRecipeLog.adapter = SearchAdapter(recipeLogList)

                    }
            }
        }



    }

    override fun onRestart() {
        super.onRestart()
        logdb = LogDatabase.getInstance(this)
        var savedLogs = logdb!!.RecipeLogDao().getAll()
        val logsId = ArrayList<String>()
        recipeLogList.removeAll(recipeLogList)
        for (i in savedLogs.size-1 downTo 0){
            logsId.add(savedLogs[i].id)
        }
        if (logsId.isNotEmpty()){
            for (i in 0..logsId.size-1){
                db.collection("recipe")
                    .document(logsId[i])
                    .get()
                    .addOnSuccessListener { document ->
                        recipeLogList!!.add(
                            NameInfo(
                                logsId[i],
                                document["name"] as String,
                                document["ingredient"] as String,
                                document["picture"] as String,
                                document["tag"] as String
                            )
                        )
                        SearchAdapter(recipeLogList).notifyDataSetChanged()
                        binding.rvRecipeLog.adapter = SearchAdapter(recipeLogList)
                    }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        val audioManager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 5,0)//음성인식때문에 음소거되어있던 알림소리 정상화
    }

}