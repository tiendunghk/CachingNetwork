package com.tiendunghk.cachingnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    val URL = "https://tiendunghk.github.io/"
    private lateinit var btnTest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest = findViewById(R.id.btnTest)

        btnTest.setOnClickListener {
            try {
                val result = HttpManager.read(URL, this)
            } catch (e: Exception) {
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
