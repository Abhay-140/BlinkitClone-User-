package com.blinkitcloneuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blinkitcloneuser.databinding.ItemViewProductCategoryBinding
import com.blinkitcloneuser.models.Category

class AdapterCategory(
    val categoryList: ArrayList<Category>,
    val onCategoryIconClicked: (Category) -> Unit
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {


    class CategoryViewHolder(
        val binding: ItemViewProductCategoryBinding
    ) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemViewProductCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]

        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }
        holder.itemView.setOnClickListener {
            onCategoryIconClicked(category)
        }
    }

}