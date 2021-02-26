package com.example.birzha

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class StartActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    fun newActivity(view: View) {
        view as Button
        when (view.id) {
            R.id.newGame -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }
    }

/*    fun continueActivity(view: View) {
        view as Button
        when (view.id) {
            R.id.conGame -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }
    }*/
}