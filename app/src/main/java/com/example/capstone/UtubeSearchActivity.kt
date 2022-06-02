package com.example.capstone

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.databinding.SearchUtubeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.*
import org.json.JSONException
import kotlin.Throws
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class UtubeSearchActivity : AppCompatActivity() {
    var search: EditText? = null
    var sdata = ArrayList<SearchData>()
    private val binding by lazy{ SearchUtubeBinding.inflate(layoutInflater)}
    val serverKey = "AIzaSyA3wjLVxkqdyd-dYtF-_2IRZGYrCd0jKN4"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        search = findViewById<View>(R.id.search) as EditText

        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = UtubeAdapter(this, sdata)
        Log.d(TAG,"sdata = ${sdata}")
    }

    val job = CoroutineScope(Dispatchers.Default).launch {

    }


    private fun search (){
            CoroutineScope(Dispatchers.Main).launch{
                //검색 결과 불러오는 코드
                val result = async(Dispatchers.Default) { paringJsonData(utube) }.await()
                //
                binding.recyclerview.layoutManager = LinearLayoutManager(this@UtubeSearchActivity, LinearLayoutManager.VERTICAL, false)
                binding.recyclerview.setHasFixedSize(true)
                binding.recyclerview.adapter = UtubeAdapter(this@UtubeSearchActivity, sdata)
            }
        }

    //유튜브 url에 접근하여 검색한 결과들을 json 객체로 만들어준다
    @get:Throws(IOException::class)
    val utube: JSONObject
        get() {
            val originUrl = ("https://www.googleapis.com/youtube/v3/search?"
                    + "part=snippet&q=" +  search!!.text.toString()
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
        sdata.clear()
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
            sdata.add(SearchData(vodid, changString, imgUrl, date))
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