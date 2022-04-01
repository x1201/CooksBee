package com.example.capstone

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.ArrayList

class UtubeAdapter(var context: Context, var mList: ArrayList<SearchData>?) :
    RecyclerView.Adapter<UtubeAdapter.UtubeViewHolder>() {
    inner class UtubeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleImage: ImageView
        var titleText: TextView
        var dateText: TextView

        init {
            titleImage = itemView.findViewById(R.id.titleImage)
            titleText = itemView.findViewById(R.id.titleText)
            dateText = itemView.findViewById(R.id.dateText)


            //유튜브 영상을 클릭하면 재생이 되는 액티비티로 이동
            itemView.setOnClickListener {
                val position = adapterPosition
                val intent = Intent(context, UtubePlay::class.java)
                intent.putExtra("id", mList!![position].videoId)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int
    ): UtubeViewHolder {


        //item_utube xml파일을 객체화 시킨다.
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_utube, viewGroup, false)
        return UtubeViewHolder(view)
    }

    override fun onBindViewHolder(viewholder: UtubeViewHolder, position: Int) {


        //영상제목 세팅
        viewholder.titleText.text = mList!![position].title
        //날짜 세팅
        viewholder.dateText.text = mList!![position].publishedAt


        //이미지를 넣어주기 위해 이미지url을 가져온다.
        val imageUrl = mList!![position].imageUrl
        //영상 썸네일 세팅
        Glide.with(viewholder.titleImage)
            .load(imageUrl)
            .into(viewholder.titleImage)
    }

    override fun getItemCount(): Int {
        return if (null != mList) mList!!.size else 0
    }
}