package com.example.planttracker.view.ui

import android.app.Dialog
import android.content.Context
import com.example.planttracker.R
import kotlinx.android.synthetic.main.dialog_number_picker.*


class NumberPickerDialog(context: Context) : Dialog(context) {

    private var onOkButtonClickListener : OnOkButtonClickListener

    init {
        onOkButtonClickListener = context as OnOkButtonClickListener

        this.setContentView(R.layout.dialog_number_picker)

        this.numberPicker.minValue = 0
        this.numberPicker.maxValue = 1000
        this.numberPicker.wrapSelectorWheel = false

        this.dialog_btn_ok.setOnClickListener {
            onOkButtonClickListener.onOkButtonClick(numberPicker.value)
            dismiss()
        }

        this.dialog_btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    interface OnOkButtonClickListener {
        fun onOkButtonClick(selectedValue: Int)
    }

}