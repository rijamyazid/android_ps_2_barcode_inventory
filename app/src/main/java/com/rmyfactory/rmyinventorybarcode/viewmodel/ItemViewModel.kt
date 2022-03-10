package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun readItemWithUnits(): LiveData<List<ItemWithUnits>> {
        return repository.readItemWithUnits()
    }

}