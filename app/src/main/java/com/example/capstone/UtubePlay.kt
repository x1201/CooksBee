package com.example.capstone

import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayerView
import com.google.android.youtube.player.YouTubePlayer
import android.os.Bundle
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubeInitializationResult

class UtubePlay : YouTubeBaseActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener
    private var speechText: String = "null"//받은 음성
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }//음성인식 무한 실행을 위한 핸들러 변수
    private var ytpv: YouTubePlayerView? = null
    private var ytp: YouTubePlayer? = null
    val serverKey = "AIzaSyA3wjLVxkqdyd-dYtF-_2IRZGYrCd0jKN4" //콘솔에서 받아온 서버키를 넣어줍니다
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utube_play)
        initPlayer()
        val btnPlay = findViewById<Button>(R.id.youtubeBtn)
        val btnPause = findViewById<Button>(R.id.youtubePBtn)
        val btnSeek = findViewById<Button>(R.id.youtubeSBtn)
        val btnBack = findViewById<Button>(R.id.youtubeBaBtn)
        btnPlay.setOnClickListener { playVideo() }
        btnPause.setOnClickListener { pauseVideo() }
        btnSeek.setOnClickListener { seekVideo() }
        btnBack.setOnClickListener { backVideo() }

        muteNoti()
        setListener()
       // show()
    }

    private fun playVideo() {
        if (ytp != null) {
            ytp!!.play()
        }
    }

    private fun pauseVideo() {
        if (ytp != null) {
            if (ytp!!.isPlaying == true) {
                ytp!!.pause()
            } else {
                ytp!!.play()
            }
        }
    }

    private fun seekVideo() {
        if (ytp != null) {
            ytp!!.seekRelativeMillis(10000)
        }
    }

    private fun backVideo() {
        if (ytp != null) {
            ytp!!.seekRelativeMillis(-10000)
        }
    }

    private fun initPlayer() {
        ytpv = findViewById(R.id.playerView)
        ytpv?.initialize(serverKey, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider,
                youTubePlayer: YouTubePlayer,
                wasRestored: Boolean
            ) {
                ytp = youTubePlayer
                val gt = intent
                if (!wasRestored) {
                    ytp!!.cueVideo(gt.getStringExtra("id"))
                }
                ytp!!.setPlayerStateChangeListener(object : PlayerStateChangeListener {
                    override fun onLoading() {}
                    override fun onLoaded(id: String) {
//                        Log.d(TAG, "onLoaded: " + id);
//                        ytp.play(); //자동재생
                    }

                    override fun onAdStarted() {}
                    override fun onVideoStarted() {}
                    override fun onVideoEnded() {}
                    override fun onError(errorReason: YouTubePlayer.ErrorReason) {
//                        Log.d(TAG, "onError: " + errorReason);
                    }
                })
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider,
                youTubeInitializationResult: YouTubeInitializationResult
            ) {
            }
        })
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
                        playVideo()
                    }
                    if(speechText.contains("앞으로")){
                        seekVideo()
                    }
                    if (speechText.contains("뒤로")){
                        backVideo()
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
                        pauseVideo()
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
}