package com.example.birzha

//import com.jjoe64.graphview.GraphView
//import com.jjoe64.graphview.series.DataPoint
//import com.jjoe64.graphview.series.LineGraphSeries
import android.R.attr.entries
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


lateinit var plot : LineChart
//val xValues = ArrayList<String>()
val lineEntry = ArrayList<Entry>()
val dataSet = LineDataSet(lineEntry, "First")
var data = LineData(dataSet)
var time = 0
val mainUser = makeMainUser()
//var status = "none"
var openPrice = 0
var isRun = false
val startMoney = 1000
var resume = false

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

        } else if (!isRun) {
            isRun = true
            "Stop".also { view.text = it }

            if(!resume){
                val mainUserMoney: TextView? = findViewById(R.id.mainUserMoney)

                mainUserMoney?.text = mainUser.money.toString()
                mainUserAssets?.text = mainUser.assets.toString()

                plot = findViewById(R.id.graph)




//                series = LineGraphSeries(
//                    arrayOf(
//                        DataPoint(0.0, 100.0)
//                    )
//                )
//                plot.addSeries(series)

                GlobalScope.launch {
                    birzhaMain()
                }
            }
        }

//        plot.viewport?.isScalable = true
//        plot.viewport?.setScalableY(false)
//        plot.viewport?.isScrollable = true
//        plot.viewport?.isXAxisBoundsManual = true
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
            /*AlertDialog.Builder() builder = new AlertDialog.Builder(MainActivity.this)
            builder.setMassege("No money")*/
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
            //всплывающее окно с ошибкой alertDialog пока не реализовано
        }
        else {
            mainUser.assets -= amount
            mainUser.money += currentPrice * amount

            "Sell on".also { openBid.text = it }
            openBidPrice.text = currentPrice.toString()
            mainUserAssets.text = mainUser.assets.toString()
            mainUserMoney.text = mainUser.money.toString()
        }
    }


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