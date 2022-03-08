package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderLogViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun readOrderWithItems() = repository.readOrderWithItems()

}