package com.alfanse.feedmycity.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.alfanse.feedmycity.data.models.NeedieritemEntity
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NeedierDataSource(
    private val scope: CoroutineScope,
    private val repository: FeedAppRepository,
    private val groupId: String,
    private val status: String
) : PageKeyedDataSource<Int, NeedieritemEntity>() {

    val FIRST_PAGE = 1
    var responseLiveData = MutableLiveData<Resource<List<NeedieritemEntity>>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        responseLiveData.postValue(Resource.error(throwable.message, null))
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, NeedieritemEntity>
    ) {
        responseLiveData.postValue(Resource.loading(null))
        scope.launch (handler){
            val response = repository.getNeediers(
                groupId = groupId,
                status = status,
                page = FIRST_PAGE,
                pageLoad = params.requestedLoadSize
            )
            if(response?.size == 0) {
                responseLiveData.postValue(Resource.empty())
            }else {
                responseLiveData.postValue(Resource.success(response))
            }
            callback.onResult(response, null, 2)
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NeedieritemEntity>
    ) {
        scope.launch(handler) {
            responseLiveData.postValue(Resource.loading(null))
            val response = repository.getNeediers(
                groupId = groupId,
                status = status,
                page = params.key,
                pageLoad = params.requestedLoadSize
            )
            if(response?.size == 0 && params.key == FIRST_PAGE) {
                responseLiveData.postValue(Resource.empty())
            }else {
                responseLiveData.postValue(Resource.success(response))
            }
            callback.onResult(response, params.key + 1)
        }

    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NeedieritemEntity>
    ) {
    }
}