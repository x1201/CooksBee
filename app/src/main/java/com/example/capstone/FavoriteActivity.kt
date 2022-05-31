package com.example.capstone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.databinding.FavoritePageBinding
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteActivity : AppCompatActivity() {

    private val binding by lazy { FavoritePageBinding.inflate(layoutInflater) }
    var favoriteList = ArrayList<NameInfo>()
    var appdb : AppDatabase? = null
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        appdb = AppDatabase.getInstance(this)
        var savedContacts = appdb!!.contactsDao().getAll()
        val contactsId = ArrayList<String>()
        for (i in 0..savedContacts.size-1){
            contactsId.add(savedContacts[i].id)
            Log.d("savrdContacts", " " + savedContacts)
        }
        if(contactsId.isNotEmpty()) {
            for (i in 0..contactsId.size - 1) {
                db.collection("recipe")
                    .document(contactsId[i])
                    .get()
                    .addOnSuccessListener { document ->
                        favoriteList!!.add(
                            NameInfo(
                                contactsId[i],
                                document["name"] as String,
                                document["ingredient"] as String,
                                document["picture"] as String,
                                document["tag"] as String
                            )
                        )
                        binding.rvFavorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                        binding.rvFavorites.setHasFixedSize(true)
                        binding.rvFavorites.adapter = SearchAdapter(favoriteList)
                    }
            }
        }else{
            Log.d("contactsId is empty", "empty")
        }

    }

    override fun onRestart() {
        super.onRestart()
        appdb = AppDatabase.getInstance(this)
        var savedContacts = appdb!!.contactsDao().getAll()
        val contactsId = ArrayList<String>()
        favoriteList.removeAll(favoriteList)
        for (i in 0..savedContacts.size-1){
            contactsId.add(savedContacts[i].id)
            Log.d("savrdContacts", " " + savedContacts)
        }
        if(contactsId.isNotEmpty()) {
            for (i in 0..contactsId.size - 1) {
                db.collection("recipe")
                    .document(contactsId[i])
                    .get()
                    .addOnSuccessListener { document ->
                        favoriteList!!.add(
                            NameInfo(
                                contactsId[i],
                                document["name"] as String,
                                document["ingredient"] as String,
                                document["picture"] as String,
                                document["tag"] as String
                            )
                        )
                        SearchAdapter(favoriteList).notifyDataSetChanged()
                        binding.rvFavorites.adapter = SearchAdapter(favoriteList)
                    }
            }
        }
    }

}