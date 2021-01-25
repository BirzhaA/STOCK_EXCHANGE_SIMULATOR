package com.example.birzha

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.*
import kotlin.math.sin

lateinit var plot : GraphView
lateinit var series: LineGraphSeries<DataPoint>
var time = 0

class MainActivity : Activity() {

  var isRun = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickStartStop(view: View) {
        view as ImageButton
        if (isRun) {
            view.setImageResource(R.drawable.start)
            isRun = false
            
        }
        else if (!isRun) {
            view.setImageResource(R.drawable.stop)

            plot = findViewById(R.id.graph)
            series = LineGraphSeries<DataPoint>(
                arrayOf<DataPoint>(
                    DataPoint(0.0, 100.0)
                )
            )
            plot.addSeries(series)

            GlobalScope.launch {
                birzhaMain()
            }
            isRun = true

        }
        plot.viewport?.isScalable = true
        plot.viewport?.setScalableY(true)
        plot.viewport?.isScrollable = true
        plot.viewport?.isXAxisBoundsManual = true
    }
}



