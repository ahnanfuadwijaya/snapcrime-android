package id.riverflows.snapcrime.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UploadViewModel: ViewModel() {
    private val _dummyData = MutableLiveData<String>()
    val dummyData: LiveData<String> = _dummyData

    fun getDummyData() = viewModelScope.launch(Dispatchers.IO){
        _dummyData.postValue("Dummy Data")
    }
}