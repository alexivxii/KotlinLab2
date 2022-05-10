package com.example.kotthemes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class NewPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_page)

        //Back button
        val buttonClick = findViewById<Button>(R.id.backPageButton)
        buttonClick.setOnClickListener {
            onBackPressed()
        }

        //Primim valorile pasate din pagina precedenta
        var amplitudes2 = ArrayList<Int>()
        var amplitudesNormat = ArrayList<Int>()

        amplitudes2 = intent.getSerializableExtra("key") as ArrayList<Int>

        println("Amplitudes received:")
        println(amplitudes2)

        //variabilele pentru canvas si bitmap
        val imageV = findViewById<View>(R.id.imageV)

        val bitmap: Bitmap = Bitmap.createBitmap(1000, 600, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)

        //Atributele de culoare/grosime ale liniilor ce vor fi reprezentate
        val paint = Paint().apply {
            color = Color.parseColor("#545AA7")
            strokeWidth = 5F
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeMiter = 2F
        }

        val drawingList = mutableListOf<Float>()
        val drawingList2 = mutableListOf<Float>()

        println("Canvas sizes")
        println(canvas.width)
        println(canvas.height)

        //Desenam dreptunghiul care delimiteaza canvasul

        // first line starting point x y
        drawingList.add(0F) // x
        drawingList.add(0F) // y
        // first line ending point x y
        drawingList.add(canvas.width + 0F)
        drawingList.add(0F)

        drawingList.add(0F) // x
        drawingList.add(0F) // y
        drawingList.add(0F)
        drawingList.add(canvas.height + 0F)

        drawingList.add(0F) // x
        drawingList.add(canvas.height + 0F) // y
        drawingList.add(canvas.width + 0F)
        drawingList.add(canvas.height + 0F)

        drawingList.add(canvas.width + 0F) // x
        drawingList.add(0F) // y
        drawingList.add(canvas.width + 0F)
        drawingList.add(canvas.height + 0F)


        println(drawingList)

        canvas.drawLines(drawingList.toFloatArray(),paint)

        //normare
        var i : Int = 0
        var maxAmp : Int? = amplitudes2.max()

        while (i<amplitudes2.size) {
            amplitudesNormat.add(amplitudes2[i] * canvas.height / maxAmp!!)
            i++
        }

        println("Amplitudes normat:")
        println(amplitudesNormat)

        //adaugarea in vectorul care contine coordonatele capetelor liniilor
        var drawingListIterator : Float = 0F
        var drawingListStep : Float = canvas.width/(amplitudesNormat.size.toFloat())

        i=0
        while(i<amplitudes2.size-1){
            //x1y1
            drawingList2.add(drawingListIterator)
            drawingList2.add(amplitudesNormat[i].toFloat())
            //x2y2
            drawingList2.add(drawingListIterator+ drawingListStep)
            drawingList2.add(amplitudesNormat[i+1].toFloat())

            drawingListIterator = drawingListIterator + drawingListStep
            i++
        }

        println("DrawingList2")
        println(drawingList2)


        canvas.drawLines(drawingList2.toFloatArray(),paint)
//        canvas.scale(1f, -1f, canvas.width / 2f, canvas.height / 2f)

        // now bitmap holds the updated pixels
        // set bitmap as background to ImageView
        imageV.background = BitmapDrawable(getResources(), bitmap)

    }
}