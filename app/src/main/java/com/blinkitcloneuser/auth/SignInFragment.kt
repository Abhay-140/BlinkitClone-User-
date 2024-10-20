package com.blinkitcloneuser.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.blinkitcloneuser.R
import com.blinkitcloneuser.utils.Utils
import com.blinkitcloneuser.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater)

        getUserNumber()
        onContinueButtonClick()
        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener{
            val number = binding.etUserNumber.text.toString()

            if (number.isEmpty() || number.length != 10){
                Utils.showToast(requireContext(), "Please enter valid phone number")
            } else {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }
        }
    }

    private fun getUserNumber() {
        binding.etUserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length
                if (len == 10){
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyish_blue))
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }
}
