package com.example.uts_map_lab

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Inisialisasi Firestore dan RecyclerView
        firestore = FirebaseFirestore.getInstance()
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)

        // Buat Adapter dan set ke RecyclerView
        historyAdapter = HistoryAdapter(historyList)
        recyclerViewHistory.adapter = historyAdapter

        // Ambil data dari Firestore
        fetchHistoryData()
    }

    private fun fetchHistoryData() {
        firestore.collection("history")
            .get()
            .addOnSuccessListener { documents ->
                // Jika berhasil mengambil data
                for (document in documents) {
                    val photoUrl = document.getString("photoUrl") ?: ""
                    val timestamp = document.getString("timestamp") ?: ""

                    // Tambahkan data ke list
                    historyList.add(HistoryItem(photoUrl, timestamp))
                }
                // Beritahu adapter bahwa data telah berubah
                historyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("HistoryActivity", "Error getting documents: ", exception)
            }
    }
}
