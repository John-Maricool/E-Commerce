package com.maricoolsapps.e_commerce

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.maricoolsapps.e_commerce.data.model.Messages

class CheckMessage(private val docRef: DocumentReference, private val userId: String) :
    LiveData<Boolean>(), EventListener<DocumentSnapshot> {

    private var listenerReg: ListenerRegistration? = null

    override fun onActive() {
        listenerReg = docRef.addSnapshotListener(this)
    }

    override fun onInactive() {
        listenerReg!!.remove()
    }

    override fun onEvent(snap: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (snap != null && snap.exists()) {
            val doc = snap.toObject(Messages::class.java)
            if (doc?.seen == true && doc.receiverId == userId){
                value = true
                Log.d("testTag", doc.toString())
                //  values.add(true)
            } else {
                value = value == true
            }
        }
    }
}