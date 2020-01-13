package com.rvtechnologies.grigora.utils

import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import kotlinx.android.synthetic.main.loader_layout.view.*
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


object CommonUtils {
    fun showMessage(view: View?, msg: String) {
        if (view != null)
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun savePrefs(context: Context?, key: String, value: String?) {
        if (context != null && value != null) {
            val prefs = context.getSharedPreferences(PrefConstants.PREF_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun getBooleanPrefValue(context: Context?, key: String): Boolean {
        val prefs = context?.getSharedPreferences(PrefConstants.PREF_NAME, Context.MODE_PRIVATE)
        return prefs?.getBoolean(key, false)!!
    }

    fun saveBooleanPrefs(context: Context?, key: String, value: Boolean) {
        if (context != null) {
            val prefs = context.getSharedPreferences(PrefConstants.PREF_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }
    }

    fun getPrefValue(context: Context?, key: String): String {
        val prefs = context?.getSharedPreferences(PrefConstants.PREF_NAME, Context.MODE_PRIVATE)
        return prefs?.getString(key, "").toString()
    }

    fun delPrefValue(context: Context?): Boolean {
        if (context == null)
            return false
        val prefs = context.getSharedPreferences(PrefConstants.PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.commit()
        return true
    }

    private var progressBarDialog: Dialog? = null

    fun showLoader(context: Context?, message: String) {
        if (progressBarDialog == null && context != null) {
            val view = LayoutInflater.from(context).inflate(R.layout.custom_loader, null)
            progressBarDialog = Dialog(context)
            progressBarDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            progressBarDialog?.setContentView(view)
            progressBarDialog?.setCancelable(false)
            progressBarDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            view.bringToFront()
//            view.loadingMessage.text = message
            progressBarDialog?.setCancelable(false)
            progressBarDialog?.show()
        }
    }

    fun hideLoader() {
        if (progressBarDialog != null) {
            progressBarDialog?.dismiss()
            progressBarDialog = null
        }
    }

    fun parseError(response: Response<JsonElement>?): String? {

        return when (response?.code()) {
            401 -> "Unauthorized"
            403 -> "Forbidden"
            405 -> "Method not allowed"
            400, 404, 422 -> {
                if (response.errorBody() != null) {
                    try {
                        val error: CommonResponseModel<*> = Gson().fromJson(
                            response.errorBody()?.string(),
                            CommonResponseModel::class.java
                        )
                        return if (!error.message?.isBlank()!!) error.message else
                            "Not Found"
                    } catch (e: Exception) {
                        return "Not Found"
                    }
                } else {
                    return "Something went wrong"
                }
            }
            429 -> "Too Many Requests"
            500 -> "Internal Server Error"
            503 -> "Service Unavailable"
            else -> "Something went wrong"
        }
    }

    fun isValidEmail(email: String): Boolean {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }


    fun convertDpToPixel(dp: Int, context: Context): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return (dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun doBounceAnimation(targetView: View) {
        targetView.animate().scaleX(0.5F).scaleY(0.5F).setDuration(150)
            .withEndAction { targetView.animate().scaleX(1F).scaleY(1F).duration = 150 }
    }

    fun compressFile(imageUri: String): String {
        //            String filePath = getRealPathFromURI(context,imageUri)
        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(imageUri, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        //      max Height and width values of the compressed image is taken as 816x612

        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

        //      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        //      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(imageUri, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageUri)

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        val filename = getFilename()
        try {
            out = FileOutputStream(filename)

            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return filename

    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            while (height / inSampleSize > reqHeight || width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getFilename(): String {
        val file = File(Environment.getExternalStorageDirectory().path, "SevenT")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    fun isLink(text: String): Boolean {
        return text.contains("http")
    }

    fun dip2pixel(context: Context, n: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            n,
            context.resources.displayMetrics
        ).toInt()
    }

    fun changeStatusBarColor(color: Int, activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var decor = activity.window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.window.statusBarColor = color
            activity.window.statusBarColor = color
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = color
        }
    }

    fun isDarkMode(): Boolean {
        return true
    }

    fun isFirst(): Boolean {
        return false
    }
}