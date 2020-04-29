package com.rvtechnologies.grigora.view.ui

import android.content.Context
import android.view.View
import android.view.ViewAnimationUtils
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.util.Base64
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.login_signup.GoogleSignin
import com.rvtechnologies.grigora.view.ui.rating.MealsDataDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RestaurantRatingDialogFragment
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import io.branch.referral.Branch
import io.branch.referral.BranchError
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MainActivity : AppCompatActivity(), RateDriverDialogFragment.DriverRate,
    MealsDataDialogFragment.MealsRate,
    RestaurantRatingDialogFragment.RestaurantRate {
    lateinit var content: RelativeLayout
    var googleSignIn: GoogleSignin? = null
    var alredayShown = false
    var CONSUMER_KEY = "koLwe43RuNy2nvhWFzVY1ssFG"
    var CONSUMER_SECRET: kotlin.String? = "2wVMF6TfJHXSfsTDb7pFJolHt5W0UX1UpuDXg0RRVC8L98ZXJc"
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        // Branch reinit (in case Activity is already in foreground when Branch link is clicked)
        Branch.getInstance().reInitSession(this, object : Branch.BranchReferralInitListener {
            override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
                if (error == null) {
                    referringParams?.get(AppConstants.CART_ID).toString()


                    val bundle = bundleOf(
                        AppConstants.RESTAURANT_ID to referringParams?.get(AppConstants.RESTAURANT_ID)
                            .toString(),
                        AppConstants.CART_ID to referringParams?.get(AppConstants.CART_ID)
                            .toString(),
                        AppConstants.FROM_PICKUP to false
                    )



                    Navigation.findNavController(this@MainActivity, R.id.main_nav_fragment)
                        .navigate(R.id.restaurantDetailGroup, bundle)

                } else {
                    Log.e("BRANCH SDK", error.message)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        Branch.getInstance().initSession(object : Branch.BranchReferralInitListener {
            override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
                if (error == null) {
                    referringParams?.get(AppConstants.CART_ID).toString()

                    val bundle = bundleOf(
                        AppConstants.RESTAURANT_ID to referringParams?.get(AppConstants.RESTAURANT_ID)
                            .toString(),
                        AppConstants.CART_ID to referringParams?.get(AppConstants.CART_ID)
                            .toString(),
                        AppConstants.FROM_PICKUP to false
                    )
                    Navigation.findNavController(this@MainActivity, R.id.main_nav_fragment)
                        .navigate(R.id.restaurantDetailGroup, bundle)

                } else {
                    Log.e("BRANCH SDK", error.message)
                }
            }
        }, this.intent.data, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printHashKey()
        //twitter
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
            .debug(false)
            .build()
        Twitter.initialize(config)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


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


        deliverLayout.setOnClickListener {
            if (CommonUtils.isLogin()) {
                Navigation.findNavController(this, R.id.main_nav_fragment)
                    .navigate(R.id.addressListFragment)
            } else {
                Navigation.findNavController(this, R.id.main_nav_fragment)
                    .navigate(R.id.selectLocationFragment)
            }

        }

        Log.e("token:: ", CommonUtils.getPrefValue(this, PrefConstants.TOKEN))
        Log.e("id:: ", CommonUtils.getPrefValue(this, PrefConstants.ID))
        Log.e("language:: ", GrigoraApp.getInstance().getCurrentLanguage())
        content = findViewById(R.id.content)

        img_menu.setOnClickListener {
            closeOrOpenDrawer()
        }
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            onNavDestinationSelected(
                item,
                Navigation.findNavController(this, R.id.main_nav_fragment)
            )
        }


    }

    fun setTheme(isDark: Boolean) {
        if (isDark)
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme_Light)
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
                    var mealsRatingDialogFragment = MealsDataDialogFragment(mealsToRate, this)
                    mealsRatingDialogFragment.isCancelable = false
                    mealsRatingDialogFragment.show(this.supportFragmentManager, "")
                }
            }
        }
    }

    override fun onDriverRateSubmit(
        rating: Float, goodReview: String,
        badReview: String, orderItemModel: OrderItemModel, tip: String
    ) {


        rateDriver(
            token = CommonUtils.getPrefValue(this, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            driverId = orderItemModel.driverId,
            goodReview = goodReview,
            badReview = badReview,
            tip = tip
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

    override fun onrestaurantRateSubmit(
        rating: Float, goodReview: String,
        badReview: String, orderItemModel: OrderItemModel
    ) {
        rateRestaurant(
            token = CommonUtils.getPrefValue(this, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            restaurantId = orderItemModel.restaurantId.toString(),
            goodReview = goodReview,
            badReview = badReview
        )

        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
        for (meal in orderItemModel.orderDetails)
            if (meal.is_item_rated == "0")
                mealsToRate.add(meal)


        if (mealsToRate.size > 0) {
            var mealsRatingDialogFragment = MealsDataDialogFragment(mealsToRate, this)
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
            var mealsRatingDialogFragment = MealsDataDialogFragment(mealsToRate, this)
            mealsRatingDialogFragment.isCancelable = false
            mealsRatingDialogFragment.show(this.supportFragmentManager, "")
        }
    }

    override fun onMealRateSubmit(ratedMeals: ArrayList<OrderItemModel.OrderDetail>) {
        var map = HashMap<String, HashMap<String, String>>()

        for (meal in ratedMeals) {
            var m = HashMap<String, String>()
            m.put("rating", meal.rating.toString())
            m.put("review", meal.review)

            map.put(meal.itemId.toString(), m)
        }
        rateMeals(
            token = CommonUtils.getPrefValue(this, PrefConstants.TOKEN),
            orderId = ratedMeals[0].orderId.toString(),
            ratings = map.toString()
        )
    }

    override fun onMealRateCancel() {

    }

    fun rateDriver(
        token: String, orderId: String, driverId: String, rating: String, goodReview: String,
        badReview: String, tip: String
    ) {
//        isLoading.value = true

        ApiRepo.getInstance()
            .rateDriver(
                token = token,
                orderId = orderId,
                receiverId = driverId,
                rating = rating,
                review = "",
                goodReview = goodReview,
                badReview = badReview, tipAmount = tip
            ) { success, result ->
                //                isLoading.value = false
            }
    }

    fun rateRestaurant(
        token: String, orderId: String, restaurantId: String, rating: String, goodReview: String,
        badReview: String
    ) {
//        isLoading.value = true
        ApiRepo.getInstance()
            .rateRestaurant(
                token = token,
                orderId = orderId,
                receiverId = restaurantId,
                rating = rating,
                review = badReview,
                goodReview = goodReview,
                badReview = ""
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
        } else if (data!=null && data.hasExtra("message"))
            CommonUtils.showMessage(drawerLayout,data.getStringExtra("message"))
    }

    private fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.e("vvv", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("vvv", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("vvv", "printHashKey()", e)
        }
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
        img_back.setOnClickListener {
            CommonUtils.hideKeyboard(this)
            onBackPressed()
        }

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

    fun showBottomNavigation(index: Int) {
        bottom_navigation.visibility = View.VISIBLE
        bottom_navigation.selectedItemId = index

    }

    fun showBottomMenu() {

    }

    fun updateCartButton() {
        if (!AppConstants.CART_RESTAURANT.isNullOrEmpty() && AppConstants.CART_COUNT > 0) {

            if (fab_cart.visibility != View.VISIBLE) {
                var x = drawerLayout.right
                var y = drawerLayout.bottom

                var endRadius =
                    Math.hypot(drawerLayout.width.toDouble(), drawerLayout.height.toDouble())
                        .toFloat()

                var anim =
                    ViewAnimationUtils.createCircularReveal(
                        fab_cart,
                        (drawerLayout.right + drawerLayout.left) / 2,
                        (drawerLayout.top + drawerLayout.bottom) / 2,
                        0F,
                        endRadius
                    )
                fab_cart.visibility = View.VISIBLE
                anim.duration = 800
                anim.start()
            }

            tv_restname.text = AppConstants.CART_RESTAURANT
            tv_items.text = AppConstants.CART_COUNT.toString() + " " + getString(R.string.items)

            fab_cart.setOnClickListener {
                Navigation.findNavController(this, R.id.main_nav_fragment)
                    .navigate(R.id.navigationCart)
            }
        } else {
            fab_cart.visibility = View.GONE
        }
    }

    fun showLoginAlert(pop: Boolean = false, id: Int = 0) {
        var alertDialog: AlertDialog? = null
        if (!alredayShown) {
            val dialogBuilder = this?.let { AlertDialog.Builder(it) }
            if (this is MainActivity && !this.isDestroyed && alertDialog == null) {
                val inflater = this.layoutInflater
                val dialogView = inflater.inflate(R.layout.alert_login, null)
                dialogBuilder?.setView(dialogView)
                dialogBuilder?.setCancelable(false)
                dialogView.btnLogin.setOnClickListener {
                    alredayShown = false
                    alertDialog?.dismiss()
                    Navigation.findNavController(this, R.id.main_nav_fragment)
                        .navigate(R.id.socialLoginFragment)
                }
                dialogView.btnLater.setOnClickListener {
                    alredayShown = false
                    alertDialog?.dismiss()
                    if (pop) {
                        selectedNavigation(id)
                    }

                }

                alertDialog = dialogBuilder?.create()
                alertDialog?.show()
                alredayShown = true

            }
        }
    }

    fun selectedNavigation(id: Int) {
        bottom_navigation.selectedItemId = id
    }

    fun setDestination(id: Int) {
        Navigation.findNavController(this, R.id.main_nav_fragment)
            .navigate(id)
    }


    fun clearStack() {
        Navigation.findNavController(this, R.id.main_nav_fragment)
            .popBackStack(R.id.nav_graph_xml, true)
    }
}


