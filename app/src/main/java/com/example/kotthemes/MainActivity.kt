package com.example.kotthemes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    //contor pentru a numara cate permisiuni acceptate avem
    var permNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //instantiere butoane
        val startButtonRef: Button = findViewById(R.id.startButton)
        val stopButtonRef: Button = findViewById(R.id.stopButton)
        val replayButtonRef: Button = findViewById(R.id.replayButton)
        val permButtonRef: Button = findViewById(R.id.permButton)

        //butoanele nu pot fi apasate pana nu verificam daca avem permisiunile de recording si stocare
        startButtonRef.isEnabled = false
        stopButtonRef.isEnabled = false
        replayButtonRef.isEnabled = false

        //verificam daca avem permisiunile de recording si stocare
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) permNumber += 1
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) permNumber += 1
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) permNumber += 1

        //apasam butonul de request permissions
        permButtonRef?.setOnClickListener{

            //orice permisiune ne-ar lipsi, facem request sa primim toate permisiunile
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            else {
                //in cazul in care avem deja permisiunile la apasarea butonului
                permNumber=3
                println("Permissions Already Granted")
            }


        }

        //verificam daca avem toate permisiunile necesare acceptate pentru a activa butoanele
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
        val replayButtonRef: Button = findViewById(R.id.replayButton)

        println("PERMISIUNILE")
        println(permissions)

        if(requestCode == 100) {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                println("Recording Permission Accepted")
                permNumber += 1
            }
            else println("Recording Permission NOT Accepted")

            if(grantResults.isNotEmpty() && grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {
                println("Storage Write Permission Accepted")
                permNumber += 1
            }
            else println("Storage Write Permission NOT Accepted")

            if(grantResults.isNotEmpty() && grantResults[2]==PackageManager.PERMISSION_GRANTED)
            {
                println("Storage Read Permission Accepted")
                permNumber += 1
            }
            else println("Storage Read Permission NOT Accepted")
        }

        //verificam daca avem toate permisiunile necesare acceptate pentru a activa butoanele
        println(permNumber)
        if(permNumber==3){
            startButtonRef.isEnabled = true
            stopButtonRef.isEnabled = true
            replayButtonRef.isEnabled = true
        }

    }

}