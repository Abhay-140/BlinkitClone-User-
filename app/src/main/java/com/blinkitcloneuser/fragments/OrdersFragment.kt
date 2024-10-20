package com.blinkitcloneuser.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blinkitcloneuser.R
import com.blinkitcloneuser.utils.Utils
import com.blinkitcloneuser.adapters.AdapterOrders
import com.blinkitcloneuser.databinding.FragmentOrdersBinding
import com.blinkitcloneuser.models.OrderedItems
import com.blinkitcloneuser.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(layoutInflater)

        Utils.setStatusBarColor(requireContext(), R.color.orange, requireActivity())
        onBackButtonClicked()
        getAllOrders()

        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect{ orderList ->
                if(orderList.isNotEmpty()){
                    val orderedList = ArrayList<OrderedItems>()
                    for(orders in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0

                        for (products in orders.orderList!!) {
                            val price = products.productPrice?.substring(1)?.toInt()
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productCategory}, ")
                        }
                        val orderedItems = OrderedItems(
                            orders.orderId, orders.orderData, orders.orderStatus, title.toString(), totalPrice
                        )
                        orderedList.add(orderedItems)
                    }
                    adapterOrders = AdapterOrders(requireContext() , ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

    fun onOrderItemViewClicked(orderedItems: OrderedItems) {
        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)

        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailFragment, bundle)
    }

    private fun onBackButtonClicked() {
        binding.tbProfileFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_ordersFragment_to_profileFragment)
        }
    }

}