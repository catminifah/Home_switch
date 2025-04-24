package com.example.home_switch

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class Splash_Screen : AppCompatActivity() {

    private var spalshSereen_logo: ImageView? = null
    lateinit var tv_tHome: TextView
    lateinit var tv_tSwitch: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        tv_tHome = findViewById(R.id.tv_tHome)
        tv_tSwitch = findViewById(R.id.tv_tSwitch)

        YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_tHome)
        YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_tSwitch)

        spalshSereen_logo = findViewById(R.id.tv_logo)
        val logo: ImageView? = spalshSereen_logo as ImageView?

        logo?.alpha = 0f
        logo?.animate()?.setDuration(3000)?.alpha(1f)?.withEndAction {

            val showLogo = Intent(this, LoginHome::class.java)
            startActivity(showLogo)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

}