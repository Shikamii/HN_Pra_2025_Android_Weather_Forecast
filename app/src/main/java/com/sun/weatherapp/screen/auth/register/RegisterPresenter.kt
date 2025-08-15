package com.sun.weatherapp.screen.auth.register

import com.sun.weatherapp.data.reposiroty.AuthRepository
import com.sun.weatherapp.screen.base.BasePresenter

class RegisterPresenter(
    private val authRepository: AuthRepository
) : BasePresenter<RegisterContract.View>(), RegisterContract.Presenter {

    override fun register(username: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            getView()?.showError("Passwords do not match.")
        }
        else if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            getView()?.showError("Fields cannot be empty.")
        }
        else {
            getView()?.showLoading()
            authRepository.signUp(
                email = username,
                password = password,
                onSuccess = {
                    getView()?.hideLoading()
                    getView()?.showRegisterSuccess()
                },
                onFailure = { exception ->
                    getView()?.hideLoading()
                    getView()?.showError(exception.message ?: "Registration failed.")
                }
            )
        }
    }

}
