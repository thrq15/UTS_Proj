package com.example.uts_map_lab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    private lateinit var dateTimeTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var openCameraButton: Button
    private lateinit var submitButton: Button
    private lateinit var historyButton: Button
    private lateinit var profileButton: Button
    private lateinit var logoutButton: Button

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateDateTime()
            handler.postDelayed(this, 1000) // Update setiap 1 detik
        }
    }

    private lateinit var photoUri: Uri

    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()

        dateTimeTextView = findViewById(R.id.dateTimeTextView)
        imageView = findViewById(R.id.imageView)
        openCameraButton = findViewById(R.id.openCameraButton)
        submitButton = findViewById(R.id.submitButton)
        historyButton = findViewById(R.id.historyButton)
        profileButton = findViewById(R.id.profileButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Mulai memperbarui waktu
        handler.post(updateTimeRunnable)

        // Register camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Tampilkan gambar di ImageView
                imageView.setImageURI(photoUri)

                // Upload photo to Firebase Storage
                val storageRef = storage.reference
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileRef = storageRef.child("attendance_photos/${auth.currentUser?.uid}_$timeStamp.jpg")

                fileRef.putFile(photoUri)
                    .addOnSuccessListener {
                        // Store photo URL in Firestore
                        fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val attendanceData = hashMapOf(
                                "userId" to auth.currentUser?.uid,
                                "photoUri" to downloadUri.toString(),
                                "timestamp" to timeStamp
                            )

                            db.collection("attendance")
                                .add(attendanceData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Attendance submitted!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to submit attendance.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to upload photo.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No was photo taken.", Toast.LENGTH_SHORT).show()
            }
        }

        // Open camera button click listener
        openCameraButton.setOnClickListener {
            checkCameraPermission()
        }

        // Navigate to History page
        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Profile page
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Logout button click listener
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateDateTime() {
        val currentDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(cameraIntent)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable) // Hentikan pembaruan saat aktivitas dihancurkan
    }
}
