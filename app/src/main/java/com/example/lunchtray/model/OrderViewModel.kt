package com.example.lunchtray.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lunchtray.data.DataSource
import java.text.NumberFormat

class OrderViewModel : ViewModel() {

    // Map of menu items
    val menuItems = DataSource.menuItems

    // Default values for item prices
    private var previousEntreePrice = 0.0
    private var previousSidePrice = 0.0
    private var previousAccompanimentPrice = 0.0

    // Default tax rate
    private val taxRate = 0.08

    // Entree for the order
    private val _entree = MutableLiveData<MenuItem?>()
    val entree: LiveData<MenuItem?> = _entree
    val entreePrice: LiveData<String> = Transformations.map(_entree) {
        NumberFormat.getCurrencyInstance().format(it?.price ?: 0.0)
    }

    // Side for the order
    private val _side = MutableLiveData<MenuItem?>()
    val side: LiveData<MenuItem?> = _side
    val sidePrice: LiveData<String> = Transformations.map(_side) {
        NumberFormat.getCurrencyInstance().format(it?.price ?: 0.0)
    }

    // Accompaniment for the order.
    private val _accompaniment = MutableLiveData<MenuItem?>()
    val accompaniment: LiveData<MenuItem?> = _accompaniment
    val accompanimentPrice: LiveData<String> = Transformations.map(_accompaniment) {
        NumberFormat.getCurrencyInstance().format(it?.price ?: 0.0)
    }

    // Subtotal for the order
    private val _subtotal = MutableLiveData<Double>()
    val subtotal: LiveData<String> = Transformations.map(_subtotal) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Total cost of the order
    private val _total = MutableLiveData<Double>()
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Tax for the order
    private val _tax = MutableLiveData<Double>()
    val tax: LiveData<String> = Transformations.map(_tax) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    init {
        resetOrder()
    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetOrder() {
        _entree.value = null
        _side.value = null
        _accompaniment.value = null
        _subtotal.value = 0.0
        _total.value = 0.0
        _tax.value = taxRate
    }

    /**
     * Set the entree for the order.
     */
    fun setEntree(entreeName: String) {
        if(_entree.value != null) {
            previousEntreePrice = entree.value!!.price
        }

        if(_subtotal.value != null) {
            _subtotal.value = _subtotal.value!! - previousEntreePrice
        }

        _entree.value = menuItems[entreeName]
        updateSubtotal(_entree.value!!.price)
    }

    /**
     * Set the side for the order.
     */
    fun setSide(sideName: String) {
        if(_side.value != null) {
            previousSidePrice = side.value!!.price
        }

        if(_subtotal.value != null) {
            _subtotal.value = _subtotal.value!! - previousSidePrice
        }

        _side.value = menuItems[sideName]
        updateSubtotal(_side.value!!.price)
    }

    /**
     * Set the accompaniment for the order.
     */
    fun setAccompaniment(accompanimentName: String) {
        if(_accompaniment.value != null) {
            previousAccompanimentPrice = accompaniment.value!!.price
        }

        if(_subtotal.value != null) {
            _subtotal.value = _subtotal.value!! - previousAccompanimentPrice
        }

        _accompaniment.value = menuItems[accompanimentName]
        updateSubtotal(_accompaniment.value!!.price)
    }

    /**
     * Update subtotal value.
     */
    private fun updateSubtotal(itemPrice: Double) {
        if(_subtotal.value != null) {
            _subtotal.value = _subtotal.value!! + itemPrice
        } else {
            _subtotal.value = itemPrice
        }

        calculateTaxAndTotal()
    }

    /**
     * Calculate tax and update total.
     */
    fun calculateTaxAndTotal() {
        _tax.value = _subtotal.value!! * taxRate
        _total.value = _subtotal.value!! + _tax.value!!
    }
}
