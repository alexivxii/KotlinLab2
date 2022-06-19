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
import java.lang.Math.*

class Complex(val re: Double, val im: Double) {
    operator infix fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    operator infix fun minus(x: Complex) = Complex(re - x.re, im - x.im)
    operator infix fun times(x: Double) = Complex(re * x, im * x)
    operator infix fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    operator infix fun div(x: Double) = Complex(re / x, im / x)
    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }

    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }

    private val a = "%1.3f".format(re)
    private val b = "%1.3f".format(abs(im))
}

object FFT {
    fun fft(a: Array<Complex>) = _fft(a, Complex(0.0, 2.0), 1.0)
    fun rfft(a: Array<Complex>) = _fft(a, Complex(0.0, -2.0), 2.0)

    private fun _fft(a: Array<Complex>, direction: Complex, scalar: Double): Array<Complex> =
        if (a.size == 1)
            a
        else {
            val n = a.size
            require(n % 2 == 0, { "The Cooley-Tukey FFT algorithm only works when the length of the input is even." })

            var (evens, odds) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
            for (i in a.indices)
                if (i % 2 == 0) evens += a[i]
                else odds += a[i]
            evens = _fft(evens, direction, scalar)
            odds = _fft(odds, direction, scalar)

            val pairs = (0 until n / 2).map {
                val offset = (direction * (java.lang.Math.PI * it / n)).exp * odds[it] / scalar
                val base = evens[it] / scalar
                Pair(base + offset, base - offset)
            }
            var (left, right) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
            for ((l, r) in pairs) { left += l; right += r }
            left + right
        }
}

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

        ////////////////////////////////////////////////////////////////////////////////////////

        //Exemplu de fft
        val dataInputFFT = arrayOf(Complex(1.0, 0.0), Complex(1.0, 0.0), Complex(1.0, 0.0), Complex(1.0, 0.0),
            Complex(0.0, 0.0), Complex(0.0, 2.0), Complex(0.0, 0.0), Complex(0.0, 0.0))

        val rezFFT = FFT.fft(dataInputFFT)

        println("Exemplu FFT")
        var j=0
        while(j<rezFFT.size){
            println(rezFFT[j])
            j++
        }

        //TODO Canvas Spectrogram ----------------------------------------------------------------

        //variabilele pentru canvas si bitmap
        val imageSP = findViewById<View>(R.id.imageSpectrogram)

        val bitmapSP: Bitmap = Bitmap.createBitmap(1000, 600, Bitmap.Config.ARGB_8888)
        val canvasSP: Canvas = Canvas(bitmapSP)

        val drawingListSP = mutableListOf<Float>()

        //Desenam dreptunghiul care delimiteaza canvasul

        // first line starting point x y
        drawingListSP.add(0F) // x
        drawingListSP.add(0F) // y
        // first line ending point x y
        drawingListSP.add(canvas.width + 0F)
        drawingListSP.add(0F)

        drawingListSP.add(0F) // x
        drawingListSP.add(0F) // y
        drawingListSP.add(0F)
        drawingListSP.add(canvas.height + 0F)

        drawingListSP.add(0F) // x
        drawingListSP.add(canvas.height + 0F) // y
        drawingListSP.add(canvas.width + 0F)
        drawingListSP.add(canvas.height + 0F)

        drawingListSP.add(canvas.width + 0F) // x
        drawingListSP.add(0F) // y
        drawingListSP.add(canvas.width + 0F)
        drawingListSP.add(canvas.height + 0F)

        canvasSP.drawLines(drawingListSP.toFloatArray(),paint)

        //Atributele de culoare/grosime ale punctelor ce vor fi reprezentate
        val paintSP = Paint().apply {
            color = Color.parseColor("#03fc35")
            strokeWidth = 5F
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeMiter = 2F
        }

        paintSP.color = Color.rgb(255,255,0)

        canvasSP.drawPoint(300F, 300F,paintSP)

        paintSP.color = Color.rgb(255,0,0)

        canvasSP.drawPoint(305F, 300F,paintSP)

        imageSP.background = BitmapDrawable(getResources(), bitmapSP)
    }
}