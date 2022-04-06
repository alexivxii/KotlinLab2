package com.example.kotthemes

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException


class MainActivity : AppCompatActivity() {

    //contor pentru a numara cate permisiuni acceptate avem
    var permNumber = 0

    //https://kotlinlang.org/docs/properties.html#late-initialized-properties-and-variables pentru motiv lateinit
    //variabila de tip MediaRecorder
     var mRecorder: MediaRecorder ?=null

    //variabile de tip MediaPlayer
    lateinit var mPlayer: MediaPlayer

    //variabila pentru numele fisierului de stocat
    lateinit var mFileName: String

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
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 100
                )
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

        //functiile asociate butoanelor
        startButtonRef?.setOnClickListener{
            startRecording()
        }

        stopButtonRef?.setOnClickListener {
            stopRecording()
        }

        replayButtonRef?.setOnClickListener {
            playRecording()
        }

    }

    //functia care este lansata la cererea permisiunilor
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    fun startRecording(){

        val textViewTitle: TextView = findViewById(R.id.textTitle)

        //pathul+numele fisierului de stocat
        mFileName="${externalCacheDir?.absolutePath}/KotlinAudioRecording.3gp"
        //mFileName="${externalCacheDir?.absolutePath}/KotlinAudioRecording"

        println(mFileName)

        mRecorder = MediaRecorder()

        //sursa sunetului
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

        //formatul fisierului audio
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //setarea encoderului pentru fisierul audio
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //setarea locatiei output-ului inregistrarii
        mRecorder!!.setOutputFile(mFileName);


        //pregatim variabila de tip MediaRecorder pentru inregistrare
        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e("TAG", "prepare() failed")
        }

        mRecorder!!.start();
        //schimbam culoarea titlului in rosu pentru a stii ca suntem in modul de recording
        textViewTitle.setTextColor(Color.parseColor("#ff5243"))

    }

    //redarea inregistrarii
    fun playRecording(){
        mPlayer = MediaPlayer()
        mPlayer.setDataSource(mFileName)
        mPlayer.prepare();
        mPlayer.start();
    }

    //oprirea inregistrarii
    fun stopRecording(){
        mRecorder!!.stop()
        mRecorder!!.release()
        mRecorder = null

        val textViewTitle: TextView = findViewById(R.id.textTitle)
        textViewTitle.setTextColor(Color.parseColor("#808080"))

    }

}