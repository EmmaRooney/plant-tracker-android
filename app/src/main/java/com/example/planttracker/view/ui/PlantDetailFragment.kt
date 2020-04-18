package com.example.planttracker.view.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.planttracker.R
import com.example.planttracker.util.ImageUtil
import com.example.planttracker.viewmodel.PlantViewModel
import com.example.planttracker.viewmodel.PlantViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_confirm_action.*
import kotlinx.android.synthetic.main.dialog_confirm_action.dialog_btn_cancel
import kotlinx.android.synthetic.main.dialog_confirm_action.dialog_btn_ok
import kotlinx.android.synthetic.main.dialog_number_picker.*
import kotlinx.android.synthetic.main.dialog_water_now_or_later.*
import kotlinx.android.synthetic.main.fragment_plant_detail.*
import kotlinx.android.synthetic.main.fragment_plant_detail.btn_cancel
import kotlinx.android.synthetic.main.fragment_plant_detail.btn_save
import kotlinx.android.synthetic.main.fragment_plant_detail.view.*
import java.lang.Exception

class PlantDetailFragment : Fragment(){

    private lateinit var sharedViewModel: PlantViewModel

    private var IN_EDIT_MODE = false

    private val REQUEST_IMAGE_CAPTURE: Int = 1
    private val REQUEST_GALLERY_ACCESS: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProvider(this, PlantViewModelFactory(this.application)).get(PlantViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_plant_detail, container, false)

        layout.btn_delete_plant.setOnClickListener {
            deletePlantButtonClick()
        }

        layout.btn_edit_plant.setOnClickListener{
            editPlantButtonClick()
        }

        layout.btn_water_plant.setOnClickListener {
            waterPlantButtonClick()
        }

        layout.btn_save.setOnClickListener {
            saveChanges()
            disableEditing()
            setPlantDetails()
        }

        layout.btn_cancel.setOnClickListener {
            disableEditing()
            setPlantDetails()
        }

        layout.last_watered.setOnClickListener {
            if (IN_EDIT_MODE) {
                selectDate()
            }
        }

        layout.next_water_days.setOnClickListener {
            if (IN_EDIT_MODE) {
                showNumberPickerDialog(false)
            }
        }

        layout.plant_photo.setOnClickListener {
            if (IN_EDIT_MODE) {
                selectPhotoMenu()
            }
        }

        return layout
    }

    override fun onStart() {
        super.onStart()
        setPlantDetails()
    }

    private fun setPlantDetails() {
        text_plant_nickname.text = sharedViewModel.selectedPlant?.plantNickname
        text_plant_fullname.text = sharedViewModel.selectedPlant?.plantFullName
        last_watered.text = sharedViewModel.selectedPlant?.lastWatered
        next_water_days.text = sharedViewModel.selectedPlant?.nextWater.toString()
        sunlight.text = sharedViewModel.selectedPlant?.sunlightReq
        water.text = sharedViewModel.selectedPlant?.waterReq
        soil.text = sharedViewModel.selectedPlant?.soilReq
        warnings.text = sharedViewModel.selectedPlant?.warnings

        if (!sharedViewModel.selectedPlant?.photoFilepath.isNullOrBlank()) {
            plant_photo.setImageBitmap(ImageUtil.loadPhoto(sharedViewModel.selectedPlant!!.photoFilepath!!))
        }

        if (sharedViewModel.selectedPlant!!.nextWater == 0) {
            highlightNeedsWatering()
        } else {
            unhighlightNeedsWatering()
        }

    }

    private fun unhighlightNeedsWatering() {
        next_water_days.setTextColor(ContextCompat.getColor(this.context!!, R.color.colorTextPrimary))
        btn_delete_plant.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorBackground))
        btn_edit_plant.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorBackground))
        btn_water_plant.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorBackground))
        this.view!!.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorBackground))
    }

    private fun highlightNeedsWatering() {
        next_water_days.setTextColor(ContextCompat.getColor(this.context!!, R.color.colorAccent))
        btn_delete_plant.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorAccentLight))
        btn_edit_plant.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorAccentLight))
        btn_water_plant.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorAccentLight))
        this.view!!.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorAccentLight))
    }

    private fun deletePlantButtonClick() {
        if (sharedViewModel.selectedPlant == null) {
            Snackbar.make(this.view!!, "Couldn't delete plant", Snackbar.LENGTH_SHORT)
            return
        }

        val dialog = Dialog(this.context!!)
        dialog.setContentView(R.layout.dialog_confirm_action)

        val btnOk = dialog.findViewById<Button>(R.id.dialog_btn_ok)
        btnOk.setOnClickListener {
            // Refresh dialog with second round of confirmation text and change btnOk click action
            dialog.text_confirmation.text = getString(R.string.delete_plant_dialog_2)
            btnOk.setOnClickListener {
                sharedViewModel.delete(sharedViewModel.selectedPlant!!)
                dialog.dismiss()
                // return to plant list
                fragmentManager!!.popBackStack()
            }
            dialog.show()
        }

        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btn_cancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun editPlantButtonClick() {
        enableEditing()
    }

    private fun enableEditing() {
        IN_EDIT_MODE = true

        btn_save.visibility = View.VISIBLE
        btn_cancel.visibility = View.VISIBLE
        btn_edit_plant.isEnabled = false
        btn_delete_plant.isEnabled = false
        btn_water_plant.isEnabled = false

        last_watered.setTypeface(null, Typeface.BOLD)
        next_water_days.setTypeface(null, Typeface.BOLD)

        text_plant_nickname.visibility = View.INVISIBLE
        edit_text_plant_nickname.visibility = View.VISIBLE
        edit_text_plant_nickname.setText(text_plant_nickname.text)

        text_plant_fullname.visibility = View.INVISIBLE
        edit_text_plant_fullname.visibility = View.VISIBLE
        edit_text_plant_fullname.setText(text_plant_fullname.text)

        sunlight.visibility = View.INVISIBLE
        edit_sunlight.visibility = View.VISIBLE
        edit_sunlight.setText(sunlight.text)

        water.visibility = View.INVISIBLE
        edit_water.visibility = View.VISIBLE
        edit_water.setText(water.text)

        soil.visibility = View.INVISIBLE
        edit_soil.visibility = View.VISIBLE
        edit_soil.setText(soil.text)

        warnings.visibility = View.INVISIBLE
        edit_warnings.visibility = View.VISIBLE
        edit_warnings.setText(warnings.text)
    }

    private fun disableEditing() {
        IN_EDIT_MODE = false

        edit_text_plant_nickname.visibility = View.GONE
        text_plant_nickname.visibility = View.VISIBLE

        edit_text_plant_fullname.visibility = View.GONE
        text_plant_fullname.visibility = View.VISIBLE

        edit_sunlight.visibility = View.GONE
        sunlight.visibility = View.VISIBLE

        edit_water.visibility = View.GONE
        water.visibility = View.VISIBLE

        edit_soil.visibility = View.GONE
        soil.visibility = View.VISIBLE

        edit_warnings.visibility = View.GONE
        warnings.visibility = View.VISIBLE

        last_watered.setTypeface(null, Typeface.NORMAL)
        next_water_days.setTypeface(null, Typeface.NORMAL)

        btn_save.visibility = View.GONE
        btn_cancel.visibility = View.GONE
        btn_edit_plant.isEnabled = true
        btn_delete_plant.isEnabled = true
        btn_water_plant.isEnabled = true
    }

    private fun saveChanges() {
        sharedViewModel.updatePlant(
            sharedViewModel.selectedPlant!!.id,
            edit_text_plant_nickname.text.toString(),
            edit_text_plant_fullname.text.toString(),
            sharedViewModel.selectedPlant!!.photoFilepath,
            last_watered.text.toString(),
            next_water_days.text.toString().toInt(),
            edit_sunlight.text.toString(),
            edit_water.text.toString(),
            edit_soil.text.toString(),
            edit_warnings.text.toString()
            )
    }

    private fun selectDate() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this.context!!, DatePickerDialog.OnDateSetListener {
                _, yearNum, monthOfYear, dayOfMonth ->
            last_watered.text = "%d/%d/%d".format(dayOfMonth,monthOfYear+1,yearNum)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showNumberPickerDialog(saveChanges: Boolean) {
        val dialog = Dialog(this.context!!)
        dialog.setContentView(R.layout.dialog_number_picker)

        dialog.numberPicker.minValue = 0
        dialog.numberPicker.maxValue = 1000
        dialog.numberPicker.wrapSelectorWheel = false

        dialog.dialog_btn_ok.setOnClickListener {
            if (saveChanges) {
                sharedViewModel.updateSelectedPlantNextWater(dialog.numberPicker.value)
                setPlantDetails()
            } else {
                next_water_days.text = dialog.numberPicker.value.toString()
            }

            dialog.dismiss()
        }

        dialog.dialog_btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun waterPlantButtonClick() {
        val dialog = Dialog(this.context!!)
        dialog.setContentView(R.layout.dialog_water_now_or_later)

        dialog.btn_water_now.setOnClickListener {
            sharedViewModel.updateSelectedPlantLastWater()
            dialog.dismiss()
            showNumberPickerDialog(true)
        }

        dialog.btn_water_later.setOnClickListener {
            dialog.dismiss()
            showNumberPickerDialog(true)
        }

        dialog.show()

    }

    private fun selectPhotoMenu() {
        val popup = PopupMenu(this.context, plant_photo)
        popup.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_take_new ->
                    dispatchTakePictureIntent()
                R.id.action_select_existing ->
                    dispatchBrowseGalleryIntent()
                else -> false

            }
        }
        popup.menuInflater.inflate(R.menu.menu_select_photo_source, popup.menu)
        popup.show()
    }

    /**
     * Launch new activity using device camera app to take new photo.
     *
     * This function is located here rather than in a view model as must be called from an activity
     */
    private fun dispatchTakePictureIntent(): Boolean {
        // Can only take photo if device has a camera
        if (activity!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
            return true
        }
        return false
    }

    private fun dispatchBrowseGalleryIntent() : Boolean {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), REQUEST_GALLERY_ACCESS)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras!!.get("data") as Bitmap
            val file = ImageUtil.createImageFile(this.context!!)
            ImageUtil.savePhoto(imageBitmap, file)

            plant_photo.setImageBitmap(imageBitmap)
        }
        else if (requestCode == REQUEST_GALLERY_ACCESS && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageBitmap : Bitmap
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.activity!!.contentResolver, data.data!!))
                }
                else {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver, data.data)
                }

                val file = ImageUtil.createImageFile(this.context!!)
                ImageUtil.savePhoto(imageBitmap, file)

                plant_photo.setImageBitmap(imageBitmap)
            }
        }
    }

}