package com.example.birzha

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

lateinit var plot: GraphView
lateinit var series: LineGraphSeries<DataPoint>
var time = 0
val mainUser = makeMainUser()
var openPrice = 0
var isRun = false
var resume = false
//var LightOrNo = false

@SuppressLint("StaticFieldLeak")
lateinit var curValueText: TextView

class MainActivity : Activity() {
    private var profit = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        curValueText = findViewById(R.id.curValue)
    }

    fun closeActivity(view: View) {
        view as Button
        when (view.id) {
            R.id.closeActivity -> {
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickStartStop(view: View) {
        view as Button
        if (isRun) {
            "Start".also { view.text = it }
            isRun = false
            resume = true

        } else if (!isRun) {
            isRun = true
            "Stop".also { view.text = it }
            if (!resume) {
                val mainUserMoney: TextView? = findViewById(R.id.mainUserMoney)

                mainUserMoney?.text = mainUser.money.toString()
                mainUserAssets?.text = mainUser.assets.toString()

                plot = findViewById(R.id.graph)
                series = LineGraphSeries(
                    arrayOf(
                        DataPoint(0.0, 100.0)
                    )
                )

                plot.viewport.setMinX(1.0)
                plot.viewport.setMaxX(60.0)
                //plot.addSeries(series)

                GlobalScope.launch {
                    birzhaMain()
                }
            }
        }

        plot.viewport?.isScalable = true
        plot.viewport?.setScalableY(false)
        plot.viewport?.isScrollable = false
        plot.viewport?.isXAxisBoundsManual = true

        /*
        GlobalScope.launch {
            while (true) {
                curValue.text = currentPrice.toString()

                if (status == "buy" && openPrice != 0) {
                    profit = currentPrice - openPrice
                    profitValue.text = profit.toString()
                }
                if (status == "sell" && openPrice != 0) {
                    profit = openPrice - currentPrice
                    profitValue.text = profit.toString()
                }
                if (status == "none") {
                    profitValue.text = ""
                }
            }
        }
        */
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickBuy(view: View) {
        view as Button
        val inputAmount = findViewById<EditText>(R.id.inputAmount)
        val amount = inputAmount.text.toString().toInt()
        //if (status == "none"){
        //  status = "buy"

        val openBid: TextView = findViewById(R.id.bidType)
        val openBidPrice: TextView = findViewById(R.id.openiBidPrice)

        if (mainUser.money - currentPrice * amount < 0) {
            val text = "Not enough money"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        } else {
            mainUser.assets += amount
            mainUser.money -= currentPrice * amount

            "Buy on".also { openBid.text = it }
            openBidPrice.text = currentPrice.toString()
            mainUserAssets.text = mainUser.assets.toString()
            mainUserMoney.text = mainUser.money.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSell(view: View) {
        view as Button
        val inputAmount = findViewById<EditText>(R.id.inputAmount)
        val amount = inputAmount.text.toString().toInt()
        openPrice = currentPrice

        val openBid: TextView = findViewById(R.id.bidType)
        val openBidPrice: TextView = findViewById(R.id.openiBidPrice)

        if (mainUser.assets - amount < 0) {
            val text = "Not enough stocks"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        } else {
            mainUser.assets -= amount
            mainUser.money += currentPrice * amount

            "Sell on".also { openBid.text = it }
            openBidPrice.text = currentPrice.toString()
            mainUserAssets.text = mainUser.assets.toString()
            mainUserMoney.text = mainUser.money.toString()
        }
    }

/*    fun Light(view: View) {
        view as Button
        if (LightOrNo){
            LightOrNo = false
        }
        else {
            LightOrNo = true
        }
    }*/

    /*fun graphChange(){
        if (!isRun){
            "Start".also { view.text = it }
            isRun = false
            resume = true
        }
    }

     */
}