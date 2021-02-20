//@file:Suppress("NAME_SHADOWING")

package com.example.birzha//import java.util.Random
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.series.DataPoint
import kotlinx.coroutines.*
import java.lang.System.currentTimeMillis
import kotlin.math.min
import kotlin.random.Random

// global variables
var currentPrice : Int = 100
lateinit var people : MutableList<Person>
var sells : MutableList<Bid> = mutableListOf()
//val compareByPriceAsc: Comparator<Bid> = compareBy { it.price }
//val compareByPriceDesc: Comparator<Bid> = compareByDescending { it.price }
//var sells = PriorityQueue<Bid>() { a, b -> a.price - b.price }
//var buys = PriorityQueue<Bid>() { a, b -> b.price - a.price }
var buys : MutableList<Bid> = mutableListOf()
//var priceHistory : MutableList<Int> = mutableListOf()

/////////////////КЛАССЫ/////////////////
class Person (
    val ID : Int,
    var money : Int,
    var assets : Int,
    var averagePrice : Int,
    var interestHigh: Double,
    var interestLow: Double,
    var sellInterest : Double,
    var potency : Double
)
class Bid(val personID: Int, val price: Int, var amount: Int, val code: Int, val id: Double, val hedgeOrNo: Boolean, val timeOfCreation: Long)  // класс ставки 0-покупка 1-продажа 2-high 3-low
enum class BidType(val code: Int) {
    BUY(0), SELL(1), SELL_HIGH(2), SELL_LOW(3)
}

/////////////////ФУНКЦИИ///////////////

suspend fun addDot(){
    runBlocking (Dispatchers.Main) {
        time += 1
        if(currentPrice <= 25) currentPrice += Random.nextInt(10, 20)
        series.appendData(DataPoint(time.toDouble(), currentPrice.toDouble()), true, time)
        plot.addSeries(series)
        curValueText.text = currentPrice.toString()
        delay(500)
    }
}

fun makeMainUser(): Person {
    //val x = (Random.nextDouble(10.0, 15.0)).toFloat()
    //val money =  ( (1.2361 * x*x*x - 0.6961 * x*x  + 0.4591 * x - 0.0055)  * 1.1).toInt()
    val money = startMoney
    val assets = 100
    return Person (
        -1, money, assets, currentPrice,
        Random.nextDouble(1.01, 2.0),
        Random.nextDouble(0.50, 0.99),
        Random.nextDouble(1.01, 1.5),
        Random.nextDouble(0.1, 0.9)
    )
}

suspend fun initialDistributionOfMoney(n:Int): MutableList<Int>{
    val money : MutableList<Int> = mutableListOf()
    for (i in 0 until n){
        val x = (Random.nextDouble(20.0, 50.0)).toFloat()
        money.add( ( (1.2361 * x*x*x - 0.6961 * x*x  + 0.4591 * x - 0.0055)  * 1.1).toInt() )
    }
    return money
}

suspend fun makePerson(id: Int, initialMoney: Int): Person {                                     // создаем человека
    val assets = (initialMoney * Random.nextDouble(0.0, 1.0) / currentPrice).toInt()
    return Person(
        ID = id,
        money = initialMoney,
        assets = assets,
        averagePrice = currentPrice,
        interestHigh = Random.nextDouble(1.01, 2.0),
        interestLow = Random.nextDouble(0.50, 0.99),
        sellInterest = Random.nextDouble(1.01, 1.5),
        potency = Random.nextDouble(0.1, 0.9)
    )
}

suspend fun makePeople(n : Int) : MutableList<Person> {                      // заполняем массив с пользователями
    val money = initialDistributionOfMoney(n)
    val peopleArray = mutableListOf<Person>()
    for (i in 0 until n) peopleArray.add(makePerson(i, money[i]))
    return peopleArray
}

fun makeBid( personID : Int, price: Int, amount: Int, type: Int, id: Double): Bid {
    return Bid(personID, price, amount, type, id, Random.nextBoolean(), currentTimeMillis())
}

fun isEmpty(bid: Bid): Boolean {
    return bid.amount == 0
}

fun canSell (bid: Bid, cur: Int) : Boolean {
    var res = true
    if ( ((bid.code == 2) and (bid.price <= cur)) or ((bid.code == 3) and (bid.price >= cur)) ) res = false
    return res
}

fun timeCheck(bid: Bid): Boolean{
    return currentTimeMillis() - bid.timeOfCreation <= 400
}

fun hedge(cur: Int, bid: Bid, amount: Int, listOfBids: MutableList<Bid>){
    val id = Random.nextDouble(0.0, 100000.0)
    if(bid.hedgeOrNo){
        listOfBids.add(makeBid(bid.personID, (people[bid.personID].interestHigh * cur).toInt(), amount, BidType.SELL_HIGH.code, id))
        listOfBids.add(makeBid(bid.personID, (people[bid.personID].interestLow * cur).toInt(), amount, BidType.SELL_LOW.code, id))
    }
}


@RequiresApi(Build.VERSION_CODES.N)
suspend fun tradeBuy(bid: Bid){                            // пытаемся закрыть покупку
    if (sells.size == 0){
        buys.add(bid)
        buys.sortByDescending { it.price }
    }
    else {
        val emptyBidList = mutableListOf<Bid>()
        val hedgeList = mutableListOf<Bid>()
        val iterator = sells.iterator()
        while (iterator.hasNext()){
            val sellOffer = iterator.next()
            if (sellOffer.personID == bid.personID || !timeCheck(sellOffer)){
                iterator.remove()
                continue
            } else if (sellOffer.price <= bid.price && canSell(sellOffer, currentPrice) and timeCheck(sellOffer)) {
                currentPrice = sellOffer.price
                runBlocking { addDot() }
                //priceHistory.add(currentPrice) // debug
                val tradedAssetsAmount = min(sellOffer.amount, bid.amount)
                bid.amount -= tradedAssetsAmount
                sellOffer.amount -= tradedAssetsAmount

                people[sellOffer.personID].money += tradedAssetsAmount * currentPrice
                people[bid.personID].averagePrice = (people[bid.personID].averagePrice * people[bid.personID].assets
                        + currentPrice * tradedAssetsAmount) / (people[bid.personID].assets + tradedAssetsAmount)
                people[bid.personID].assets += tradedAssetsAmount
                hedge(currentPrice, bid, tradedAssetsAmount, hedgeList)

                if (isEmpty(sellOffer)) {
                    iterator.remove()
                    if ((sellOffer.code == 2) or (sellOffer.code == 3)) {
                        for (sell in sells) {
                            if (sell.id == sellOffer.id) emptyBidList.add(bid)
                        }
                    }
                }
                if (isEmpty(bid)) break
            }
        }
        for (emptyBid in emptyBidList) sells.remove(emptyBid)
        for (specialBid in hedgeList) sells.add(specialBid)
        sells.sortBy { it.price }

        hedgeList.clear()
        if (bid.amount > 0){
            buys.add(bid)
            buys.sortByDescending { it.price }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun tradeSell(bid: Bid){                  // пытаемся закрыть продажу
    if (buys.size == 0){
        sells.add(bid)
        sells.sortBy { it.price }
    }
    else {
        val iterator = buys.iterator()
        while (iterator.hasNext()){
            val buyOffer = iterator.next()
            if (buyOffer.personID == bid.personID || !timeCheck(buyOffer)){
                iterator.remove()
                continue
            } else if (buyOffer.price >= bid.price && timeCheck(buyOffer)){
                currentPrice = buyOffer.price
                runBlocking { addDot() }
                //priceHistory.add(currentPrice)
                val tradedAssetsAmount = min(buyOffer.amount, bid.amount)
                bid.amount -= tradedAssetsAmount
                buyOffer.amount -= tradedAssetsAmount
                people[bid.personID].money += tradedAssetsAmount * currentPrice
                people[buyOffer.personID].averagePrice = (people[buyOffer.personID].averagePrice * people[buyOffer.personID].assets
                        + currentPrice * tradedAssetsAmount) / (people[buyOffer.personID].assets + tradedAssetsAmount)
                people[buyOffer.personID].assets += tradedAssetsAmount
                //hedge(currentPrice, bid, min(buyOffer.amount, bid.amount), sells)

                if (isEmpty(buyOffer)) iterator.remove()
                if (isEmpty(bid)) break
            }
        }
        if (bid.amount > 0){
            sells.add(bid)
            sells.sortBy { it.price }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun trade(bid : Bid){
    if (bid.code == BidType.BUY.code){
        tradeBuy(bid)
    }
    else if (bid.code == BidType.SELL.code){
        tradeSell(bid)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun decisionBuy(person : Person) {
    val share = Random.nextFloat()  //share for investing
    val investmentAmount = (person.money * share).toInt()
    val buyPrice = (Random.nextDouble(0.6, 1.25) * currentPrice).toInt()
    val amountOfAssets = investmentAmount / buyPrice
    if (amountOfAssets > 0){
        person.money -= amountOfAssets * buyPrice
        val bid = makeBid(person.ID, buyPrice, amountOfAssets, BidType.BUY.code, 0.0)
        trade(bid)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun decisionSell(person : Person) {
    val share = Random.nextFloat()  //share for selling
    val saleableAssets = (person.assets * share).toInt()
    if (saleableAssets > 0){
        val sellPrice = (Random.nextDouble(0.9, 1.25) * currentPrice).toInt()
        if (sellPrice > person.averagePrice * person.sellInterest || (Random.nextDouble() < Random.nextDouble() * Random.nextDouble() * (person.potency * person.potency))){
            person.assets -= saleableAssets
            val bid = makeBid(person.ID, sellPrice, saleableAssets, BidType.SELL.code, 0.0)
            trade(bid)
        }
    }
}

fun randomUsers(n:Int): MutableList<Person> {
    val activeUsers : MutableList<Person> = mutableListOf()
    val count = Random.nextInt(n/2, n-1)
    for (i in 0 until count) activeUsers.add(people[Random.nextInt(0,n)])
    return activeUsers
}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun birzhaMain() {
    people = makePeople(10001)
    while (true){

        if (isRun){
            val index = Random.nextInt(0,10000)
            val person = people[index]
            if (Random.nextDouble() < person.potency) {
                runBlocking { decisionBuy(person) }
            }
            if (Random.nextDouble() < person.potency) {
                runBlocking { decisionSell(person) }
            }
        }


    //for (i in 0 until 15000){
        //priceHistory.add(currentPrice)
        //println(currentPrice)
        /*if(time % 60 == 0){
            sells.clear()
            buys.clear()
        }*/
//        if(sells.size >= 40) sells.clear()
//        if(buys.size >= 40) buys.clear()
    }
//    File("prices.csv").printWriter().use { out ->
//        priceHistory.forEach {
//            out.println(it)
//        }
//    }

}
