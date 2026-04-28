package com.stupidtree.hitax.ui.style

import android.app.Application
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.repository.BackgroundRepository
import com.stupidtree.hitax.data.repository.BackgroundSettings
import com.stupidtree.hitax.databinding.DialogBackgroundSettingsBinding
import com.stupidtree.hitax.utils.BackgroundHelper
import com.stupidtree.style.widgets.PopUpColorPicker
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class PopUpBackgroundSettings :
    TransparentBottomSheetDialog<DialogBackgroundSettingsBinding>() {

    private val bgRepo by lazy {
        BackgroundRepository.getInstance(requireActivity().application as Application)
    }

    // Local editing state — not written to SP until "Apply"
    private var localSettings: BackgroundSettings = BackgroundSettings()
    private var localMode: String = "image"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val internalUri = BackgroundHelper.copyToInternalStorage(requireContext(), it)
                if (internalUri.isNotBlank()) {
                    localSettings = localSettings.copy(imageUri = internalUri)
                    localMode = "image"
                    updateModeUI()
                    updatePreview(internalUri)
                }
            }
        }

    override fun getLayoutId(): Int = R.layout.dialog_background_settings

    override fun initViewBinding(v: View): DialogBackgroundSettingsBinding =
        DialogBackgroundSettingsBinding.bind(v)

    override fun initViews(v: View) {
        localSettings = bgRepo.getSettingsSnapshot().copy(scopeGlobal = true)
        localMode = if (localSettings.bgColor.isNotBlank()) "color" else "image"

        binding.enableSwitch.isChecked = localSettings.enabled
        binding.transparencySlider.progress = localSettings.transparency
        updateModeUI()
        updateFitModeButtons(localSettings.fitMode)
        updateBlurButtons(localSettings.blurRadius)

        if (localSettings.imageUri.isNotBlank() && localMode == "image") {
            binding.preview.visibility = View.VISIBLE
            updatePreview(localSettings.imageUri)
        }
        if (localSettings.bgColor.isNotBlank() && localMode == "color") {
            binding.colorPreview.visibility = View.VISIBLE
            binding.colorPreview.setBackgroundColor(Color.parseColor(localSettings.bgColor))
        }

        binding.enableSwitch.setOnCheckedChangeListener { _, isChecked ->
            localSettings = localSettings.copy(enabled = isChecked)
            updateControlsEnabled(isChecked)
        }

        binding.modeImage.setOnClickListener {
            localMode = "image"
            updateModeUI()
        }
        binding.modeColor.setOnClickListener {
            localMode = "color"
            updateModeUI()
        }

        binding.pickImage.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.colorPreview.setOnClickListener {
            val initColor = if (localSettings.bgColor.isNotBlank())
                Color.parseColor(localSettings.bgColor) else Color.parseColor("#B0BEC5")
            PopUpColorPicker()
                .initColor(initColor)
                .setOnColorSelectListener(object : PopUpColorPicker.OnColorSelectedListener {
                    override fun onSelected(color: Int) {
                        val hex = String.format("#%06X", 0xFFFFFF and color)
                        localSettings = localSettings.copy(bgColor = hex)
                        binding.colorPreview.setBackgroundColor(color)
                    }
                })
                .show(childFragmentManager, "pickBgColor")
        }

        binding.transparencySlider.setOnSeekBarChangeListener(
            object : android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: android.widget.SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) localSettings = localSettings.copy(transparency = progress)
                }
                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
            }
        )

        binding.blurNone.setOnClickListener {
            localSettings = localSettings.copy(blurRadius = 0)
            updateBlurButtons(0)
        }
        binding.blurLight.setOnClickListener {
            localSettings = localSettings.copy(blurRadius = 4)
            updateBlurButtons(4)
        }
        binding.blurMedium.setOnClickListener {
            localSettings = localSettings.copy(blurRadius = 12)
            updateBlurButtons(12)
        }
        binding.blurHeavy.setOnClickListener {
            localSettings = localSettings.copy(blurRadius = 20)
            updateBlurButtons(20)
        }

        binding.fitCrop.setOnClickListener {
            localSettings = localSettings.copy(fitMode = "crop")
            updateFitModeButtons("crop")
        }
        binding.fitFit.setOnClickListener {
            localSettings = localSettings.copy(fitMode = "fit")
            updateFitModeButtons("fit")
        }
        binding.fitStretch.setOnClickListener {
            localSettings = localSettings.copy(fitMode = "stretch")
            updateFitModeButtons("stretch")
        }

        binding.applyButton.setOnClickListener {
            applyAllSettings()
            Toast.makeText(requireContext(), R.string.background_applied, Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.removeBackground.setOnClickListener {
            bgRepo.setImageUri("")
            bgRepo.setBgColor("")
            bgRepo.setEnabled(false)
            BackgroundHelper.clearCache()
            dismiss()
        }

        updateControlsEnabled(localSettings.enabled)
    }

    private fun updateModeUI() {
        val isImage = localMode == "image"
        highlightButton(binding.modeImage, isImage)
        highlightButton(binding.modeColor, !isImage)
        binding.pickImage.visibility = if (isImage) View.VISIBLE else View.GONE
        binding.preview.visibility = if (isImage && localSettings.imageUri.isNotBlank()) View.VISIBLE else View.GONE
        binding.colorPreview.visibility = if (!isImage) View.VISIBLE else View.GONE
        binding.fitModeLabel.visibility = if (isImage) View.VISIBLE else View.GONE
        binding.fitModeButtons.visibility = if (isImage) View.VISIBLE else View.GONE
        if (!isImage && localSettings.bgColor.isNotBlank()) {
            binding.colorPreview.setBackgroundColor(Color.parseColor(localSettings.bgColor))
        }
    }

    private fun updateControlsEnabled(enabled: Boolean) {
        binding.modeImage.isEnabled = enabled
        binding.modeColor.isEnabled = enabled
        binding.pickImage.isEnabled = enabled
        binding.transparencySlider.isEnabled = enabled
        binding.colorPreview.isEnabled = enabled
        binding.blurNone.isEnabled = enabled
        binding.blurLight.isEnabled = enabled
        binding.blurMedium.isEnabled = enabled
        binding.blurHeavy.isEnabled = enabled
        binding.fitCrop.isEnabled = enabled
        binding.fitFit.isEnabled = enabled
        binding.fitStretch.isEnabled = enabled
    }

    private fun applyAllSettings() {
        if (localMode == "color") {
            bgRepo.setImageUri("")
            bgRepo.setBgColor(localSettings.bgColor)
        } else {
            bgRepo.setBgColor("")
            bgRepo.setImageUri(localSettings.imageUri)
        }
        bgRepo.setEnabled(localSettings.enabled)
        bgRepo.setTransparency(localSettings.transparency)
        bgRepo.setBlurRadius(localSettings.blurRadius)
        bgRepo.setScopeGlobal(localSettings.scopeGlobal)
        bgRepo.setFitMode(localSettings.fitMode)
    }

    private fun highlightButton(btn: MaterialButton, active: Boolean) {
        val primary = requireContext().getColor(R.color.cruel_summer_primary)
        val white = requireContext().getColor(android.R.color.white)
        val transparent = requireContext().getColor(android.R.color.transparent)
        if (active) {
            btn.setBackgroundColor(primary)
            btn.setTextColor(white)
        } else {
            btn.setBackgroundColor(transparent)
            btn.setTextColor(primary)
        }
    }

    private fun updateBlurButtons(radius: Int) {
        val primary = requireContext().getColor(R.color.cruel_summer_primary)
        val white = requireContext().getColor(android.R.color.white)
        val transparent = requireContext().getColor(android.R.color.transparent)
        fun style(btn: MaterialButton, active: Boolean) {
            if (active) {
                btn.setBackgroundColor(primary)
                btn.setTextColor(white)
            } else {
                btn.setBackgroundColor(transparent)
                btn.setTextColor(primary)
            }
        }
        style(binding.blurNone, radius == 0)
        style(binding.blurLight, radius == 4)
        style(binding.blurMedium, radius == 12)
        style(binding.blurHeavy, radius == 20)
    }

    private fun updateFitModeButtons(mode: String) {
        val primary = requireContext().getColor(R.color.cruel_summer_primary)
        val white = requireContext().getColor(android.R.color.white)
        val transparent = requireContext().getColor(android.R.color.transparent)
        fun style(btn: MaterialButton, active: Boolean) {
            if (active) {
                btn.setBackgroundColor(primary)
                btn.setTextColor(white)
            } else {
                btn.setBackgroundColor(transparent)
                btn.setTextColor(primary)
            }
        }
        style(binding.fitCrop, mode == "crop")
        style(binding.fitFit, mode == "fit")
        style(binding.fitStretch, mode == "stretch")
    }

    private fun updateScopeButtons(isGlobal: Boolean) {
        val primary = requireContext().getColor(R.color.cruel_summer_primary)
        val white = requireContext().getColor(android.R.color.white)
        val transparent = requireContext().getColor(android.R.color.transparent)
        if (isGlobal) {
            binding.scopeGlobal.setBackgroundColor(primary)
            binding.scopeGlobal.setTextColor(white)
            binding.scopeTimetable.setBackgroundColor(transparent)
            binding.scopeTimetable.setTextColor(primary)
        } else {
            binding.scopeTimetable.setBackgroundColor(primary)
            binding.scopeTimetable.setTextColor(white)
            binding.scopeGlobal.setBackgroundColor(transparent)
            binding.scopeGlobal.setTextColor(primary)
        }
    }

    private fun updatePreview(uri: String) {
        if (uri.isBlank()) return
        binding.preview.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(uri.removePrefix("file://"))
            .centerCrop()
            .into(binding.preview)
    }
}
