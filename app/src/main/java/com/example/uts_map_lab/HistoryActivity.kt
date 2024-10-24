package com.example.uts_map_lab

import android.annotation.SuppressLint
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Inisialisasi Firestore dan RecyclerView
        firestore = FirebaseFirestore.getInstance()
        recyclerViewHistory = findViewById(R.id.imageViewPhoto)
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
                    val photoUrl = document.getString("https://firebasestorage.googleapis.com/v0/b/uts-map-lab-a3f5c.appspot.com/o/attendance_photos%2FPgoFL6MWg9U7vgnPg6RwgfLIC0T2_20241024_091635.jpg?alt=media&token=2a44878a-1d1e-4160-a6db-6038b73a775d") ?: ""
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
