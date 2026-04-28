package com.stupidtree.hitax.ui.style

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.DialogColorPalettePickerBinding
import com.stupidtree.hitax.utils.ColorPalette
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class PopUpColorPalettePicker :
    TransparentBottomSheetDialog<DialogColorPalettePickerBinding>() {

    interface OnPaletteSelectedListener {
        fun onSelected(palette: ColorPalette.Palette)
    }

    private var onSelectedListener: OnPaletteSelectedListener? = null
    private var activePaletteId: String = "material"

    fun setOnPaletteSelectedListener(l: OnPaletteSelectedListener): PopUpColorPalettePicker {
        onSelectedListener = l
        return this
    }

    fun setActivePaletteId(id: String): PopUpColorPalettePicker {
        activePaletteId = id
        return this
    }

    override fun getLayoutId(): Int = R.layout.dialog_color_palette_picker

    override fun initViewBinding(v: View): DialogColorPalettePickerBinding =
        DialogColorPalettePickerBinding.bind(v)

    override fun initViews(v: View) {
        binding.paletteGrid.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.paletteGrid.adapter = PaletteAdapter(
            ColorPalette.all,
            activePaletteId
        ) { palette ->
            onSelectedListener?.onSelected(palette)
            dismiss()
        }
    }

    private class PaletteAdapter(
        private val palettes: List<ColorPalette.Palette>,
        private val activeId: String,
        private val onClick: (ColorPalette.Palette) -> Unit
    ) : RecyclerView.Adapter<PaletteAdapter.ViewHolder>() {

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val nameText: TextView = v.findViewById(R.id.palette_name)
            val colorsLayout: LinearLayout = v.findViewById(R.id.colors_row)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.element_palette_grid_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val palette = palettes[position]
            holder.nameText.text = holder.itemView.context.getString(palette.displayNameRes)
            if (palette.name == activeId) {
                holder.nameText.text = "${holder.nameText.text} ●"
            }
            holder.colorsLayout.removeAllViews()
            val size = dpToPx(20, holder.itemView.context)
            for (hex in palette.colors) {
                val circle = ImageView(holder.itemView.context)
                val params = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(dpToPx(2, holder.itemView.context), 0, dpToPx(2, holder.itemView.context), 0)
                }
                circle.layoutParams = params
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor(hex))
                }
                circle.setImageDrawable(drawable)
                holder.colorsLayout.addView(circle)
            }
            holder.itemView.setOnClickListener { onClick(palette) }
        }

        override fun getItemCount(): Int = palettes.size

        private fun dpToPx(dp: Int, context: android.content.Context): Int =
            (dp * context.resources.displayMetrics.density).toInt()
    }
}
