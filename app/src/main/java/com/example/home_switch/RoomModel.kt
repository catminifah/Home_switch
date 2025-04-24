package com.example.home_switch

import java.io.Serializable

data class RoomModel(
    private var sTitle: String? = null,
    private var sSubTitle: String? = null,
    private var sSwitch: Boolean? = null,
    private var colorBarPosition: Int? = null,
    private var alphaBarPosition: Int? = null,
    private var icolor: Int? = null
) : Serializable {

    fun getTitle(): String? {
        return sTitle
    }

    fun setTitle(Title: String?) {
        this.sTitle = Title
    }

    fun getSubTitle(): String? {
        return sSubTitle
    }

    fun setSubTitle(SubTitle: String?) {
        this.sSubTitle = SubTitle
    }

    fun getSwitch(): Boolean? {
        return sSwitch
    }

    fun setSwitch(Switch: Boolean?) {
        this.sSwitch = Switch
    }

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