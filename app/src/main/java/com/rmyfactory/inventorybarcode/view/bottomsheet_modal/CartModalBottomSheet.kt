package com.rmyfactory.inventorybarcode.view.bottomsheet_modal

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rmyfactory.inventorybarcode.databinding.MbsCartBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder2
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.util.toCHDomainSingleUnit
import com.rmyfactory.inventorybarcode.view.adapter.MbsCartAdapter
import com.rmyfactory.inventorybarcode.view.fragment.CartFragment

class CartModalBottomSheet(private val fragmentParent: Fragment): BottomSheetDialogFragment() {

    private var _binding: MbsCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var mbsCartAdapter: MbsCartAdapter

    private var productsOnCart = mutableMapOf<String, Map<String, Boolean>>()
    private var product: ProductWithUnits? = null

    private var onUnitClickListener: OnUnitClickListener? = null

    companion object {
        val TAG = CartModalBottomSheet::class.java.toString()
    }

    fun submitData(product: ProductWithUnits, existingProducts: Map<String, Map<String, Boolean>> = mutableMapOf()) {
        this.product = product
        this.productsOnCart.clear()
        this.productsOnCart.putAll(existingProducts)
    }

    fun onUnitClickListener(listener: OnUnitClickListener) {
        this.onUnitClickListener = listener
    }

    interface OnUnitClickListener {
        fun onUnitClick(cartHolders: List<CartHolder2>)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MbsCartBinding.inflate(inflater, container, false)
        (fragmentParent as CartFragment).unbindCameraUseCase()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mbsCartAdapter = MbsCartAdapter { productUnit ->
            var productSingleUnits: ProductWithUnits?
            product?.let { product ->
                productSingleUnits = ProductWithUnits(
                    product = ProductModel(
                        productId = product.product.productId,
                        productName = product.product.productName,
                        productNote = product.product.productNote
                    ),
                    productUnitList = listOf(productUnit)
                )
                onUnitClickListener?.onUnitClick(listOf(productSingleUnits!!.toCHDomainSingleUnit()))
                dismiss()
            }

        }

        binding.rvCartMbs.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = mbsCartAdapter
        }

        binding.btnCancelCartMbs.setOnClickListener {
            this.dismiss()
        }

        product?.let {
            mbsCartAdapter.submitToCartMbs(it, productsOnCart)
        }

    }

    override fun dismiss() {
        super.dismiss()
        (fragmentParent as CartFragment).apply {
            modalBottomSheet = null
            bindCameraUseCases()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}