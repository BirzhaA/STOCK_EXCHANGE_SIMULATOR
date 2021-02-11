package com.example.birzha

import android.R.layout
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.birzha.R.id
import com.example.birzha.R.layout.*

abstract class ScreenActivity : Activity(), DialogInterface.OnClickListener {

     public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)
        //btnActTwo = findViewById<View>(R.id.) as Button
        //btnActTwo!!.setOnClickListener(this)
    }

    fun startActivity(view: View) {
        view as ImageButton
        when (view.id) {
            R.id.startActivity -> {
                val intent = Intent(this, ScreenActivity::class.java)
                startActivity(intent)
           }
            else -> {
            }
        }
    }
}