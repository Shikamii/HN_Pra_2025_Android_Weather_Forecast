package com.sun.weatherapp.screen.auth.login

import com.sun.weatherapp.data.reposiroty.AuthRepository
import com.sun.weatherapp.screen.base.BasePresenter

class LoginPresenter(
    private val authRepository: AuthRepository
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    override fun login(username: String, password: String) {
        if (username.isEmpty() && password.isEmpty()) {
            getView()?.showError("Fields cannot be empty.")
        } else {
            getView()?.showLoading()
            authRepository.signIn(
                email = username,
                password = password,
                onSuccess = {
                    getView()?.hideLoading()
                    getView()?.showLoginSuccess()
                },
                onFailure = { exception ->
                    getView()?.hideLoading()
                    getView()?.showError(exception.message ?: "Login failed.")
                }
            )
        }
    }

}
