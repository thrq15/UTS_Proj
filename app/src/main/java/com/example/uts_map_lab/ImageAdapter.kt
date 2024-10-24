package com.example.uts_map_lab

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ImageAdapter (private var items:List<Item>, private val context: Context):
        RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val imageView : ImageView = view.findViewById(R.id.imageViewPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.history_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ImageAdapter.ViewHolder, position: Int) {
        val item = items[position]
//        Picasso.get().load(item).into(holder.itemView)
    }


}