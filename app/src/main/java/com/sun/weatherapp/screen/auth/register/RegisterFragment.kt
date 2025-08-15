package com.sun.weatherapp.screen.auth.register

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sun.weatherapp.R
import com.sun.weatherapp.data.reposiroty.AuthRepository
import com.sun.weatherapp.databinding.FragmentRegisterBinding
import com.sun.weatherapp.screen.base.BaseFragment

class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterPresenter>(),
    RegisterContract.View {

    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(inflater, container, false)
    }

    override fun initializePresenter() {
        presenter = RegisterPresenter(AuthRepository())
    }

    override fun setupViews() {
        presenter?.attachView(this)
    }

    override fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            presenter?.register(username, password, confirmPassword)
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun showRegisterSuccess() {
        Toast.makeText(requireContext(), getString(R.string.register_success), Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun showLoading() {
        if (loadingDialog == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.dialog_loading) // layout custom có ProgressBar
            builder.setCancelable(false)
            loadingDialog = builder.create()
        }
        loadingDialog?.show()
    }

    override fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun showError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.error))
            .setMessage(message)
            .setCancelable(true)
        errorDialog = builder.create()
        errorDialog?.show()
    }

}
