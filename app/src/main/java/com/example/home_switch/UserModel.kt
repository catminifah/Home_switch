package com.example.home_switch

import android.graphics.Bitmap
import java.io.Serializable

data class UserModel(
    private var Username: String? = null,
    private var Email: String? = null,
    private var image: Int? = null
): Serializable {

    fun getUsername(): String? {
        return Username
    }

    fun setUsername(Username: String?) {
        this.Username = Username
    }

    fun getEmail(): String? {
        return Email
    }

    fun setEmail(Email: String?) {
        this.Email = Email
    }

    fun getimage(): Int? {
        return image
    }

    fun setimage(image: Int?) {
        this.image = image
    }

}
