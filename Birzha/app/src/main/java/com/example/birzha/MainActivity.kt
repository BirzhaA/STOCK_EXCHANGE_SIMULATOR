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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


lateinit var plot : GraphView
lateinit var series: LineGraphSeries<DataPoint>
var time = 0
val mainUser = makeMainUser()

class MainActivity : Activity() {

  var isRun = false
    @SuppressLint("SetTextI18n")
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
        } else if (!isRun) {
            view.setImageResource(R.drawable.stop)

            val mainUserMoney: TextView? = findViewById(R.id.mainUserMoney)
            val mainUserAssets: TextView? = findViewById(R.id.mainUserAssets)

            mainUserMoney?.text = mainUser.money.toString()
            mainUserAssets?.text = mainUser.assets.toString()

            plot = findViewById(R.id.graph)
            series = LineGraphSeries(
                arrayOf(
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
//        plot.legendRenderer.setFixedPosition(5, 20)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickBuy(view: View) {
        view as ImageButton

        val openBid: TextView = findViewById(R.id.bidType)
        val openBidPrice: TextView = findViewById(R.id.openiBidPrice)
        val profitValue: TextView = findViewById(R.id.profitValue)

        val buyPrice = currentPrice

        openBid.text = "Buy on"
        openBidPrice.text = buyPrice.toString()
        profitValue.text = (currentPrice - buyPrice).toString()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSell(view: View) {
        view as ImageButton
        val openBid: TextView = findViewById(R.id.bidType)
        val openBidPrice: TextView = findViewById(R.id.openiBidPrice)
        val profitValue: TextView = findViewById(R.id.profitValue)

        val sellPrice = currentPrice

        openBid.text = "Sell on"
        openBidPrice.text = sellPrice.toString()
        profitValue.text = (currentPrice - sellPrice).toString()
    }

//    fun closeBid(view: View){
//
//        val profitValue: TextView = findViewById(R.id.profitValue)
//
//        mainUser.money -= profitValue.toInt()
//        mainUserMoney?.text = mainUser.money.toString()
//
//    }

//    fun changeUserMoney() {
//        var textMoney: TextView? = null
//        textMoney = findViewById(R.id.mainUserMoney)
//        textMoney?.text = mainUser.money.toString()
//    }
//
//    fun changeUserAssets(){
//        var textAssets: TextView? = null
//        textAssets = findViewById(R.id.assets)
//        textAssets?.text = mainUser.assets.toString()
//    }

    //fun inputAssets(){}

}