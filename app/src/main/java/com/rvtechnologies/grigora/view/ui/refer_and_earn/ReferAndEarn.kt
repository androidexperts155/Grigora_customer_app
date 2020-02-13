package com.rvtechnologies.grigora.view.ui.refer_and_earn

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ReferAndEarnFragmentBinding
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ReferAndEarnViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch.BranchLinkCreateListener
import io.branch.referral.BranchError
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.refer_and_earn_fragment.*
import java.util.*


class ReferAndEarn : Fragment() {

    companion object {
        fun newInstance() = ReferAndEarn()
    }

    private lateinit var viewModel: ReferAndEarnViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReferAndEarnViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val referAndEarnFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.refer_and_earn_fragment,
            container,
            false
        ) as ReferAndEarnFragmentBinding
        referAndEarnFragmentBinding.referAndEarnView = this
        referAndEarnFragmentBinding.referAndEarnViewViewModel = viewModel

        return referAndEarnFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buo = BranchUniversalObject()
            .setCanonicalIdentifier("content/12345")
            .setTitle("My Content Title")
            .setContentDescription("My Content Description")
            .setContentImageUrl("https://lorempixel.com/400/400")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("key1", "value1"))


        val lp = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .setCampaign("content 123 launch")
            .setStage("new user")
            .addControlParameter("url   ", "http://example.com/home")
            .addControlParameter("custom", "data")
            .addControlParameter("custom_random", Calendar.getInstance().timeInMillis.toString())

        buo.generateShortUrl(context!!, lp, object : BranchLinkCreateListener {
            override fun onLinkCreate(url: String?, error: BranchError?) {
                if (error == null) {
                    tv_link.text = url
                } else {
//                    tv_url.text = error.message
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.refer_and_earn))
        (activity as MainActivity).lockDrawer(true)
    }

    fun fbClick() {
        val content = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse(tv_link.text.toString()))
            .build()
        var shareDialog = ShareDialog(this)
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC)
    }

    fun instaClick() {

    }

    fun twitterClick() {

    }

    fun googleClick() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            tv_link.text.toString()
        )
        filterByPackageName(context!!, intent, "com.google.android.apps.plus")
        context!!.startActivity(intent)
    }

    fun copyCode() {
        var clipBoardManager =
            context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        var clip = ClipData.newPlainText("link", tv_link.text.toString())
        clipBoardManager.setPrimaryClip(clip)

        CommonUtils.showMessage(parent, getString(R.string.link_copied))

    }

    fun filterByPackageName(
        context: Context,
        intent: Intent,
        prefix: String?
    ) {
        val matches: List<ResolveInfo> =
            context.getPackageManager().queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(prefix!!)) {
                intent.setPackage(info.activityInfo.packageName)
                return
            }
        }
    }

}
