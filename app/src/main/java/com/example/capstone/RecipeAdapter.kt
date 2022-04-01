package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(val recipesList: ArrayList<Recipes>) : RecyclerView.Adapter<RecipeAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView){
        val photo = itemView.findViewById<ImageView>(R.id.recipeImage)
        val Text = itemView.findViewById<TextView>(R.id.textRecipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(recipesList.get(position).url).into(holder.photo)
        holder.Text.text = recipesList.get(position).recipeText
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }
}