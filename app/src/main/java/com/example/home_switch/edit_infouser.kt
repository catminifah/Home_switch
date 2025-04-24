package com.example.home_switch

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class edit_infouser : AppCompatActivity() {

    private lateinit var img_uploadimg: ImageView
    private lateinit var tv_logo: ImageView
    private lateinit var bt_save: ImageButton
    private lateinit var bt_back: ImageButton
    private lateinit var et_Username: EditText
    private lateinit var tv_email: TextView
    lateinit var drawable: Drawable
    private var backPressedTime = 0L


    private lateinit var database: DatabaseReference

    private lateinit var connectionLiveData: ConnectionLiveData

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                var page = data!!.getStringExtra("page")
                if (page!!.compareTo("imageuser") == 0) {
                    var img = intent.getIntExtra("image",0)
                    img_uploadimg.setImageResource(img)
                }
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_infouser)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        initView()
        setButton()
        checkNetworkConnectionLiveData()

    }

    private fun setButton() {

        img_uploadimg.setOnClickListener {
            val iIntent = Intent(this, Images_user::class.java)
            iIntent.putExtra("username", et_Username.text.toString())
            var IntImage = intent.getIntExtra("IntImage",0)
            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("IntImage", IntImage)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)
            var img = intent.getIntExtra("image",0)
            if (img!=0){
                iIntent.putExtra("image",img)
            }
            resultLauncher.launch(iIntent)
            finish()
            //animation next page
            overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
        }

        bt_back.setOnClickListener {

            val iIntent = Intent(this, Infouser::class.java)
            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)
            resultLauncher.launch(iIntent)
            finish()
            //animation next page
            overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)

        }

        bt_save.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            var uid = user!!.uid
            var email = user!!.email
            database = FirebaseDatabase.getInstance().getReference(uid!!.toString())

            var img = intent.getIntExtra("image",0)
            var username = intent.getStringExtra("username")
            var usermodel:UserModel
            if(et_Username.text.toString().isEmpty()){
                Toast.makeText(applicationContext, "Please Enter Username", Toast.LENGTH_SHORT).show()
            }else{
                if (img==0&&username==""){
                    usermodel = UserModel(et_Username.text.toString(),email,R.drawable.adduser)
                    database.child("InfoUser").setValue(usermodel).addOnSuccessListener {
                        val iIntent = Intent(this, Infouser::class.java)
                        var BoxSwitch = intent.getIntExtra("countbox",0)
                        var countOn = intent.getIntExtra("countOn",0)
                        iIntent.putExtra("countbox",BoxSwitch)
                        iIntent.putExtra("countOn",countOn)
                        resultLauncher.launch(iIntent)
                        finish()
                        //animation page
                        overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
                    }
                }else if (img==0){
                    var IntImage = intent.getIntExtra("IntImage",0)
                    usermodel = UserModel(et_Username.text.toString(),email,IntImage)
                    database.child("InfoUser").setValue(usermodel).addOnSuccessListener {
                        val iIntent = Intent(this, Infouser::class.java)
                        var BoxSwitch = intent.getIntExtra("countbox",0)
                        var countOn = intent.getIntExtra("countOn",0)
                        iIntent.putExtra("countbox",BoxSwitch)
                        iIntent.putExtra("countOn",countOn)
                        resultLauncher.launch(iIntent)
                        finish()
                        //animation page
                        overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
                    }
                }else{
                    usermodel = UserModel(et_Username.text.toString(),email,img)
                    database.child("InfoUser").setValue(usermodel).addOnSuccessListener {
                        val iIntent = Intent(this, Infouser::class.java)
                        var BoxSwitch = intent.getIntExtra("countbox",0)
                        var countOn = intent.getIntExtra("countOn",0)
                        iIntent.putExtra("countbox",BoxSwitch)
                        iIntent.putExtra("countOn",countOn)
                        resultLauncher.launch(iIntent)
                        finish()
                        //animation page
                        overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
                    }
                }
            }
        }
    }

    private fun initView() {

        img_uploadimg = findViewById(R.id.img_uploadimg)
        tv_logo = findViewById(R.id.tv_logo)
        bt_save = findViewById(R.id.bt_save)
        bt_back = findViewById(R.id.bt_back)
        et_Username = findViewById(R.id.et_Username)
        tv_email = findViewById(R.id.tv_email)

        var username = intent.getStringExtra("username")
        if (username!=""){
            et_Username.setText(username)
        }
        et_Username.filters += InputFilter.LengthFilter(10)
        val user = FirebaseAuth.getInstance().currentUser
        tv_email.text = user!!.email.toString()

        setImageView()
        animationclick()
    }

    fun animationclick() {

        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }

    }

    //https://www.youtube.com/watch?v=KI8KW4n_adI
    private fun setImageView() {
        var img = intent.getIntExtra("image",0)

        var IntImage = intent.getIntExtra("IntImage",0)
        if (IntImage!=0){
            img_uploadimg.setImageResource(IntImage)
        }

        if (img!=0){
            img_uploadimg.setImageResource(img)
        }

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

    //ปุ่มออก 1 ไปหน้า Infouser
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            val iIntent = Intent(this, Infouser::class.java)
            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)
            resultLauncher.launch(iIntent)
            finish()
            //animation next page
            overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
        }
        backPressedTime = System.currentTimeMillis()
    }

}