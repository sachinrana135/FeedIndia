package com.alfanse.feedmycity.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.models.UserEntity
import com.alfanse.feedmycity.data.repository.FeedAppRepository
import com.alfanse.feedmycity.data.storage.ApplicationStorage
import com.alfanse.feedmycity.utils.APP_USER_ID_PREFS_KEY
import com.alfanse.feedmycity.utils.BUNDLE_KEY_FIRST_LAUNCH
import com.alfanse.feedmycity.utils.BUNDLE_KEY_GROUP_CODE
import com.alfanse.feedmycity.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SplashViewModel @Inject constructor(
    private val repository: FeedAppRepository,
    private val storage: ApplicationStorage,
    @Named("memory") private val memoryStorage: ApplicationStorage,
    private val utils: Utils
) : ViewModel() {

    val userLiveData = MutableLiveData<Resource<UserEntity>>()

    private val userLiveDataHandler = CoroutineExceptionHandler { _, throwable ->
        userLiveData.value = Resource.error(throwable.message, null)
    }

    fun getUserById(userId: String) {
        userLiveData.value = Resource.loading(null)

        viewModelScope.launch(userLiveDataHandler) {

            repository.getUserById(userId).let { user ->
                // setting logged user in singleton to access anywhere in app
                storage.putString(APP_USER_ID_PREFS_KEY, user.userId)
                utils.setLoggedUser(user)
                userLiveData.value = Resource.success(user)
            }
        }
    }

    fun getLoggedUser() = storage.getString(APP_USER_ID_PREFS_KEY, null)

    fun saveGroupCode(groupCode: String) {
        memoryStorage.putString(BUNDLE_KEY_GROUP_CODE, groupCode)
    }

    fun isFirstLaunch() = storage.getBoolean(BUNDLE_KEY_FIRST_LAUNCH, true)
}