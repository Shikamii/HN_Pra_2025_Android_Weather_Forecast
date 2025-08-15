package com.sun.weatherapp.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthService {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
}
