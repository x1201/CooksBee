package com.example.capstone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.databinding.RecipePageBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class RecipePage : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener
    private val binding by lazy { RecipePageBinding.inflate(layoutInflater) }
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }//음성인식 무한 실행을 위한 핸들러 변수
    private var speechText: String = "null"//받은 음성
    private var tts: TextToSpeech? = null//TTS변수
    private var selectedRecipe: Int = 0// 선택된 레시피 번호
    private var recipeTextList = emptyArray<String>()//레시피 리스트
    private var recipeURLList = emptyArray<String>()//레시피 사진 리스트
    private var ingrList = emptyArray<String>()//재료 리스트
    private var ingrVolList = emptyArray<String>()//재료 수량 리스트
    private var allIngrList = emptyArray<String>()
    var recipeText: String = ""
    var db = FirebaseFirestore.getInstance()
    var name : String = ""
    var ingredient : String = ""
    var picture : String = ""
    var recipePicture : String = ""
    var code : String = ""
    var appdb : AppDatabase? = null
    var logdb : LogDatabase? = null
    val recipesList = arrayListOf<Recipes>()
    val recipeAdapter = RecipeAdapter(recipesList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        code = intent?.getStringExtra("clickListId")?: ""

        var contact = Contacts(0, code)

        db.collection("recipe")
            .document(code)
            .get()
            .addOnSuccessListener { document ->

                name = document["name"] as String
                ingredient = document["ingredient"] as String
                recipeText= document["content"] as String
                picture = document["picture"] as String
                recipePicture = document["recipePicture"] as String
                recipeTextList = recipeText.split("@").toTypedArray()
                recipeURLList = recipePicture.split("@").toTypedArray()
                ingrList = ingredient.split("@").toTypedArray()
                for (i in ingrList.indices){
                    allIngrList = ingrList[i].split("#").toTypedArray()
                    ingrList[i] = allIngrList[0]
                    ingrVolList += allIngrList[1]
                }
                val sb = StringBuffer()
                for (i in ingrList.indices){
                    sb.append(ingrList[i]+ " ")
                    if (i == ingrList.size-1){
                        sb.append(ingrVolList[i])
                    }
                    else {
                        sb.append(ingrVolList[i] + ",")
                    }
                }

                binding.name.text = name
                binding.ingredient.text = sb
                Glide.with(this).load(picture).into(binding.titlePhoto)
                for (i in recipeTextList.indices){
                    if(recipeURLList[i] == "empty")//없을때 안읽어주는거 고쳐 TTS읽을때 공백인식해서 다음으로 넘기게 만들어야함
                        recipeURLList[i] = ""
                    if(recipeTextList[i] == "empty")
                        recipeTextList[i] = ""
                    recipesList.add(Recipes(recipeURLList[i], recipeTextList[i]))
                }
                muteNoti()
                setListener()
                makeTTS()
                show()

                binding.rvRecipe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvRecipe.setHasFixedSize(true)
                binding.rvRecipe.adapter = recipeAdapter
            }

        recipeAdapter.setItemClickListener(object : RecipeAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                selectedRecipe = position
                Toast.makeText(binding.root.context, "${position+1}번 선택됨", Toast.LENGTH_SHORT).show()
            }
        })

        logdb = LogDatabase.getInstance(this)
        var log = RecipeLog(0, code)

        var saveLogs = logdb!!.RecipeLogDao().getAll()
        val logid = arrayListOf<String>()
        for (i in 0..saveLogs.size-1){
            logid.add(saveLogs[i].id)
            if (saveLogs[i].id == code) {
                logdb?.RecipeLogDao()?.delete(saveLogs[i])
            }
        }
        logdb?.RecipeLogDao()?.insertAll(log)
        saveLogs = logdb!!.RecipeLogDao().getAll()
        if (saveLogs.size > 5){
            logdb?.RecipeLogDao()?.delete(saveLogs[0])
        }


        appdb = AppDatabase.getInstance(this)
        var savedContacts = appdb!!.contactsDao().getAll()
        Log.d("saveContacts", "Contacts" + savedContacts)

        val contactsId = arrayListOf<String>()
        for (i in 0..savedContacts.size-1){
            contactsId.add(savedContacts[i].id)
        }

        if (contactsId.contains(code)){
            binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
        }else{
            binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
        }

        binding.favoriteButton.setOnClickListener{
            val contactsId = arrayListOf<String>()
            for (i in 0..savedContacts.size-1){
                contactsId.add(savedContacts[i].id)
            }
            Log.d("APPDB start", "start "+savedContacts)
            if (savedContacts.isNotEmpty()){
                if (contactsId.contains(code)){
                    binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off)
                    for (i in 0..savedContacts.size-1){
                        if (savedContacts[i].id == code){
                            appdb?.contactsDao()?.delete(savedContacts[i])
                        }
                    }
                    savedContacts = appdb!!.contactsDao().getAll()
                    Log.d("APPDB contain", "delete "+savedContacts)
                }else{
                    binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
                    appdb?.contactsDao()?.insertAll(contact)
                    savedContacts = appdb!!.contactsDao().getAll()
                    Log.d("APPDB isNotContain", "add "+savedContacts)
                }
            }else{
                binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
                appdb?.contactsDao()?.insertAll(contact)
                savedContacts = appdb!!.contactsDao().getAll()
                Log.d("APPDB ContactsEmpty", "add "+savedContacts)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
            tts = null
        }

    }
    private fun muteNoti(){//알림음 뮤트
        val audioManager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val muteValue =  AudioManager.ADJUST_MUTE
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, muteValue, 0)
    }

    private fun startSTT(){//STT시작
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener)
            startListening(intent)
        }
    }

    private fun setListener(){//recognitionListener 생성
        recognitionListener = object: RecognitionListener {

            override fun onReadyForSpeech(p0: Bundle?) {//사용자가 말하기 시작할 준비가 되면 호출됨

                //Toast.makeText(applicationContext, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {//사용자가 말하기 시작했을때 호출됨

            }

            override fun onRmsChanged(p0: Float) {//입력받는 소리의 크기를 알려줌

            }

            override fun onBufferReceived(p0: ByteArray?) {//사용자가 말을 시작하고 인식이 된 단어를 buffer에 담음

            }

            override fun onEndOfSpeech() {//사용자가 말하기를 중지하면 호출됨

            }

            override fun onError(error: Int) {//에러가 났을때
                var message: String

                when(error) {
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "퍼미션 없음"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "서버가 이상함"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "RECGNIZER가 바쁨"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "말하는 시간이 초과됨"
                    else ->
                        message = "알수없는 오류"
                }
                //Toast.makeText(applicationContext, "에러발생 $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {//인식 결과가 준비되면 호출됨
                var matches: ArrayList<String>? = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (matches != null) {
                    for (i in 0 until matches.size) {
                        speechText = matches[i]
                    }
                    if (speechText.contains("시작")){
                        startTTS()
                        autoScroll()
                    }
                    if(speechText.contains("다음")){
                        nextTTS()
                        autoScroll()
                    }
                    if(speechText.contains("이전")){
                        prevTTS()
                        autoScroll()
                    }
                    if(speechText.contains("정지")){
                        stopTTS()
                    }
                    if(speechText.contains("빠르게")){

                    }
                    if (speechText.contains("느리게")){

                    }
                }
            }

            override fun onPartialResults(partiaResults: Bundle?) {//부분 인식 결과를 사용할 수 있을때 호출됨
                var matches: ArrayList<String>? = partiaResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null){
                    for (i in 0 until matches.size) {
                        speechText = matches[i]
                        //println("STT-TEST: PART " + speechText)
                    }
                    if(speechText.contains("멈춰")){
                        stopTTS()
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {//향후 이벤트를 추가하기 위해 예약됨

            }
        }
    }

    private fun show(){//STT무한 실행

        startSTT()
        handler.postDelayed(::show, 1000)
    }

    private fun makeTTS(){//TTS만들기
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // 언어를 선택한다.
                tts?.language = Locale.KOREAN
            }
        }
    }

    private fun startTTS(){//TTS시작
        if(recipeTextList[selectedRecipe] == "")
            selectedRecipe += 1
        tts?.speak(recipeTextList[selectedRecipe], TextToSpeech.QUEUE_FLUSH, null)
    }

    private fun nextTTS(){//다음 TTS
        selectedRecipe += 1
        tts?.speak(recipeTextList[selectedRecipe], TextToSpeech.QUEUE_FLUSH, null)
        if(selectedRecipe > recipeTextList.size){
            selectedRecipe = 0
        }
    }

    private fun prevTTS(){//이전 TTS
        selectedRecipe -= 1
        if (selectedRecipe<0) {
            selectedRecipe = 0
        }
        tts?.speak(recipeTextList[selectedRecipe], TextToSpeech.QUEUE_FLUSH, null)
    }

    private fun stopTTS(){//TTS멈춰!!
        tts?.stop()
    }

    private fun autoScroll(){
        if(selectedRecipe == 0)
            binding.nestrdScrollView.scrollTo(0, binding.textView3.bottom)
        else
            binding.nestrdScrollView.scrollTo(0, binding.rvRecipe[selectedRecipe+1].bottom)
    }

}