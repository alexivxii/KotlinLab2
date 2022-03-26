package com.example.kotthemes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    var permNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val startButtonRef: Button = findViewById(R.id.startButton)
        val stopButtonRef: Button = findViewById(R.id.stopButton)
        val replayButtonRef: Button = findViewById(R.id.replayButton)
        val permButtonRef: Button = findViewById(R.id.permButton)

        startButtonRef.isEnabled = false
        stopButtonRef.isEnabled = false
        replayButtonRef.isEnabled = false

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) permNumber += 1
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) permNumber += 1
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) permNumber += 1

        permButtonRef?.setOnClickListener{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
            else {
                println("Recording Permission Already Granted")
            }

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            else {
                println("Storage Write Permission Already Granted")
            }

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            else {
                println("Storage Read Permission Already Granted")
            }

        }

        println()
        println(permNumber)
        if(permNumber==3){
            startButtonRef.isEnabled = true
            stopButtonRef.isEnabled = true
            replayButtonRef.isEnabled = true
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val startButtonRef: Button = findViewById(R.id.startButton)
        val stopButtonRef: Button = findViewById(R.id.stopButton)

        println("PERMISIUNILE")
        println(permissions)

        if(requestCode == 100) {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                println("Recording Permission Accepted")
                permNumber += 1
            }
            else println("Recording Permission NOT Accepted")
        }
        else if(requestCode == 101){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                println("Storage Write Permission Accepted")
                permNumber += 1
            }
            else println("Storage Write Permission NOT Accepted")
        }
        else if(requestCode == 102){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                println("Storage Read Permission Accepted")
                permNumber += 1
            }
            else println("Storage Read Permission NOT Accepted")
        }

    }

}