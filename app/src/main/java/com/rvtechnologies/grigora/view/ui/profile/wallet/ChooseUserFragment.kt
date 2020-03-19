package com.rvtechnologies.grigora.view.ui.profile.wallet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.SearchUserModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.UsersAdapter
import com.rvtechnologies.grigora.view_model.SharedGiftViewModel
import kotlinx.android.synthetic.main.choose_user_fragment.*
import kotlinx.android.synthetic.main.gift_fragment.*

class ChooseUserFragment : Fragment(), IRecyclerItemClick {

    companion object {
        fun newInstance() = ChooseUserFragment()
    }

    var users = ArrayList<SearchUserModel>()

    private lateinit var viewModel: SharedGiftViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            activity!!.let { ViewModelProviders.of(it).get(SharedGiftViewModel::class.java) }


        viewModel.searchResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    users.clear()
                    users.add(response.data as SearchUserModel)
                    rc_users.adapter = UsersAdapter(users, this)
                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else if(response!=null) {
                CommonUtils.showMessage(parent, response.toString())
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.choose_user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ed_search.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (p0.toString().length > 2) {
//                    viewModel?.getUsers(
//                        CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
//                        ed_search.text.toString()
//                    )
//                }
//            }
//        })

        img_search.setOnClickListener {
            viewModel?.getUsers(
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                ed_search.text.toString()
            )
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.grigora_gift_card))
    }

    override fun onItemClick(item: Any) {
        if (item is SearchUserModel) {
            viewModel.selectedUser.value = item
            view?.findNavController()?.popBackStack()
        }
    }

}
