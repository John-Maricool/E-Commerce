package com.maricoolsapps.e_commerce.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.model.User
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

/*
This class is where all the functions involving registration, profile changes etc are done
 */

class ProfileChanges
@Inject constructor(val auth: FirebaseAuth){

    val user = auth.currentUser
    val uid = auth.uid

    fun signInUser(user: User): LiveData<Resource<String>>{
        val userValue = MutableLiveData<Resource<String>>()
        //this is where the user signs up for the first time
        auth.signInWithEmailAndPassword(user.email, user.password).addOnSuccessListener{
           userValue.value = Resource.success("Successful Login")
        }.addOnFailureListener{
            userValue.value = Resource.error("Error",it.message!!)
        }
        return userValue
    }

   fun createNewUser(user: User): LiveData<Resource<String>>{
        val userValue = MutableLiveData<Resource<String>>()
        //this is where the user signs up for the first time
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener{
            userValue.value = Resource.success("Successful Login")
        }.addOnFailureListener{
            userValue.value = Resource.error("Error",it.message!!)
        }
        return userValue
    }

    fun resetPassword(email: String): LiveData<Resource<String>>{
        val valueReset = MutableLiveData<Resource<String>>()
        //this is when the user clicks on forgot password.
        //an email is sent to him
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
            valueReset.value = Resource.success("Email has been sent to $email")
        }.addOnFailureListener{
            valueReset.value = Resource.error("Error, email doesn't exist", it.message!!)
        }
        return valueReset
    }
}