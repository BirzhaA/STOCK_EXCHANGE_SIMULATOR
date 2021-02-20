package com.example.birzha

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Thread.sleep

lateinit var plot : GraphView
lateinit var series: LineGraphSeries<DataPoint>
var time = 0
val mainUser = makeMainUser()
var status = "none"
var openPrice = 0

class MainActivity : Activity() {
    private var profit = 0
    private var isRun = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //MainActivity.setImageResource(R.drawable.background);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickStartStop(view: View) {
        view as Button
        if (isRun) {
            //view.setImageResource(R.drawable.start)
            while(isRun) sleep(1000)
            isRun = false

        } else if (!isRun) {
            //view.setImageResource(R.drawable.stop)

            val mainUserMoney: TextView? = findViewById(R.id.mainUserMoney)

            mainUserMoney?.text = mainUser.money.toString()

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
        plot.viewport?.setScalableY(false)
        plot.viewport?.isScrollable = true
        plot.viewport?.isXAxisBoundsManual = true

        GlobalScope.launch{
            while(true){

                curValue.text = currentPrice.toString()

                if (status == "buy" && openPrice != 0){
                    profit = currentPrice - openPrice
                    profitValue.text = profit.toString()
                }
                if (status == "sell" && openPrice != 0){
                    profit = openPrice - currentPrice
                    profitValue.text = profit.toString()
                }
                if (status == "none"){
                    profitValue.text = ""
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickBuy(view: View) {
        view as Button

        if (status == "none"){
            status = "buy"
            openPrice = currentPrice

            val openBid: TextView = findViewById(R.id.bidType)
            val openBidPrice: TextView = findViewById(R.id.openiBidPrice)

            openBid.text = "Buy on"
            openBidPrice.text = openPrice.toString()
            mainUser.money -= openPrice
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSell(view: View) {
        view as Button
        if (status == "none"){
            status = "sell"
            openPrice = currentPrice

            val openBid: TextView = findViewById(R.id.bidType)
            val openBidPrice: TextView = findViewById(R.id.openiBidPrice)

            openBid.text = "Sell on"
            openBidPrice.text = openPrice.toString()
            mainUser.money -= openPrice
        }
    }

//    fun closeBid(view: View){
//        view as ImageButton

//        status = "none"
//        mainUser.money -= profit
//        mainUserMoney?.text = mainUser.money.toString()
//
//        val openBid: TextView = findViewById(R.id.bidType)
//        val openBidPrice: TextView = findViewById(R.id.openiBidPrice)
//        openBid.text = ""
//        openBidPrice.text = ""
//
//    }

    /*fun closeBid(view: View){
        view as ImageButton

        status = "none"

        mainUser.money = mainUser.money + profit + openPrice
        mainUserMoney?.text = mainUser.money.toString()

        val profitValue: TextView = findViewById(R.id.profitValue)
        val openBid: TextView = findViewById(R.id.bidType)
        val openBidPrice: TextView = findViewById(R.id.openiBidPrice)
        openBid.text = ""
        openBidPrice.text = ""
        profitValue.text = ""

    }*/


//    fun changeUserMoney() {
//        var textMoney: TextView? = null
//        textMoney = findViewById(R.id.mainUserMoney)
//        textMoney?.text = mainUser.money.toString()
//    }

//    fun changeUserAssets(){
//        var textAssets: TextView? = null
//        textAssets = findViewById(R.id.assets)
//        textAssets?.text = mainUser.assets.toString()
//    }

}