package com.stupidtree.hitax.ui.main.timetable.panel

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.view.HapticFeedbackConstants
import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.repository.ColorPaletteRepository
import com.stupidtree.hitax.databinding.FragmentTimetablePanelBinding
import com.stupidtree.hitax.ui.style.PopUpColorPalettePicker
import com.stupidtree.hitax.ui.style.PopUpThemeColorPicker
import com.stupidtree.hitax.utils.ColorPalette
import com.stupidtree.style.widgets.TransparentModeledBottomSheetDialog

class FragmentTimetablePanel : TransparentModeledBottomSheetDialog<TimetablePanelViewModel, FragmentTimetablePanelBinding>() {


    override fun initViews(view: View) {
        bindLiveData()
        binding?.reset?.setOnClickListener {
            viewModel.startResetColor()
        }
        binding?.from?.setOnClickListener {
            viewModel.startDateLiveData.value?.let {
                val minuteValue = it % 100
                val hour = it / 100
                TimePickerDialog(requireContext(), { view, hourOfDay, minute ->
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.changeStartDate(hourOfDay, minute)
                }, hour, minuteValue, true)
                        .show()
            }
        }
        binding?.drawbglines?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDrawBGLines(isChecked)
        }
        binding?.colorEnable?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setColorEnable(isChecked)
        }
        binding?.fadeEnable?.setOnCheckedChangeListener{_,isChecked->
            viewModel.setFadeEnable(isChecked)
        }
        binding?.periodLabel?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPeriodLabelEnabled(isChecked)
        }
        binding?.autoReimport?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoReimportEnabled(isChecked)
            if (isChecked) {
                viewModel.triggerAutoReimportNow()
            }
        }
        binding?.themeColorPreview?.setOnClickListener {
            PopUpThemeColorPicker().show(childFragmentManager, "pickTheme")
        }
        binding?.activePaletteName?.setOnClickListener {
            val activeId = ColorPaletteRepository
                .getInstance(requireActivity().application)
                .getActivePaletteId()
            PopUpColorPalettePicker()
                .setActivePaletteId(activeId)
                .setOnPaletteSelectedListener(object :
                    PopUpColorPalettePicker.OnPaletteSelectedListener {
                    override fun onSelected(palette: ColorPalette.Palette) {
                        viewModel.applyPalette(palette)
                        binding?.activePaletteName?.text =
                            getString(palette.displayNameRes)
                    }
                })
                .show(childFragmentManager, "pickPalette")
        }

    }

    @SuppressLint("SetTextI18n")
    fun bindLiveData() {
        viewModel.drawBGLinesLiveData.observe(this) {
            binding?.drawbglines?.isChecked = it
        }
        viewModel.startDateLiveData.observe(this) {
            binding?.from?.text = TimeInDay(it/100,it%100).toString()
        }
        viewModel.colorEnableLiveData.observe(this) {
            binding?.colorEnable?.isChecked = it
        }
        viewModel.fadeEnableLiveData.observe(this) {
            binding?.fadeEnable?.isChecked = it
        }
        viewModel.periodLabelLiveData.observe(this) {
            binding?.periodLabel?.isChecked = it
        }
        viewModel.autoReimportLiveData.observe(this) {
            binding?.autoReimport?.isChecked = it
        }
        binding?.activePaletteName?.text = viewModel.getActivePaletteName()
        binding?.themeColorPreview?.text = viewModel.getActiveThemeName()
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_timetable_panel
    }

    override fun getViewModelClass(): Class<TimetablePanelViewModel> {
        return TimetablePanelViewModel::class.java
    }

    override fun initViewBinding(v: View): FragmentTimetablePanelBinding {
        return FragmentTimetablePanelBinding.bind(v)
    }
}
