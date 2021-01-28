package com.example.birzha

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.*


lateinit var plot : GraphView
lateinit var series: LineGraphSeries<DataPoint>
var time = 0

class MainActivity : Activity() {

  var isRun = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var textBidName : TextView? = null
        textBidName = findViewById(R.id.BidName)
        textBidName?.text = "Amount assets: "
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickBuy(view: View) {
        view as ImageButton
        makeBid(-1, currentPrice, 5, 0, -1.0)
        // if(mainUser.money - 5 * currentPrice >= 0) tradeBuy()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSell(view: View) {
        view as ImageButton
        makeBid(-1, currentPrice, 5, 1, -1.0)
        //if(mainUser.assets - 5 >= 0) tradeSell()
    }

    fun changeUserMoney() {
        var textMoney: TextView? = null
        textMoney = findViewById(R.id.money)
        textMoney?.text = mainUser.money.toString()
    }

    fun changeUserAssets(){
        var textAssets: TextView? = null
        textAssets = findViewById(R.id.assets)
        textAssets?.text = mainUser.assets.toString()
    }

    //fun inputAssets(){}

}