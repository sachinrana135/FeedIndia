package com.alfanse.feedmycity.ui.donor

import android.content.Intent
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
import com.alfanse.feedmycity.utils.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_update_donor.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_update_donor.rbActive as rbActive1

class UpdateDonorActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: UpdateDonorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_donor)
        title = getString(R.string.update_donor_screen_label)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as FeedMyCityApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).
            get(UpdateDonorViewModel::class.java)
        etDonationInfo.setText(User.donateItems)
        if (User.donorVisibility!!) rbActive1.isChecked = true else rbInActive.isChecked = true
        initListener()
        viewModel.updateDonorLiveData.observe(this, observer)
    }

    private fun initListener() {

        var status = if (User.donorVisibility!!) 1 else 0
        rbActive1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) status = 1
        }
        rbInActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) status = 0
        }

        btnUpdate.setOnClickListener {
            viewModel.updateDonorDetails(
                User.userId!!,
                etDonationInfo.text.toString(),
                status.toString()
            )
        }
    }


    private var observer = Observer<Resource<Any>> {
        when (it.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.txt_save_success),
                    Snackbar.LENGTH_SHORT).show()
                val intent = Intent(this, DonorHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                Snackbar.make(findViewById(android.R.id.content), it.message?:getString(R.string.txt_something_wrong),
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


    companion object {
        private const val TAG = "DonorDetailsActivity"
    }
}
