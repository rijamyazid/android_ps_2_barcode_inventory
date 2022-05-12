package com.rmyfactory.inventorybarcode.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.ItemHolderDetailUnitBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductUnitWithUnit
import com.rmyfactory.inventorybarcode.util.CutoutDrawable
import com.rmyfactory.inventorybarcode.util.themeColor

class DetailUnitAdapter(
    private val context: Context,
    private val onUnitRemoved: (isAdd: Boolean, position: Int, itemUnitId: Long) -> Unit
) : RecyclerView.Adapter<DetailUnitAdapter.ViewHolder>() {

    private var unitListSize = 1
    private val spinnerList = mutableListOf<String>()
    private val itemUnitMap = mutableMapOf<Int, Map<String, String>>()
    private val bindingList = mutableMapOf<Int, ItemHolderDetailUnitBinding>()
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHolderDetailUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, onUnitRemoved)
    }

    override fun getItemCount() = unitListSize

    fun addItemUnits(
        unitListSize: Int, updateProductUnit: List<ProductUnitWithUnit> = mutableListOf()
    ) {

        if (updateProductUnit.isNotEmpty()) {
            updateProductUnit.forEachIndexed { index, _ ->
                itemUnitMap[index] = mapOf(
                    "id" to updateProductUnit[index].productUnit.id.toString(),
                    "price" to updateProductUnit[index].productUnit.price,
                    "stock" to updateProductUnit[index].productUnit.stock.toString(),
                    "unit" to updateProductUnit[index].productUnit.unitId,
                    "increment" to updateProductUnit[index].productUnit.increment.toString()
                )
            }
        }

        this.unitListSize = unitListSize

        if (updateProductUnit.isNotEmpty()) {
            notifyItemRangeChanged(0, unitListSize)
        } else {
            notifyItemInserted(unitListSize-1)
        }
    }

    fun addSpinnerList(spinnerList: List<String>) {
        this.spinnerList.clear()
        this.spinnerList.addAll(spinnerList)
        notifyItemRangeChanged(0, unitListSize)
    }

    fun getBindingList(): Map<Int, ItemHolderDetailUnitBinding> = bindingList
    fun getUnitListSize(): Int = unitListSize
    fun refreshSize(size: Int) {
        unitListSize = size
    }

    inner class ViewHolder(private val binding: ItemHolderDetailUnitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, onUnitRemoved: (isAdd: Boolean, position: Int, itemUnitId: Long) -> Unit) {

//            Log.d("RMYFACTORYX", "Posisi bind: $position\n" +
//                    "ItemUnitId: ${itemUnitMap[position]?.get("id")?.toLong() ?: -1}")

            val itemUnitId = itemUnitMap[position]?.get("id")?.toLong() ?: -1

            spinnerAdapter = object: ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerList as List<String>
            )
            {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return super.getView(position, convertView, parent).apply {
                        setPadding(24, paddingTop, paddingEnd, paddingBottom)
                    }
                }
            }
            binding.spinItemUnitRv.adapter = spinnerAdapter
            binding.spinItemUnitRv.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2 == (spinnerList.size-1)) {
                        binding.contSpinnerRv.visibility = View.GONE
                        binding.edtItemUnitRv.isEndIconVisible = true
                        binding.edtItemUnitRv.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

            binding.edtItemUnitRv.setEndIconOnClickListener {
                binding.spinItemUnitRv.setSelection(spinnerAdapter.getPosition(itemUnitMap[position]?.get("unit").toString()))
                binding.edtItemUnitRv.visibility = View.GONE
                binding.contSpinnerRv.visibility = View.VISIBLE
            }

            binding.btnDeleteUnitRv.setOnClickListener {
//                unitListSize -= 1
//                notifyItemRemoved(position)
                onUnitRemoved(true, position, itemUnitId)
                it.visibility = View.GONE
                binding.btnDeletedUnitRv.visibility = View.VISIBLE
            }

            binding.btnDeletedUnitRv.setOnClickListener {
                onUnitRemoved(false, position, itemUnitId)
                it.visibility = View.GONE
                binding.btnDeleteUnitRv.visibility = View.VISIBLE
            }

            val borderUnitDrawable =
                getCutoutDrawable(color = context.themeColor(R.attr.colorPrimary))
            binding.llUnitRv.background = borderUnitDrawable
            binding.tvBorderUnitRv.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                val realLeft = left - binding.llUnitRv.left
                val realTop = top - binding.llUnitRv.top
                val realRight = right - binding.llUnitRv.left
                val realBottom = bottom - binding.llUnitRv.top
                borderUnitDrawable.setCutout(
                    realLeft.toFloat(),
                    realTop.toFloat(),
                    realRight.toFloat(),
                    realBottom.toFloat()
                )
            }

            val borderSpinnerDrawable = getCutoutDrawable(
                strokeWidth = 1F,
                cornerSize = 8F, color = context.themeColor(R.attr.colorPrimary)
            )
            binding.spinItemUnitRv.background = borderSpinnerDrawable
            binding.tvBorderSpinnerUnitRv.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                val realLeft = left - binding.spinItemUnitRv.left
                val realTop = top - binding.spinItemUnitRv.top
                val realRight = right - binding.spinItemUnitRv.left
                val realBottom = bottom - binding.spinItemUnitRv.top
                borderSpinnerDrawable.setCutout(
                    realLeft.toFloat(),
                    realTop.toFloat(),
                    realRight.toFloat(),
                    realBottom.toFloat()
                )
            }

            if(itemUnitMap.containsKey(position)) {
                Log.d("RMYFACTORYX", "Contain key: YES")
                binding.edtItemPriceRv.editText?.setText(itemUnitMap[position]?.get("price").toString())
                binding.edtItemStockRv.editText?.setText(itemUnitMap[position]?.get("stock").toString())
                binding.spinItemUnitRv.setSelection(spinnerAdapter.getPosition(itemUnitMap[position]?.get("unit").toString()))
                binding.edtItemIncrementRv.editText?.setText(itemUnitMap[position]?.get("increment").toString())
            } else {
                Log.d("RMYFACTORYX", "Contain key: NO")
            }

            bindingList[position] = binding
        }

        private fun getRoundedCornerShape(cornerSize: Float) =
            ShapeAppearanceModel.Builder()
                .setAllCorners(RoundedCornerTreatment())
                .setAllCornerSizes(cornerSize)
                .build()

        private fun getCutoutDrawable(
            cornerSize: Float = 16F,
            strokeWidth: Float = 2f,
            color: Int
        ) =
            CutoutDrawable(getRoundedCornerShape(cornerSize)).apply {
                setStroke(strokeWidth, color)
                fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
            }

    }
}