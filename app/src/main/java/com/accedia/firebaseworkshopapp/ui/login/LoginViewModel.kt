package com.accedia.firebaseworkshopapp.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

import com.accedia.firebaseworkshopapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, activity: Activity) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    alreadyLogin()
                } else {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
            }
    }

    fun alreadyLogin() {
        auth = Firebase.auth
        var email:String? = null
        val user = auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            email = user.email
        }
        if (email != null) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = email!!))
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
