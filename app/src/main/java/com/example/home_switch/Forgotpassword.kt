package com.example.home_switch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class Forgotpassword : AppCompatActivity() {

    private lateinit var et_Email: TextInputLayout//EditText
    private lateinit var bt_reset: Button
    private lateinit var bt_back: ImageButton

    private lateinit var auth: FirebaseAuth

    private var backPressedTime = 0L
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        auth = FirebaseAuth.getInstance()

        initView()
        checkNetworkConnectionLiveData()
    }

    private fun initView() {

        et_Email = findViewById(R.id.et_Email)
        bt_reset = findViewById(R.id.bt_reset)
        bt_back = findViewById(R.id.bt_back)

        bt_reset.setOnClickListener {

            var email: String = et_Email.editText?.text?.toString() ?: ""
            if (email.isEmpty()/*et_Email?.text.toString().isEmpty()*/) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword()
            }
        }

        bt_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
    }

    private fun resetPassword() {
        val sEmail = et_Email.editText?.text?.toString() ?: ""/*et_Email.text.toString()*/
        auth.sendPasswordResetEmail(sEmail)
            .addOnSuccessListener {
                Toast.makeText(this, "Please Check your Email!", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
        backPressedTime = System.currentTimeMillis()
    }

    //แสดงข้อความการเชื่อมต่อเน็ต
    private fun checkNetworkConnectionLiveData() {
        connectionLiveData = ConnectionLiveData(application)
        connectionLiveData.observe(this) { isAvailable ->
            when (isAvailable) {
                true -> null
                else -> Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

}