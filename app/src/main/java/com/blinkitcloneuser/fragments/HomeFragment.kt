package com.blinkitcloneuser.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blinkitcloneuser.utils.CartListener
import com.blinkitcloneuser.utils.Constant
import com.blinkitcloneuser.R
import com.blinkitcloneuser.utils.Utils
import com.blinkitcloneuser.adapters.AdapterBestseller
import com.blinkitcloneuser.adapters.AdapterCategory
import com.blinkitcloneuser.adapters.AdapterProduct
import com.blinkitcloneuser.databinding.BsSeeAllBinding
import com.blinkitcloneuser.databinding.FragmentHomeBinding
import com.blinkitcloneuser.databinding.ItemViewProductBinding
import com.blinkitcloneuser.models.Bestseller
import com.blinkitcloneuser.models.Category
import com.blinkitcloneuser.models.Product
import com.blinkitcloneuser.roomdb.CartProducts
import com.blinkitcloneuser.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterBestseller: AdapterBestseller
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        Utils.setStatusBarColor(requireContext(), R.color.orange, requireActivity())
        setAllCategories()
        navigatingToSearchFragment()
        onProfileClicked()
        fetchBestseller()

        return binding.root
    }

    private fun fetchBestseller() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchProductTypes().collect{
                adapterBestseller = AdapterBestseller(::onSeeAllButtonClicked)
                binding.rvBestsellers.adapter = adapterBestseller
                adapterBestseller.differ.submitList(it)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun onProfileClicked() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }


    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constant.allProductsCategoryIcon.size) {
            categoryList.add(
                Category(
                    Constant.allProductsCategory[i],
                    Constant.allProductsCategoryIcon[i]
                )
            )
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)
    }

    private fun onCategoryIconClicked(category: Category) {
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

    private fun onSeeAllButtonClicked(productType : Bestseller) {
        val bsSeeAllBinding = BsSeeAllBinding.inflate(LayoutInflater.from(requireContext()))
        val bs = BottomSheetDialog(requireContext())
        bs.setContentView(bsSeeAllBinding.root)


        adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)
        bsSeeAllBinding.rvProducts.adapter = adapterProduct
        adapterProduct.differ.submitList(productType.products)
        bs.show()
    }

    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        // Step 1.
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        // Step 2.
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCount)
        }
    }

    private fun saveProductInRoomDb(product: Product) {

        val cartProduct = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productCount = product.itemCount,
            productPrice = "₹" + "${product.productPrice}",
            productStock = product.productStock,
            productCategory = product.productCategory,
            productImage = product.productImageUris?.get(0)!!,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }

    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        if(product.productStock!! + 1 > itemCountInc) {
            productBinding.tvProductCount.text = itemCountInc.toString()

            cartListener?.showCartLayout(1)

            // Step 2.
            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product, itemCountInc)
            }
        }
        else {
            Utils.showToast(requireContext(), "Can't add more item")
        }
    }

    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        // Step 2.
        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCountDec)
        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        }
        else {
            lifecycleScope.launch { viewModel.deleteCartProduct(product.productRandomId!!) }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }

        cartListener?.showCartLayout(-1)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener) {
            cartListener = context
        }
        else {
            throw ClassCastException("Please implement cart listener")
        }
    }

}