package com.rvtechnologies.grigora.view.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.opensooq.supernova.gligar.GligarPicker
//import com.fxn.pix.Options
//import com.fxn.pix.Pix
//import com.fxn.utility.PermUtil
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ProfileDetailsFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.model.models.UserDetails
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ProfileDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.profile_details_fragment.*


class ProfileDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileDetailsFragment()
    }

    private lateinit var viewModel: ProfileDetailsViewModel
    private var profileDetailsFragmentBinding: ProfileDetailsFragmentBinding? = null
    private var listener: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileDetailsViewModel::class.java)
        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        viewModel.userDetailsRes.observe(this, Observer { userDataRes ->
            if (userDataRes is CommonResponseModel<*>) {
                if (userDataRes.status!!) {
                    val userDetails = userDataRes.data as UserDetails
                    viewModel.name.value = userDetails.name?.toString()
                    viewModel.email.value = userDetails.email?.toString()
                    viewModel.phone.value = userDetails.phone?.toString()
                    viewModel.image.value = userDetails.image?.toString()

                    CommonUtils.savePrefs(context, PrefConstants.ID, userDetails.id?.toString())
                    CommonUtils.savePrefs(context, PrefConstants.NAME, userDetails.name?.toString())
                    CommonUtils.savePrefs(
                        context,
                        PrefConstants.IMAGE,
                        userDetails.image?.toString()
                    )
                    CommonUtils.savePrefs(
                        context,
                        PrefConstants.EMAIL,
                        userDetails?.email?.toString()
                    )

                    if (userDetails.email.isNullOrEmpty()) {
                        etEmail.isEnabled = true
                    }

                    profileDetailsFragmentBinding?.profileDetailsViewModel = viewModel
                    profileDetailsFragmentBinding?.profileDetailsView = this
                    etName.addTextChangedListener(listener)
                    etEmail.addTextChangedListener(listener)
                    etPhone.addTextChangedListener(listener)
                    userDataRes.message?.let { CommonUtils.showMessage(parentView, it) }
                } else {
                    userDataRes.message?.let { CommonUtils.showMessage(parentView, it) }
                }
            } else {
                CommonUtils.showMessage(parentView, userDataRes.toString())
            }
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, "") }
            } else {
                CommonUtils.hideLoader()
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileDetailsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_details_fragment,
            container,
            false
        ) as ProfileDetailsFragmentBinding
        profileDetailsFragmentBinding?.profileDetailsViewModel = viewModel
        return profileDetailsFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProfileData()
        listener = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                btnSave.visibility = VISIBLE
            }
        }
    }

    fun chooseProfilePic() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */

                    GligarPicker().requestCode(400).withFragment(this@ProfileDetailsFragment)
                        .limit(1).show()

//                    if (report.areAllPermissionsGranted()) {
//                        allPermissionGranted = true
//                    } else {
//                        allPermissionGranted = false
//// showSettingsDialog()
//                    }
//                    if (report.isAnyPermissionPermanentlyDenied) {
//                        showSettingsDialog()
//                        allPermissionGranted = false;
//                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {/* ... */
                    token.continuePermissionRequest()
//                    showSettingsDialog()
                }
            }).check()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
//        when (requestCode) {
//            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Pix.start(this, Options.init().setRequestCode(AppConstants.SELECT_IMAGE_CODE))
//                } else {
//                    CommonUtils.showMessage(
//                        parentView,
//                        getString(R.string.permission_denial_message)
//                    )
//                }
//                return
//            }
//        }
    }


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {

            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            400 -> {
                val imagesList =
                    data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)// return list of selected images paths.
                if (!imagesList.isNullOrEmpty()) {

                    viewModel.image.value = imagesList[0]
                    profileDetailsFragmentBinding?.profileDetailsViewModel = viewModel
//
                }
            }
        }
    }


}
