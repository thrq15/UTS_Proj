package com.example.uts_map_lab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class HistoryItem(val photoUrl: String, val timestamp: String)

class HistoryAdapter(private val historyList: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewPhoto: ImageView = view.findViewById(R.id.imageViewPhoto)
        val textViewTimestamp: TextView = view.findViewById(R.id.textViewTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]

        // Menggunakan Glide untuk memuat gambar
        Glide.with(holder.itemView.context)
            .load(historyItem.photoUrl)
            .into(holder.imageViewPhoto)

        holder.textViewTimestamp.text = historyItem.timestamp
    }

    override fun getItemCount() = historyList.size
}
