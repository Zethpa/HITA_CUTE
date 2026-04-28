package com.stupidtree.hitax.ui.style

import android.app.Application
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
import com.stupidtree.hitax.HApplication
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.repository.BackgroundRepository
import com.stupidtree.hitax.data.repository.ThemeColorRepository
import com.stupidtree.hitax.databinding.DialogThemeColorPickerBinding
import com.stupidtree.hitax.utils.BackgroundHelper
import com.stupidtree.hitax.utils.DominantColorExtractor
import com.stupidtree.hitax.utils.ThemeColorOption
import com.stupidtree.hitax.utils.ThemeColors
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class PopUpThemeColorPicker :
    TransparentBottomSheetDialog<DialogThemeColorPickerBinding>() {

    private val themeRepo by lazy {
        ThemeColorRepository.getInstance(requireActivity().application as Application)
    }
    private val bgRepo by lazy {
        BackgroundRepository.getInstance(requireActivity().application as Application)
    }

    override fun getLayoutId(): Int = R.layout.dialog_theme_color_picker

    override fun initViewBinding(v: View): DialogThemeColorPickerBinding =
        DialogThemeColorPickerBinding.bind(v)

    override fun initViews(v: View) {
        val activeId = themeRepo.getActiveThemeId()
        binding.themeColorGrid.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.themeColorGrid.adapter = ThemeColorAdapter(
            ThemeColors.all, activeId
        ) { option ->
            themeRepo.setActiveThemeId(option.id)
            themeRepo.setAutoFromBg(false)
            (requireActivity().application as HApplication).syncThemeConfig()
            requireActivity().recreate()
        }

        // Extracted colors from background
        val bgSettings = bgRepo.getSettingsSnapshot()
        val hasBgImage = bgSettings.enabled && bgSettings.imageUri.isNotBlank()

        binding.autoFromBgSwitch.isChecked = themeRepo.isAutoFromBg()
        binding.autoFromBgSwitch.isEnabled = hasBgImage

        binding.autoFromBgSwitch.setOnCheckedChangeListener { _, isChecked ->
            themeRepo.setAutoFromBg(isChecked)
            if (isChecked && hasBgImage) {
                val uri = bgRepo.getImageUri()
                val bitmap = BackgroundHelper.getProcessedBitmap(uri, 0, 300, 300)
                if (bitmap != null) {
                    val extracted = DominantColorExtractor.extract(bitmap)
                    val closest = DominantColorExtractor.findClosestTheme(extracted)
                    themeRepo.setActiveThemeId(closest.id)
                    bitmap.recycle()
                }
            }
            (requireActivity().application as HApplication).syncThemeConfig()
            requireActivity().recreate()
        }

        // Show extracted palette from background
        if (hasBgImage) {
            val uri = bgRepo.getImageUri()
            val bitmap = BackgroundHelper.getProcessedBitmap(uri, 0, 300, 300)
            if (bitmap != null) {
                val palette = DominantColorExtractor.extractPalette(bitmap)
                if (palette.size >= 4) {
                    showExtractedColors(palette.take(4))
                }
                bitmap.recycle()
            }
        }
    }

    private fun showExtractedColors(colors: List<Int>) {
        binding.extractedLabel.visibility = View.VISIBLE
        binding.extractedColorsRow.visibility = View.VISIBLE
        binding.extractedColorsRow.removeAllViews()
        val size = (40 * resources.displayMetrics.density).toInt()
        for (color in colors) {
            val circle = ImageView(requireContext())
            val params = LinearLayout.LayoutParams(size, size).apply {
                setMargins((6 * resources.displayMetrics.density).toInt(), 0,
                    (6 * resources.displayMetrics.density).toInt(), 0)
            }
            circle.layoutParams = params
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
                setStroke(2, Color.parseColor("#33000000"))
            }
            circle.setImageDrawable(drawable)
            circle.setOnClickListener {
                val closest = DominantColorExtractor.findClosestTheme(color)
                themeRepo.setActiveThemeId(closest.id)
                themeRepo.setAutoFromBg(false)
                (requireActivity().application as HApplication).syncThemeConfig()
                requireActivity().recreate()
            }
            binding.extractedColorsRow.addView(circle)
        }
    }

    private class ThemeColorAdapter(
        private val options: List<ThemeColorOption>,
        private val activeId: String,
        private val onClick: (ThemeColorOption) -> Unit
    ) : RecyclerView.Adapter<ThemeColorAdapter.ViewHolder>() {

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val colorSwatch: ImageView = v.findViewById(R.id.color_swatch)
            val nameText: TextView = v.findViewById(R.id.theme_name)
            val checkMark: View = v.findViewById(R.id.check_mark)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.element_theme_color_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val option = options[position]
            holder.nameText.text = holder.itemView.context.getString(option.displayNameRes)
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 10f * holder.itemView.context.resources.displayMetrics.density
                setColor(Color.parseColor(option.primaryColorHex))
            }
            holder.colorSwatch.setImageDrawable(drawable)
            holder.checkMark.visibility = if (option.id == activeId) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener { onClick(option) }
        }

        override fun getItemCount(): Int = options.size
    }
}
