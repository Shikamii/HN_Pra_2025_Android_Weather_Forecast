package com.sun.weatherapp.screen.auth.login

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sun.weatherapp.R
import com.sun.weatherapp.WeatherApplication
import com.sun.weatherapp.data.helper.PreferenceHelper
import com.sun.weatherapp.data.reposiroty.AuthRepository
import com.sun.weatherapp.databinding.FragmentLoginBinding
import com.sun.weatherapp.screen.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginPresenter>(), LoginContract.View {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    private lateinit var pref: PreferenceHelper

    override fun initializePresenter() {
        presenter = LoginPresenter(AuthRepository())
    }

    override fun setupViews() {
        presenter?.attachView(this)
        pref = WeatherApplication.getInstance().preferenceHelper
    }

    override fun setupListeners() {
        val isRemembered = pref.getBoolean(PreferenceHelper.KEY_REMEMBER_ME, false)
        if( isRemembered) {
            binding.etEmail.setText(pref.getString(PreferenceHelper.KEY_EMAIL, ""))
            binding.etPassword.setText(pref.getString(PreferenceHelper.KEY_PASSWORD, ""))
            binding.cbRememberPassword.isChecked = true
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (binding.cbRememberPassword.isChecked) {
                pref.putBoolean(PreferenceHelper.KEY_REMEMBER_ME, true)
                pref.putString(PreferenceHelper.KEY_EMAIL, username)
                pref.putString(PreferenceHelper.KEY_PASSWORD, password)
            } else {
                pref.putBoolean(PreferenceHelper.KEY_REMEMBER_ME, false)
                pref.remove(PreferenceHelper.KEY_EMAIL)
                pref.remove(PreferenceHelper.KEY_PASSWORD)
            }

            presenter?.login(username, password)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.register_fragment)
        }
    }

    override fun showLoginSuccess() {
        Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.home_fragment)
    }

}
