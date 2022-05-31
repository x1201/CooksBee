package com.example.capstone

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList

class RandomRecipeAdapter(val searchList: ArrayList<NameInfo>) : RecyclerView.Adapter<RandomRecipeAdapter.CustomViewHolder>(){
    val range = (0..searchList.size-1)
    lateinit var RandomRecipe : ArrayList<NameInfo>
    val RandomNumList = mutableListOf<Int>()

    inner class CustomViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.recipe_image, parent, false)){
        val recipePicture = itemView.findViewById<ImageView>(R.id.imageView_recipe)
        val recipeTitle = itemView.findViewById<TextView>(R.id.imageView_title)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CustomViewHolder((parent))

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        RandomRecipe = ArrayList()

        while(RandomNumList.size<4){
            var a = range.random()
            if(RandomNumList.contains(a)){
                continue
            }
            RandomNumList.add(a)
        }
        Log.d(TAG,"list = ${RandomNumList}")

        for(i in 0 until RandomNumList.size){
            RandomRecipe.add(NameInfo(searchList[RandomNumList[i]].id,searchList[RandomNumList[i]].name,searchList[RandomNumList[i]].ingredient,searchList[RandomNumList[i]].url,searchList[RandomNumList[i]].tag))
        }


        Glide.with(holder.recipePicture.context).load(RandomRecipe.get(position).url).into(holder.recipePicture)
        holder.recipeTitle.text = RandomRecipe.get(position).name
        val clickListId = RandomRecipe.get(position).id

        holder.recipePicture.setOnClickListener {
            val intent = Intent(holder.recipePicture?.context, RecipePage::class.java)
            intent.putExtra("clickListId", clickListId)
            ContextCompat.startActivity(holder.recipePicture.context, intent, null)
        }

    }
    override fun getItemCount(): Int = 4
}