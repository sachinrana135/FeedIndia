package com.alfanse.feedmycity.ui.donor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.data.Resource
import com.alfanse.feedmycity.data.Status
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.mobileauth.CodeVerificationActivity
import com.alfanse.feedmycity.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import kotlinx.android.synthetic.main.activity_donor_details.*
import javax.inject.Inject

class DonorDetailsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var donorDetailViewModel: DonorDetailsViewModel
    private var donorLat = 0.0
    private var donorLng = 0.0
    private var phone = ""
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLatLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        title = getString(R.string.donor_details_screen_label)
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as FeedMyCityApplication).appComponent.inject(this)
        donorDetailViewModel = ViewModelProviders.of(this, viewModelFactory).
            get(DonorDetailsViewModel::class.java)
        initListener()
        donorDetailViewModel.saveDonorLiveData.observe(this, observer)
        readPhoneNum()
    }

    private fun readPhoneNum() {
        if (intent != null){
            phone = intent.getStringExtra(CodeVerificationActivity.MOBILE_NUM_KEY)!!
        }
    }

    private fun initListener(){
        var status = 1
        rbActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) status = 1
        }
        rbInActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) status = 0
        }
        etDonorAddress.setOnClickListener {
            requestPermission()
        }

        btnSave.setOnClickListener {
            when {
                etName.text.toString().trim().isEmpty() -> {
                    etName.error = "Enter name"
                    return@setOnClickListener
                }

                etDonationInfo.text.toString().trim().isEmpty() -> {
                    etDonationInfo.error = "Enter your donation"
                    return@setOnClickListener
                }

                etDonorAddress.text.toString().trim().isEmpty() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Please give you location",
                        Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            donorDetailViewModel.saveDonorDetails(etName.text.toString(),
                etDonationInfo.text.toString(), status.toString(),
                donorLat.toString(),
                donorLng.toString(),
                phone,
                etDonorAddress.text.toString())
        }
    }

    private var observer = Observer<Resource<String>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                val intent = Intent(this, DonorHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), it.message!!,
                    Snackbar.LENGTH_SHORT).show()
            }
            Status.EMPTY -> {

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestPermission(){
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.location_permission_not_granted),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setUpLocationListener() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
            if (it != null) {
                currentLatLng = LatLng(it.latitude, it.longitude)

                // start map search screen to find address
                startLocationPicker(currentLatLng!!)
            }
        }
    }

    private fun startLocationPicker(latLng: LatLng){
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(latLng.latitude, latLng.longitude)
            .withSearchZone(INDIA_LOCALE_ZONE)
            .withDefaultLocaleSearchZone()
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            .build(applicationContext)

        startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED){
            if (requestCode == 1){
                if (data != null){
                    donorLat = data.getDoubleExtra(LATITUDE, 0.0)
                    donorLng = data.getDoubleExtra(LONGITUDE, 0.0)
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    if (address != null){
                        setAddressToView(address)
                    }
                }
            }
        }
    }

    private fun setAddressToView(address: String){
        etDonorAddress.setText(address)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val TAG = "DonorDetailsActivity"
        private const val MAP_BUTTON_REQUEST_CODE = 1
        private const val DEFAULT_LAT = 28.6429
        private const val DEFAULT_LNG = 77.2191
        private const val INDIA_LOCALE_ZONE = "en_in"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }
}
