package com.blinkitcloneuser.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.blinkitcloneuser.databinding.FragmentSplashScreenBinding
import com.blinkitcloneuser.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SplashScreenFragment : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater)

        Utils.setStatusBarColor(requireContext(), R.color.yellow, requireActivity())

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isCurrentUser.collect{
                    if (it) {
                        startActivity(
                            Intent(
                                requireActivity(),
                                UsersMainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_splashScreenFragment_to_signInFragment)
                    }
                }
            }
        }, 3000)

        return binding.root
    }



}