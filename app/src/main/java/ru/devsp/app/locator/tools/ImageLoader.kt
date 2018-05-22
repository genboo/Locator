package ru.devsp.app.locator.tools

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.lang.Exception

object ImageLoader {
    fun loadImageFromCache(imageView: ImageView, image: String?) {
        //Принудительная загрузка из кэша
        Picasso.get()
                .load(image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        //not used
                    }

                    override fun onError(ex: Exception) {
                        //Если в кэше все-таки нет, загружаем из сети
                        loadImage(imageView, image)
                    }
                })
    }

    fun loadImage(imageView: ImageView, image: String?) {
        Picasso.get()
                .load(image)
                .into(imageView)
    }

}