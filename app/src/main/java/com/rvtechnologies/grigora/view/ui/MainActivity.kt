package com.rvtechnologies.grigora.view.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.util.Base64
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.login_signup.GoogleSignin
import com.rvtechnologies.grigora.view.ui.rating.MealsRatingDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RestaurantRatingDialogFragment
import io.branch.referral.Branch
import io.branch.referral.BranchError
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MainActivity : AppCompatActivity(), RateDriverDialogFragment.DriverRate,
    MealsRatingDialogFragment.MealsRate,
    RestaurantRatingDialogFragment.RestaurantRate {
    lateinit var content: RelativeLayout
    lateinit var mainLayoutShadow: RelativeLayout
    var googleSignIn: GoogleSignin? = null

    object branchListener : Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
            if (error == null) {
                Log.i("BRANCH SDK", referringParams.toString())
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                Log.e("BRANCH SDK", error.message)
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        // Branch reinit (in case Activity is already in foreground when Branch link is clicked)
        Branch.getInstance().reInitSession(this, branchListener)
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        Branch.getInstance().initSession(branchListener, this.intent.data, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printHashKey()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
//        }


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );


        GrigoraApp.getInstance().setCurrentActivity(this)
        updateLocale(false)

        setTheme(CommonUtils.getBooleanPrefValue(this, PrefConstants.IS_DARK_MODE))
        setContentView(R.layout.activity_main)


        if (intent.hasExtra("from"))
            if (intent.getStringExtra("type") == "1")
                Navigation.findNavController(
                    this,
                    R.id.main_nav_fragment
                ).navigate(R.id.quizFragment)

        rateOrder()
        setDrawer()

        nav_view.setNavigationItemSelectedListener { item ->
            closeOrOpenDrawer()
            onNavDestinationSelected(
                item,
                Navigation.findNavController(this, R.id.main_nav_fragment)
            )

        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->


            //            Navigation.findNavController(this, R.id.main_nav_fragment).popBackStack(item.itemId,true)


            onNavDestinationSelected(
                item,
                Navigation.findNavController(this, R.id.main_nav_fragment)
            )

        }
        img_back.setOnClickListener {
            onBackPressed()
        }

        deliverLayout.setOnClickListener {
            //            Navigation.findNavController(this, R.id.main_nav_fragment)
//                .popBackStack(R.id.nav_graph_xml, true)

            Navigation.findNavController(this, R.id.main_nav_fragment)
                .navigate(R.id.addressListFragment)
        }

        Log.e("token:: ", CommonUtils.getPrefValue(this, PrefConstants.TOKEN))
        Log.e("id:: ", CommonUtils.getPrefValue(this, PrefConstants.ID))
        Log.e("language:: ", GrigoraApp.getInstance().getCurrentLanguage())
        content = findViewById(R.id.content)
        mainLayoutShadow = findViewById(R.id.content2)
        drawerSlide()

        img_menu.setOnClickListener {
            closeOrOpenDrawer()
        }

        sw_mode.setOnClickListener {
            if (sw_mode.isChecked) {
                switchUiMode(true)
            } else {
                switchUiMode(false)
            }
        }


    }

    fun setTheme(isDark: Boolean) {
        if (isDark)
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme_Light)
    }

    private fun setDrawer() {
        if (!CommonUtils.getPrefValue(
                this,
                PrefConstants.TOKEN
            ).isBlank()
        ) {
            linearLayoutLogout.visibility = View.VISIBLE
            tv_username.text = CommonUtils.getPrefValue(
                this,
                PrefConstants.PREF_NAME
            )

            if (!CommonUtils.getPrefValue(
                    this,
                    PrefConstants.IMAGE
                ).isBlank()
            )
                Glide.with(this).load(
                    CommonUtils.getPrefValue(
                        this,
                        PrefConstants.IMAGE
                    )
                ).apply(
                    RequestOptions().override(
                        300,
                        200
                    ).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.logo).error(
                        R.drawable.logo
                    )
                ).into(img_user)
        } else {
            linearLayoutLogout.visibility = View.GONE
            Glide.with(this).load(
                R.drawable.logo
            ).apply(
                RequestOptions().override(
                    300,
                    200
                ).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.logo).error(
                    R.drawable.logo
                )
            ).into(img_user)
        }
    }

    fun drawerSlide() {
        var scaleFactor = 4f
        var cornerRadiusFactorTop = 55f
        var cornerRadiusFactorBottom = 100f
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        val actionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, R.string.add, R.string.track) {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset

                    content.translationX = slideX

                    content.scaleX = 1 - (slideOffset / scaleFactor)
                    content.scaleY = 1 - (slideOffset / scaleFactor)


                    var scaleFactor2 = 19f
                    val slideX2 = drawerView.width + 20 * slideOffset

                    content2.translationX = slideX2

                    content2.scaleX = 1 - (slideOffset / scaleFactor2)
                    content2.scaleY = 1 - (slideOffset / scaleFactor2 * 6)


                    val mainLayoutBackground = GradientDrawable()
                    mainLayoutBackground.setColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                    mainLayoutBackground.setCornerRadii(
                        floatArrayOf(
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset
                        )
                    )
                    content.background = mainLayoutBackground


                    val shadow = GradientDrawable()
                    shadow.setColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    shadow.setCornerRadii(
                        floatArrayOf(
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset
                        )
                    )

                    mainLayoutShadow.background = shadow
                    mainLayoutShadow.elevation = 16f
                    content.elevation = 16f


                    val toolBarGradeint = GradientDrawable()
                    toolBarGradeint.setColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.colorAccent
                        )
                    )

                    toolBarGradeint.setCornerRadii(

                        floatArrayOf(
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset,
                            cornerRadiusFactorTop * slideOffset

                        )
                    )
//                    appBar.background = toolBarGradeint


                    val bottomBarGradient = GradientDrawable()
                    bottomBarGradient.setColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            android.R.color.white
                        )
                    )
                    bottomBarGradient.setCornerRadii(
                        floatArrayOf(
                            0f,
                            0f,
                            0f,
                            0f,
                            0f,
                            0f,
                            cornerRadiusFactorBottom * slideOffset,
                            cornerRadiusFactorBottom * slideOffset
                        )
                    )

//                    bottomLayout.background = bottomBarGradient

                }
            }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }

    fun updateLocale(shouldRecreate: Boolean) {
        val languageToLoad: String
        languageToLoad = if (CommonUtils.getBooleanPrefValue(
                this,
                PrefConstants.IS_LANGUAGE_SELECTED
            ) && GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH
        ) {
            "fr"
        } else
            "en"

        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()

        config.setLocale(locale)
        resources?.updateConfiguration(
            config,
            resources?.displayMetrics
        )
        if (shouldRecreate)
            recreate()


    }

    override fun attachBaseContext(newBase: Context?) {
        val languageToLoad: String
        if (CommonUtils.getBooleanPrefValue(
                newBase,
                PrefConstants.IS_LANGUAGE_SELECTED
            ) && GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH
        ) {
            languageToLoad = "fr"
        } else
            languageToLoad = "en"
        super.attachBaseContext(MyContextWrapper.wrap(newBase!!, languageToLoad))
    }

    override fun onSupportNavigateUp() = findNavController(R.id.main_nav_fragment).navigateUp()

    fun updateLocation() {
        address.text = CommonUtils.getPrefValue(this, PrefConstants.ADDRESS)
    }

    private fun closeOrOpenDrawer() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun lockDrawer(enabled: Boolean) {
        var lockMode = if (enabled) {
            img_menu.isEnabled = false
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        } else {
            img_menu.isEnabled = true
            DrawerLayout.LOCK_MODE_UNLOCKED
        }

        drawerLayout.setDrawerLockMode(lockMode);
//        toggle.setDrawerIndicatorEnabled(enabled);


    }

    fun rateOrder() {
        if (CommonUtils.getPrefValue(this, PrefConstants.ORDER_TO_RATE) != "") {
            var item: OrderItemModel = Gson().fromJson(
                CommonUtils.getPrefValue(this, PrefConstants.ORDER_TO_RATE),
                OrderItemModel::class.java
            )

            CommonUtils.savePrefs(this, PrefConstants.ORDER_TO_RATE, "")

            if (item.is_driver_rated == "0") {
                var rateDriverDialog = RateDriverDialogFragment(item, this)
                rateDriverDialog.isCancelable = false
                rateDriverDialog.show(this.supportFragmentManager, "")
            } else if (item.is_restaurant_rated == "0") {
                var restaurantRatingDialogFragment = RestaurantRatingDialogFragment(item, this)
                restaurantRatingDialogFragment.isCancelable = false
                restaurantRatingDialogFragment.show(this.supportFragmentManager, "")
            } else {
                var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
                for (meal in item.orderDetails) {
                    if (meal.is_item_rated == "0") {
                        mealsToRate.add(meal)
                    }
                }

                if (mealsToRate.size > 0) {
                    var mealsRatingDialogFragment = MealsRatingDialogFragment(mealsToRate, this)
                    mealsRatingDialogFragment.isCancelable = false
                    mealsRatingDialogFragment.show(this.supportFragmentManager, "")
                }
            }
        }
    }

    override fun onDriverRateSubmit(rating: Float, orderItemModel: OrderItemModel) {


        rateDriver(
            token = CommonUtils.getPrefValue(this, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            driverId = orderItemModel.driverId
        )

        if (orderItemModel.is_restaurant_rated == "0") {
            var restaurantRatingDialogFragment =
                RestaurantRatingDialogFragment(orderItemModel, this)
            restaurantRatingDialogFragment.isCancelable = false
            restaurantRatingDialogFragment.show(this.supportFragmentManager, "")
        }
    }

    override fun onDriverRateCancel(orderItemModel: OrderItemModel) {
//        show restaurant rating if not rated
        if (orderItemModel.is_restaurant_rated == "0") {
            var restaurantRatingDialogFragment =
                RestaurantRatingDialogFragment(orderItemModel, this)
            restaurantRatingDialogFragment.isCancelable = false
            restaurantRatingDialogFragment.show(this.supportFragmentManager, "")
        }
    }

    override fun onrestaurantRateSubmit(rating: Float, orderItemModel: OrderItemModel) {
        rateRestaurant(
            token = CommonUtils.getPrefValue(this, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            restaurantId = orderItemModel.restaurantId.toString()
        )

        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
        for (meal in orderItemModel.orderDetails)
            if (meal.is_item_rated == "0")
                mealsToRate.add(meal)


        if (mealsToRate.size > 0) {
            var mealsRatingDialogFragment = MealsRatingDialogFragment(mealsToRate, this)
            mealsRatingDialogFragment.isCancelable = false
            mealsRatingDialogFragment.show(this.supportFragmentManager, "")
        }

    }

    override fun onRateRestaurantCancel(orderItemModel: OrderItemModel) {
        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
        for (meal in orderItemModel.orderDetails)
            if (meal.is_item_rated == "0")
                mealsToRate.add(meal)


        if (mealsToRate.size > 0) {
            var mealsRatingDialogFragment = MealsRatingDialogFragment(mealsToRate, this)
            mealsRatingDialogFragment.isCancelable = false
            mealsRatingDialogFragment.show(this.supportFragmentManager, "")
        }
    }

    override fun onMealRateSubmit(ratedMeals: ArrayList<OrderItemModel.OrderDetail>) {
        var map = HashMap<String, String>()

        for (meal in ratedMeals) {
            map.put(meal.itemId.toString(), meal.rating.toString())
        }

        rateMeals(
            token = CommonUtils.getPrefValue(this, PrefConstants.TOKEN),
            orderId = ratedMeals[0].orderId.toString(),
            ratings = map.toString()
        )
    }

    override fun onMealRateCancel() {

    }

    fun rateDriver(token: String, orderId: String, driverId: String, rating: String) {
//        isLoading.value = true

        ApiRepo.getInstance()
            .rateDriver(
                token = token,
                orderId = orderId, receiverId = driverId, rating = rating, review = ""
            ) { success, result ->
                //                isLoading.value = false
            }
    }

    fun rateRestaurant(token: String, orderId: String, restaurantId: String, rating: String) {
//        isLoading.value = true
        ApiRepo.getInstance()
            .rateRestaurant(
                token = token,
                orderId = orderId, receiverId = restaurantId, rating = rating, review = ""
            ) { success, result ->
                //                isLoading.value = false
            }
    }

    fun rateMeals(token: String, orderId: String, ratings: String) {
//        isLoading.value = true
        ApiRepo.getInstance()
            .rateMeals(
                token = token,
                orderId = orderId, rating = ratings
            ) { success, result ->
                //                isLoading.value = false
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 121) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleSignIn?.signInResult(task)
        }
    }

    private fun printHashKey() {
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.e("vvv", "printHashKey() Hash Key: $hashKey")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            Log.e("vvv", "printHashKey()", e)
//        } catch (e: Exception) {
//            Log.e("vvv", "printHashKey()", e)
//        }
    }

    fun initGoogleSignin(googleSigni: GoogleSignin) {
        this.googleSignIn = googleSigni
    }

    fun switchUiMode(selected: Boolean) {
        CommonUtils.saveBooleanPrefs(this, PrefConstants.IS_DARK_MODE, selected)
        recreate()
    }

    fun backTitle(title: String) {
        hideAll()

        top_bar.visibility = View.VISIBLE
        img_menu.visibility = View.GONE
        img_right.visibility = View.GONE
        deliverLayout.visibility = View.GONE
        img_back.visibility = View.VISIBLE

        tv_title.visibility = View.VISIBLE
        tv_title.text = title
    }

    fun menuAddress() {
        top_bar.visibility = View.VISIBLE
        img_menu.visibility = View.VISIBLE
        deliverLayout.visibility = View.VISIBLE
        img_back.visibility = View.GONE
        img_right.visibility = View.GONE
        tv_title.visibility = View.GONE
    }

    fun menuOnly() {
        top_bar.visibility = View.VISIBLE
        img_menu.visibility = View.VISIBLE
        deliverLayout.visibility = View.GONE
        img_back.visibility = View.GONE
        img_right.visibility = View.GONE
        tv_title.visibility = View.GONE
    }

    fun setRightIcon(icon: Int) {
        img_right.visibility = View.VISIBLE
        img_right.setImageResource(icon)
    }

    fun hideAll() {
        top_bar.visibility = View.GONE
        img_right.visibility = View.GONE
        bottom_navigation.visibility = View.GONE
        fab_cart.visibility = View.GONE
    }

    fun showFab() {
        fab_cart.visibility = View.VISIBLE

    }

    fun showBottomNavigation(index: Int) {
        bottom_navigation.visibility = View.VISIBLE
        bottom_navigation.selectedItemId = index
    }

    fun showBottomMenu() {

    }


}
