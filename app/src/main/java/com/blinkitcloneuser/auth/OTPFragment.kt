package com.blinkitcloneuser.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blinkitcloneuser.R
import com.blinkitcloneuser.utils.Utils
import com.blinkitcloneuser.activity.UsersMainActivity
import com.blinkitcloneuser.databinding.FragmentOTPBinding
import com.blinkitcloneuser.models.Users
import com.blinkitcloneuser.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(inflater)

        getUserNumber()
        inputOtp()
        sendOTP()
        onLoginButtonClicked()
        onBackButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing you...")
            val editTexts = arrayOf(
                binding.etOtp1,
                binding.etOtp2,
                binding.etOtp3,
                binding.etOtp4,
                binding.etOtp5,
                binding.etOtp6
            )
            val otp = editTexts.joinToString("") {
                it.text.toString()
            }
            Log.d("otp", otp)
            if (otp.length == editTexts.size) {
                editTexts.forEach {
                    it.text?.clear()
                    it.clearFocus()
                }
                verifyOtp(otp)
            } else {
                Utils.showToast(requireContext(), "Please enter right otp")
            }
        }
    }

    private fun verifyOtp(otp: String) {

        val user = Users(uid = null, userPhoneNumber = userNumber, userAddress = "  ")

        viewModel.signInWithPhoneAuthCredential(otp, user)
        lifecycleScope.launch {
            viewModel.isSignedUpSuccessfully.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Logged In...")
                    startActivity(
                        Intent(
                            requireActivity(),
                            UsersMainActivity::class.java
                        )
                    )
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect { otpSent ->
                    if (otpSent) {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "Otp sent to the number..")
                    }
                }
            }
        }
    }

    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }

    private fun inputOtp() {
        val editTexts = arrayOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.length == 0) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = "+91" + bundle?.getString("number").toString()
        binding.phoneNumber.text = userNumber
    }
}