package com.example.capstone

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.databinding.SearchUtubeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.capstone.databinding.SearchpageBinding
import com.google.api.Distribution
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

class SearchActivity : AppCompatActivity(){
    //
    var catchUtubeText : String? = null
    var utubeText: String? = null
    var utubeData = ArrayList<SearchData>()
    val serverKey = "AIzaSyA3wjLVxkqdyd-dYtF-_2IRZGYrCd0jKN4"
    var selectedIngredient : String = ""
    //
    var db = FirebaseFirestore.getInstance()
    lateinit var catchLists: ArrayList<NameInfo> // <- SearchPageActivity에서 searchsList를 받아올 ArrayList + 타입 일치를 위해 lateinit사용 ? = null 사용하면 타입안맞아서 오류발생
    var DBLists: ArrayList<NameInfo>? = null // id, name, 재료, 이미지url 을 넣은 리스트
    private val binding by lazy { SearchpageBinding.inflate(layoutInflater) }
    lateinit var adapterCatchLists : ArrayList<NameInfo>


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        selectedIngredient = intent?.getStringExtra("select_ingredient")?: ""
        binding.SearchText.setText(selectedIngredient)

        binding.SearchButton.setOnClickListener(View.OnClickListener {
            var searchText = binding.SearchText.text.toString()
            utubeText = searchText

            if(searchText.equals("")) {
                Log.d(TAG,"null")
            }
            else{
                val mAdapter : RecyclerView.Adapter<*> = SearchAdapter(DBLists!!) //searchAdapter의 searchList에 데이터베이스의 모든 레시피를 보내 filter에서 검색을 돌리도록하는것
                (mAdapter as SearchAdapter).filter(searchText) //searchText는 searchAdapter의 filter의 searchText가되어 검색어가된다.
                adapterCatchLists = (mAdapter).returnRecipe()
                //

                if(utubeText!!.contains('#')){
                    var utubeSplitText: List<String>
                    var tempUtubeText: String? = null
                    utubeSplitText = utubeText!!.split('#')
                    Log.d(TAG,"utubeSplitText = ${utubeSplitText} 입니다")
                    tempUtubeText = utubeSplitText[0]
                    for(i in 1 until utubeSplitText.size){
                        tempUtubeText += utubeSplitText[i]
                    }
                    utubeText = tempUtubeText
                }
                Log.d(TAG,"utubeText = ${utubeText} 입니다.")
                search()
                //
                binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.SearchPageRecyclerView.setHasFixedSize(true)
                binding.SearchPageRecyclerView.adapter = SearchAdapter(adapterCatchLists)
            }
        })

        binding.RecipePageButton.setOnClickListener({
            if(utubeText == null){
                Log.d(TAG,"name은 null값입니다!")
            }
            else {
                binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.SearchPageRecyclerView.setHasFixedSize(true)
                binding.SearchPageRecyclerView.adapter = SearchAdapter(adapterCatchLists)
            }
        })

        binding.UtubePageButton.setOnClickListener({
            if(utubeText == null){
                Log.d(TAG,"name은 null값입니다!")
            }
            else {
                binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.SearchPageRecyclerView.setHasFixedSize(true)
                binding.SearchPageRecyclerView.adapter = UtubeAdapter(this, utubeData)
                Log.d(TAG, "${utubeData}")
            }
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
                            document.data["picture"].toString(),
                            document.data["tag"].toString()
                        )
                    )
                }
            }
    }

    private fun search (){
        CoroutineScope(Dispatchers.Main).launch{
            //검색 결과 불러오는 코드
            val result = async(Dispatchers.Default) { paringJsonData(utube) }.await()
            //

        }
    }

    //유튜브 url에 접근하여 검색한 결과들을 json 객체로 만들어준다
    @get:Throws(IOException::class)

    val utube: JSONObject
        get() {
            val originUrl = ("https://www.googleapis.com/youtube/v3/search?"
                    + "part=snippet&q=" +  utubeText
                    + "&key=" + serverKey + "&maxResults=5")
            val myUrl = String.format(originUrl)
            val url = URL(myUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.readTimeout = 10000
            connection.connectTimeout = 15000
            connection.connect()
            var line: String?
            var result = ""
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuffer()
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            println("검색결과$response")
            result = response.toString()
            var jsonObject = JSONObject()
            try {
                jsonObject = JSONObject(result)
            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return jsonObject
        }

    //json 객체를 가지고 와서 필요한 데이터를 파싱한다.
    //파싱을 하면 여러가지 값을 얻을 수 있는데 필요한 값들을 세팅하셔서 사용하시면 됩니다.
    @Throws(JSONException::class)
    private fun paringJsonData(jsonObject: JSONObject) {
        //재검색할때 데이터들이 쌓이는걸 방지하기 위해 리스트를 초기화 시켜준다.
        utubeData.clear()
        val contacts = jsonObject.getJSONArray("items")
        for (i in 0 until contacts.length()) {
            val c = contacts.getJSONObject(i)
            val kind = c.getJSONObject("id").getString("kind") // 종류를 체크하여 playlist도 저장
            vodid = if (kind == "youtube#video") {
                c.getJSONObject("id").getString("videoId") // 유튜브 아이디값만 재생페이지 넘길떄(view) string으로 전달
                // 동영상
                // 아이디
                // 값입니다.
                // 재생시
                // 필요합니다.
            } else {
                c.getJSONObject("id").getString("playlistId") // 유튜브
            }
            val title = c.getJSONObject("snippet").getString("title") //유튜브 제목을 받아옵니다
            val changString = stringToHtmlSign(title)
            val date = c.getJSONObject("snippet").getString("publishedAt") //등록날짜
                .substring(0, 10)
            val imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                .getJSONObject("default").getString("url") //썸네일 이미지 URL값

            //JSON으로 파싱한 정보들을 객체화 시켜서 리스트에 담아준다.
            utubeData.add(SearchData(vodid, changString, imgUrl, date))
        }
    }

    var vodid = ""

    //영상 제목을 받아올때 &quot; &#39; 문자가 그대로 출력되기 때문에 다른 문자로 대체 해주기 위해 사용하는 메서드
    private fun stringToHtmlSign(str: String): String {
        return str.replace("&amp;".toRegex(), "[&]")
            .replace("[<]".toRegex(), "&lt;")
            .replace("[>]".toRegex(), "&gt;")
            .replace("&quot;".toRegex(), "'")
            .replace("&#39;".toRegex(), "'")
    }

}


