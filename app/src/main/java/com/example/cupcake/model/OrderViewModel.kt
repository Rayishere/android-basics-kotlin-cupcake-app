package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// set the cupcake to $2
private const val PRICE_PER_CUPCAKE = 2.00

// set the pick up date
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

class OrderViewModel: ViewModel() {
    /**
     * ====================
     * All the variables
     * ====================
     */
    // the quantities using livedata
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    // the flavor
    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    //possible date options
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    // the price and using "Transformations.map" to take _price as intake
    // Then convert the price into local currency
    // LiveData Transformation
    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price){
        NumberFormat.getCurrencyInstance().format(it)
    }

    //possible date options
    val dateOptions = getPickupOptions()

    //initialize the class
    init {
        resetOrder()
    }

    /**
     * ===============================
     * All the setters and getters
     * ===============================
     */

    /**
     * setup the quantity that the user ordered
     * @param numberCupcakes <-- orders
     */
    fun setQuantity(numberCupcakes: Int){
        _quantity.value = numberCupcakes
        updatePrice()
    }

    /**
     * Set the flavor of cupcake for this order
     * Only 1 flavor is permitted
     * @param desiredFlavor <-- the cupcake flavor as a string
     */
    fun setFlavor(desiredFlavor:String){
        _flavor.value = desiredFlavor
    }

    /**
     * Set the pickup date for this order
     * @param pickupDate is the date for pickup as a string
     */
    fun setDate(pickupDate: String){
        _date.value = pickupDate
        updatePrice()
    }

    /**
     * Returns TRUE: if a flavor is not selected for the order yet.
     * Returns FALSE: a flavor is selected.
     */
    fun hasNoFlavorSet(): Boolean{
        return _flavor.value.isNullOrEmpty()
    }


    /**
     * Reset order when the user changes its mind
     */
    fun resetOrder(){
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    /**
     * ========================
     * Methods and Functions
     * ========================
     */
    /**
     * A helper method for calculating the price
     */
    private fun updatePrice(){
        // The "quantity.value" could be null
        // using elvis operator "?:" to determine whether it is null (not proceed)
        var calculatedPrice = (quantity.value?:0) * PRICE_PER_CUPCAKE
        // check the user selected the same day pickup
        if (dateOptions[0] == _date.value){
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    // Setup "Local" for different time-zone & country
    private fun getPickupOptions():List<String>{
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // Create a list of dates starting with the current date and the following 3 dates
        repeat(4){
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }

}