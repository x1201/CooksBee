package com.example.capstone

import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubePlayerView
import com.google.android.youtube.player.YouTubePlayer
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubeInitializationResult

class UtubePlay : YouTubeBaseActivity() {
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
}