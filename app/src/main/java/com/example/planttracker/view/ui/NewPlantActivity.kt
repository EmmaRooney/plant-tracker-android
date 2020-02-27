package com.example.planttracker.view.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.planttracker.R
import com.example.planttracker.util.ImageUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_plant.*
import kotlinx.android.synthetic.main.activity_new_plant.btn_cancel
import kotlinx.android.synthetic.main.activity_new_plant.btn_save

class NewPlantActivity : AppCompatActivity(), NumberPickerDialog.OnOkButtonClickListener  {

    private val REQUEST_IMAGE_CAPTURE: Int = 1
    private val REQUEST_GALLERY_ACCESS: Int = 2

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plant)

        textViewNextWater.setOnClickListener {
            showNumberPickerDialog()
        }

        btn_select_photo.setOnClickListener {
            selectPhotoMenu()
        }

        btn_date.setOnClickListener {
            selectDate()
        }

        btn_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btn_save.setOnClickListener {
            val replyIntent = Intent()
            when {
                editTextNickname.text.toString().isEmpty() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Plant needs a name", Snackbar.LENGTH_SHORT).show()
                }
                textViewValueNextWater.text.toString().isEmpty() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Please specify when to next water the plant", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    val mBundle = Bundle()
                    mBundle.putString("Nickname", editTextNickname.text.toString())
                    mBundle.putString("Fullname", editTextFullname.text.toString())
                    mBundle.putString("LastWater", editTextLastWater.text.toString())
                    mBundle.putString("photoPath", textViewPhotoPath.text.toString())
                    mBundle.putInt("NextWater", textViewValueNextWater.text.toString().toInt())
                    mBundle.putString("Sunlight", editTextSun.text.toString())
                    mBundle.putString("Water", editTextWater.text.toString())
                    mBundle.putString("Soil", editTextSoil.text.toString())
                    mBundle.putString("Warning", editTextWarning.text.toString())
                    replyIntent.putExtras(mBundle)

                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
            }
        }
    }

    private fun selectDate() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                _, yearNum, monthOfYear, dayOfMonth ->
                    editTextLastWater.setText("%d/%d/%d".format(dayOfMonth,monthOfYear+1,yearNum))
                }, year, month, day)

        datePickerDialog.show()
    }

    private fun showNumberPickerDialog() {
        val dialog = NumberPickerDialog(this)
        dialog.show()
    }

    /**
     * Launch a popup menu giving the user the option to take a new photo or select an existing one.
     * The resulting photo will be set as the icon for the new plant.
     */
    private fun selectPhotoMenu() {
        val popup = PopupMenu(this, btn_select_photo)
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
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
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

    override fun onOkButtonClick(selectedValue: Int) {
        textViewValueNextWater.text = selectedValue.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras!!.get("data") as Bitmap
            val file = ImageUtil.createImageFile(this)
            ImageUtil.savePhoto(imageBitmap, file)

            textViewPhotoPath.text = file.path
        }
        else if (requestCode == REQUEST_GALLERY_ACCESS && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageBitmap : Bitmap
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, data.data!!))
                }
                else {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                }

                val file = ImageUtil.createImageFile(this)
                ImageUtil.savePhoto(imageBitmap, file)

                textViewPhotoPath.text = file.path
            }
        }
    }

}