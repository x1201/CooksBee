package com.example.capstone

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.databinding.CategoripageBinding

class CategoriActivity : AppCompatActivity() {
    private val binding by lazy { CategoripageBinding.inflate(layoutInflater)}
    lateinit var  catchkategori: ArrayList<NameInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        catchkategori = intent.getSerializableExtra("sendKategori") as ArrayList<NameInfo>
        binding.KategoriPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.KategoriPageRecyclerView.setHasFixedSize(true)
        binding.KategoriPageRecyclerView.adapter = CategoriAdapter(catchkategori)
        for(i in 0 until catchkategori.size){
            Log.d(TAG,"${i}번째 ${catchkategori[i].tag}태그입니다")
        }
    }
}