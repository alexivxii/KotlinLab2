package com.example.kotthemes

import android.Manifest
import android.R.attr.key
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.IOException


class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener{

    //GRAPH
    var series: LineGraphSeries<DataPoint> = LineGraphSeries()

    //contor pentru a numara cate permisiuni acceptate avem
    var permNumber = 0

    //https://kotlinlang.org/docs/properties.html#late-initialized-properties-and-variables pentru motiv lateinit
    //variabila de tip MediaRecorder
    lateinit var mRecorder: MediaRecorder

    //variabile de tip MediaPlayer
    lateinit var mPlayer: MediaPlayer

    //variabila pentru numele fisierului de stocat
    lateinit var mFileName: String

    //TIMER timer
    lateinit var timer: Timer
    var recDuration = 0L

    //TIMER audio amplitudes
    var amplitudes = ArrayList<Int>()
    var amplitudesTemp = ArrayList<Int>()

    //TODO: AudioRecord pentru spectrograma

    var bufferSizeInFloats2 = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT)
    val record2AudioRecordFloats = AudioRecord(MediaRecorder.AudioSource.MIC,16000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_FLOAT,bufferSizeInFloats2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TIMER instantiere timer
        timer = Timer(this)

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
            stopButtonRef.isEnabled = false
            replayButtonRef.isEnabled = false
        }

        //functiile asociate butoanelor
        startButtonRef?.setOnClickListener{

            stopButtonRef.isEnabled = true
            startRecording()
        }

        stopButtonRef?.setOnClickListener {
            stopRecording()
            stopButtonRef.isEnabled = false
            replayButtonRef.isEnabled = true
        }

        replayButtonRef?.setOnClickListener {
            playRecording()
        }


        //GRAPH

        var graph: GraphView = findViewById(R.id.graph)

        //var series: LineGraphSeries<DataPoint> = LineGraphSeries()

//        var x: Double = 0.0
//        var y: Double = 1.0
//        var DP1 : DataPoint = DataPoint(x,y)
//        series.appendData(DP1, true,90)
//        x=1.0
//        y=5.0
//        DP1 = DataPoint(x,y)

//        var DP2: Array<DataPoint> = arrayOf(
//                DataPoint(0.0,5.0),
//                DataPoint(1.0,1.0),
//                DataPoint(2.0,3.0),
//                DataPoint(3.0,7.0),
//                DataPoint(4.0,4.0),
//                DataPoint(5.0,6.0),
//                DataPoint(6.0,2.0),
//                DataPoint(7.0,3.0),
//                DataPoint(8.0,9.0),
//                DataPoint(9.0,2.0),
//                DataPoint(10.0,9.0)
//        )
//
//        for (i:DataPoint in DP2){
//            series.appendData(i,true,90)
//        }
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-1.0);
        graph.getViewport().setMaxY(12.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(12.0);

        graph.viewport.setScrollableY(true)
        graph.viewport.setScalableY(true)
        graph.addSeries(series)

        //GRAPH END --------------------------------------------------------------

        //Next page button
        val buttonNewPage = findViewById<Button>(R.id.newPageButton)
        buttonNewPage.setOnClickListener {

            val newPageBundle = Bundle()
            newPageBundle.putIntegerArrayList("key", amplitudes)
            val intent = Intent(this, NewPage::class.java)
            intent.putExtras(newPageBundle)

            startActivity(intent)
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
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)

        //formatul fisierului audio
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //setarea encoderului pentru fisierul audio
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //setarea locatiei output-ului inregistrarii
        mRecorder?.setOutputFile(mFileName);


        //pregatim variabila de tip MediaRecorder pentru inregistrare
        try {
            mRecorder?.prepare()
        } catch (e: IOException) {
            Log.e("TAG", "prepare() failed")
        }

        amplitudes.clear()

        mRecorder?.start()
        //schimbam culoarea titlului in rosu pentru a stii ca suntem in modul de recording
        textViewTitle.setTextColor(Color.parseColor("#ff5243"))

        //TIMER pornim timerul
        timer.start()

        //TODO AudioRecord pentru Spectrograma

        record2AudioRecordFloats.startRecording()

    }

    //redarea inregistrarii
    fun playRecording(){
        mPlayer = MediaPlayer()
        mPlayer.setDataSource(mFileName)
        mPlayer.prepare()

        mPlayer.start()
    }

    //oprirea inregistrarii
    fun stopRecording(){
        mRecorder?.stop()
        mRecorder?.release()
        //mRecorder = null

        val textViewTitle: TextView = findViewById(R.id.textTitle)
        textViewTitle.setTextColor(Color.parseColor("#808080"))

        //TIMER oprim timerul
        recDuration=timer.duration
        timer.stop()
        println("Final Time " + recDuration)
        println("Amp array length " + amplitudes.size)
        println(amplitudes)

        //GRAPH
        amplitudesTemp = amplitudes

        series=LineGraphSeries()

        println("Series empty " + series.isEmpty)

        var DP1: DataPoint
        var indexul:Int = 0
        for (i:Int in amplitudesTemp){
            //println(indexul)
            DP1=DataPoint(indexul.toDouble(), i.toDouble())
            series.appendData(DP1, true, 90)
            indexul++
        }

        println("Numar esantioane: " + indexul)

        var graph: GraphView = findViewById(R.id.graph)

        graph.removeAllSeries()

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-1.0);
        graph.getViewport().setMaxY(15000.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(30.0);

        graph.viewport.setScrollableY(true)
        graph.viewport.setScalable(true)

        graph.addSeries(series)

        //TODO AudioRecord pentru Spectrograma

        var lengthAudioRecFloat : Int = 0
        var sampleuriFloats = FloatArray(15600)

        lengthAudioRecFloat = record2AudioRecordFloats.read(sampleuriFloats,0,15600,AudioRecord.READ_NON_BLOCKING)
        println("Length2 Floats Record Read")
        println(lengthAudioRecFloat)

        var contor : Int = 0
        while(contor < 200)
        {
            println(sampleuriFloats[contor])
            contor++
        }
        println("Gata sampleurile")

        record2AudioRecordFloats.stop()
        record2AudioRecordFloats.release()

    }

    //TIMER afisarea milisecundelor
    override fun OnTimerTick(duration: String) {
        println(duration)

        //TIMER adaugam in vectorul de amplitudini esantioanele
        amplitudes.add(mRecorder?.maxAmplitude)
    }

}