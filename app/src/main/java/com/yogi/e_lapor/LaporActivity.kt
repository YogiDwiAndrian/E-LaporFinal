package com.yogi.e_lapor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_lapor.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class LaporActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var mCurrentPhotoPath: String? = null
    var photoURI: Uri? = null
    private var storage: StorageReference? = null
    private var progressDialog: ProgressDialog? = null
    private var baseMenuPresenter: Bitmap? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    lateinit var Latitude : String
    lateinit var Longitude : String
    private var filepath : StorageReference? = null

    val REQUEST_CODE = 1000
    lateinit var textSpinner: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lapor)


        storage = FirebaseStorage.getInstance().reference
        progressDialog = ProgressDialog(this)


        btn_camera.setOnClickListener {
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            dispatchTakePictureIntent()
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)

            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

        btn_send.setOnClickListener{
            saveData()
        }

        //Melakukan cek permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else{
            buildLocationRequest()
            buildLocationCallBack()

            //membuat FusedProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.spinner_kategori,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_kategori.setAdapter(adapter)
        spinner_kategori.setOnItemSelectedListener(this)
    }

    @Throws(IOException::class)
    fun createImageFile(): File { // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) { // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) { // Error occurred while creating the File...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                    "com.yogi.e_lapor.fileprovider",
                    photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    private fun saveData() {


        val loading = ProgressDialog(this)
        val ref = FirebaseDatabase.getInstance().getReference("data")

        val dataId = ref.push().key
        val datalapor = DataLapor()


        if (et_judul.text.isEmpty()){
            Toast.makeText(this, "Masukkann Judul", Toast.LENGTH_SHORT).show()
        }else if (et_isiLaporan.text.isEmpty()){
            Toast.makeText(this, "Masukkann Isi", Toast.LENGTH_SHORT).show()
        }else{
            loading.setMessage("Uploading Data Lapor...")
            loading.show()
            datalapor.id = dataId.toString()
            datalapor.isi = et_isiLaporan.text.toString()
            datalapor.judul = et_judul.text.toString()
            datalapor.kategori = textSpinner
            datalapor.latitude = Latitude
            datalapor.longitude = Longitude
            datalapor.status = "Pending"
            datalapor.timestamp = SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Date())

            filepath?.putFile(photoURI!!)
            ref.child(dataId.toString()).setValue(datalapor).addOnSuccessListener {
                loading.dismiss()
                Toast.makeText(this, "Upload Successful!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            filepath = storage?.child("Photos")?.child(timeStamp)

            baseMenuPresenter = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
            btn_image.setImageBitmap(baseMenuPresenter)
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1
    }

    private fun buildLocationCallBack(){
        locationCallback = object :LocationCallback(){
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(p0: LocationResult?) {
                val location = p0!!.locations.get(p0.locations.size-1)

                Latitude = location.latitude.toString()
                Longitude = location.longitude.toString()
                //Get Last Location
                text_location_latitude.text = location.latitude.toString()
                text_location_longtitude.text = location.longitude.toString()
            }
        }
    }
    private fun buildLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    override fun onItemSelected(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ) {
        textSpinner = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}