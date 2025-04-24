package com.example.home_switch

import java.io.Serializable

class oColor(
    private var colorBarPosition: Int? = null,
    private var alphaBarPosition: Int? = null,
    private var icolor: Int? = null
) : Serializable {
    fun getcolorBarPosition(): Int? {
        return colorBarPosition
    }

    fun setcolorBarPosition(colorBarPosition: Int?) {
        this.colorBarPosition = colorBarPosition
    }

    fun getalphaBarPosition(): Int? {
        return alphaBarPosition
    }

    fun setalphaBarPosition(alphaBarPosition: Int?) {
        this.alphaBarPosition = alphaBarPosition
    }

    fun geticolor(): Int? {
        return icolor
    }

    fun seticolor(icolor: Int?) {
        this.icolor = icolor
    }

}