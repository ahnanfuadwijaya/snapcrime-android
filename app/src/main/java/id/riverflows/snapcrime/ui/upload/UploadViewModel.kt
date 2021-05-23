package id.riverflows.snapcrime.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UploadViewModel: ViewModel() {
    private val _uploadResponse = MutableLiveData<String>()
    val uploadResponse: LiveData<String> = _uploadResponse

    fun uploadData() = viewModelScope.launch(Dispatchers.IO){
        _uploadResponse.postValue("Dummy Response")
    }
}