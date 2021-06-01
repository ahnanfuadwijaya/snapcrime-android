package id.riverflows.snapcrime.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.riverflows.snapcrime.data.response.DetailCaseResponse
import id.riverflows.snapcrime.util.UtilDummyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel: ViewModel() {
    private val _detailCaseResponse = MutableLiveData<DetailCaseResponse>()
    val detailCaseResponse: LiveData<DetailCaseResponse> = _detailCaseResponse

    fun getDetailCaseResponse(id: Long) = viewModelScope.launch(Dispatchers.IO){
        _detailCaseResponse.postValue(UtilDummyData.getDetailCaseResponse(id))
    }
}