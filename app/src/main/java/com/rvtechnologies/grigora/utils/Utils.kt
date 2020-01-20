package com.rvtechnologies.grigora.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.github.siyamed.shapeimageview.CircularImageView
import com.rvtechnologies.grigora.R
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.BlurTransformation

@BindingAdapter("image_url")
fun imageUrl(imageView: ImageView?, imageUrl: String?) {
    if (imageView != null) {
        Glide.with(imageView).load(imageUrl).apply(
            RequestOptions().override(
                300,
                200
            ).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.cuisine).error(
                R.drawable.cuisine
            )
        ).into(imageView)
    }
}

@BindingAdapter("normal_image_url")
fun normalImageUrl(imageView: ImageView?, imageUrl: String?) {
    if (imageView != null) {
        val circularProgressDrawable = CircularProgressDrawable(imageView?.context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Picasso.get()
            .load(imageUrl).placeholder(
                circularProgressDrawable
            )
            .error(
                circularProgressDrawable
            )
            .into(imageView)
    }
}

@BindingAdapter("image_url_person")
fun imageUrlPerson(imageView: CircularImageView?, imageUrl: String?) {
    if (imageView != null) {
        Glide.with(imageView).load(imageUrl).apply(
            RequestOptions().override(
                200,
                200
            ).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.user_placeholder).error(
                R.drawable.user_placeholder
            )
        ).into(imageView)
    }
}

@BindingAdapter("rounded_image_url")
fun imageRoundedUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView).load(imageUrl)
        .apply(
            RequestOptions().override(
                300,
                200
            ).transform(RoundedCorners(50)).placeholder(R.drawable.cuisine).error(
                R.drawable.cuisine
            )
        ).into(imageView)
}

@BindingAdapter("image_blur_url")
fun imageBlurUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView).load(imageUrl)

        .apply(
            RequestOptions().override(
                300,
                200
            ).transform(RoundedCorners(50)).placeholder(R.drawable.cuisine).error(
                R.drawable.cuisine
            )
        ).apply(bitmapTransform(BlurTransformation(25, 3))).into(imageView)
}
