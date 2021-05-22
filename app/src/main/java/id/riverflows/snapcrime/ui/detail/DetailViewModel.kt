package id.riverflows.snapcrime.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.riverflows.snapcrime.data.DetailCase
import id.riverflows.snapcrime.util.UtilDummyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel: ViewModel() {
    private val _detailCase = MutableLiveData<DetailCase>()
    val detailCase: LiveData<DetailCase> = _detailCase

    fun getDetailCase() = viewModelScope.launch(Dispatchers.IO){
        _detailCase.postValue(UtilDummyData.getDetailCase())
    }
}