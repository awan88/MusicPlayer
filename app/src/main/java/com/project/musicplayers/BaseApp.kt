package com.project.musicplayers
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BaseApp : MultiDexApplication() {
    var firebaseUser: FirebaseUser? = null

    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)
        FirebaseApp.initializeApp(this)
        Firebase.database.setPersistenceEnabled(true)
        firebaseUser = FirebaseAuth.getInstance().currentUser

    }
}
