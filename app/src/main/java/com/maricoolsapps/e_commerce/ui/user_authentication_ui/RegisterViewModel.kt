package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.data.repositories.RegisterRepository
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.validateEmail
import com.maricoolsapps.e_commerce.utils.validateTwoPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject constructor(
    private val repo: RegisterRepository,
    private val profileChanges: ProfileChanges,
    val defaultRepo: DefaultRepository,
    @ApplicationContext val app: Application
) :
    AndroidViewModel(app) {

    @Bindable
    private val _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean> get() = _result

    private val _done = MutableLiveData<Boolean>()
    val done: LiveData<Boolean> get() = _done

    @Bindable
    val userEmail = MutableLiveData<String>()
    @Bindable
    val userPassword = MutableLiveData<String>()
    @Bindable
    val userReenterPassword = MutableLiveData<String>()
    @Bindable
    val userNumber = MutableLiveData<String>()
    @Bindable
    val userName = MutableLiveData<String>()
    @Bindable
    val userRegion = MediatorLiveData<String>()
    @Bindable
    val userlocation = MutableLiveData<String>()

    val userRegionId = MutableLiveData<Int>()
    var intent_data: MutableLiveData<Uri?> = MutableLiveData()

    init {
        userRegion.addSource(userRegionId){
            userRegion.value = app.resources.getStringArray(R.array.brands)[it]
        }
    }

    private fun createNewUser(user: User) {
        viewModelScope.launch(IO) {
            repo.createNewUser(user) {
                defaultRepo.onResult(it)
                when (it.status) {
                    Status.SUCCESS -> {
                        _result.postValue(true)
                    }
                    Status.ERROR -> {
                        _result.postValue(false)
                    }
                    Status.LOADING -> {
                        _result.postValue(false)
                    }
                }
            }
        }
    }

    fun gotoMedia(): Intent {
        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        return intent
    }

    private fun completeRegistration(user: String, carBuyerOrSeller: CarBuyerOrSeller) {
        viewModelScope.launch(IO) {
            repo.changeProfile(user, carBuyerOrSeller) {
                defaultRepo.onResult(it)
                when (it.status) {
                    Status.SUCCESS -> {
                        _done.postValue(true)
                    }
                    Status.ERROR -> {
                        _done.postValue(false)
                    }
                    Status.LOADING -> {
                        _done.postValue(false)
                    }
                }
            }
        }
    }

    fun saveToDb() {
        val location = userlocation.value.toString()
        val name = userName.value.toString()
        val email = userEmail.value.toString()
        val number = userNumber.value.toString()
        val region = userRegion.value.toString()

        if (name.isEmpty() || email.isEmpty() ||
            number.isEmpty() || userRegionId.value == 0
        ) {
            defaultRepo._resultData.value = "Error in your entries"
        } else {
            getUser().observeForever {
                if (it != null) {
                    val user = CarBuyerOrSeller(
                        it.uid,
                        intent_data.toString(),
                        name,
                        email,
                        number,
                        region,
                        location
                    )
                    completeRegistration(it.uid, user)
                }
            }
        }
    }

    fun userRegistration() {
        val email: String = userEmail.value.toString()
        val password: String = userPassword.value.toString()
        val secondPassword: String = userReenterPassword.value.toString()

        if (!validateEmail(email) || !validateTwoPasswords(password, secondPassword)) {
            defaultRepo._resultData.value = "Error in your entries"
            return
        }

        val user = User(email, password)
        createNewUser(user)
    }

    fun getUser(): MutableLiveData<FirebaseUser?> {
        return profileChanges.getAuthState()
    }
}