package com.blinkitcloneuser.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blinkitcloneuser.R
import com.blinkitcloneuser.adapters.AdapterCartProducts
import com.blinkitcloneuser.databinding.FragmentOrderDetailBinding
import com.blinkitcloneuser.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private val viewModel : UserViewModel by viewModels()
    private lateinit var binding: FragmentOrderDetailBinding
    private var status = 0
    private var orderId = ""
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        getValues()
        settingStatus()
        lifecycleScope.launch { getOrderedProducts() }
        onBackButtonClicked()

        return binding.root
    }

    private suspend fun getOrderedProducts() {
        viewModel.getOrderedProduct(orderId).collect{cartList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartList)
        }
    }

    private fun settingStatus() {
        val blueColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)

        binding.iv1.backgroundTintList = blueColor
        if (status >= 1) {
            binding.iv2.backgroundTintList = blueColor
            binding.view1.backgroundTintList = blueColor
        }
        if (status >= 2) {
            binding.iv3.backgroundTintList = blueColor
            binding.view2.backgroundTintList = blueColor
        }
        if (status >= 3) {
            binding.iv4.backgroundTintList = blueColor
            binding.view3.backgroundTintList = blueColor
        }

    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
    }

    private fun onBackButtonClicked() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_ordersFragment)
        }
    }
}