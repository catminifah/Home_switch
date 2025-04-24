package com.example.home_switch

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rtugeek.android.colorseekbar.ColorSeekBar
import java.net.URI


class add_room : AppCompatActivity() {

    private lateinit var bt_add: ImageButton
    private lateinit var bt_back: ImageButton
    private lateinit var et_Title: EditText
    private lateinit var et_SubTitle: EditText
    private lateinit var tv_img: ImageView
    private lateinit var colorSeekBar: ColorSeekBar
    private lateinit var switchroom: Switch
    private lateinit var imgbg: ImageView
    private var iColor: Int = 0x000000
    private lateinit var tv_logo: ImageView
    private lateinit var colorModal: oColor

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseManager: FirebaseManager

    private var backPressedTime = 0L

    private lateinit var connectionLiveData: ConnectionLiveData

    private var firebaseListener = object : FirebaseListener {
        override fun OnDataChange(courseList1: ArrayList<RoomModel>) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary);

        firebaseManager = FirebaseManager.getFirebaseManager(firebaseListener)
        //databaseReference = firebaseManager.getDatabaseReference()

        initView()
        animation()
        switchroom()
        colorSeekBar()
        checkNetworkConnectionLiveData()
    }

    private fun animation() {
        //animation click
        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }

        tv_img.setOnClickListener {
            YoYo.with(Techniques.Swing).duration(1000).repeat(1).playOn(tv_img)
        }
    }

    private fun initView() {

        imgbg = findViewById(R.id.tv_imgbg)
        tv_img = findViewById(R.id.tv_img)
        tv_logo = findViewById(R.id.tv_logo)
        colorSeekBar = findViewById(R.id.color_seek_bar)
        //พอเปลี่ยนเวอร์ชั่นแล้วบัค colorSeekBar
        switchroom = findViewById(R.id.switchroom)
        bt_add = findViewById(R.id.bt_add)
        bt_back = findViewById(R.id.bt_back)
        et_Title = findViewById(R.id.et_Title)
        et_SubTitle = findViewById(R.id.et_SubTitle)

        //กำหนดความยาวอักษร
        //editText.filters += InputFilter.LengthFilter(maxLength)
        et_Title.filters += InputFilter.LengthFilter(10)
        et_SubTitle.filters += InputFilter.LengthFilter(80)

        bt_add.setOnClickListener {
            savedata()
        }

        bt_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

    }

    fun savedata() {

        var roomModal = RoomModel(
            et_Title!!.text.toString(),
            et_SubTitle!!.text.toString(),
            switchroom!!.isChecked,
            colorModal.getcolorBarPosition(),
            colorModal.getalphaBarPosition(),
            colorModal.geticolor()
        )

        //tableName
        //databaseReference = FirebaseDatabase.getInstance().getReference("Home Switch")
        var uid=intent.getStringExtra("uid").toString()
        var intent = Intent()
        intent.putExtra("page", "add")

        databaseReference = FirebaseDatabase.getInstance().getReference(uid!!.toString()).child("HomeSwitch")

        databaseReference.push().setValue(roomModal).addOnSuccessListener {
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK, intent)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED, intent)
        }
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)

    }

    private fun setBGLight(color1: Int) {
        val drawable = GradientDrawable().apply {
            colors = intArrayOf(
                color1,
                Color.parseColor("#FFFFFF")
            )
            gradientType = GradientDrawable.RADIAL_GRADIENT
            shape = GradientDrawable.OVAL
            gradientRadius = 300F
        }
        imgbg.setImageDrawable(drawable)
    }

    private fun switchroom() {
        //img ดึงเช็คจาก switchroom
        switchroom.setChecked(false)
        setBGLight(Color.parseColor("#FFFFFF"))
        switchroom.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switchroom.text = resources.getString(R.string.lightison)
                tv_img.setImageResource(R.drawable.light_on)
                setBGLight(iColor)
                YoYo.with(Techniques.Flash).duration(1000).repeat(1).playOn(tv_img)
            } else {
                switchroom.text = resources.getString(R.string.lightisoff)
                tv_img.setImageResource(R.drawable.light_off)
                setBGLight(Color.parseColor("#FFFFFF"))
                YoYo.with(Techniques.Flash).duration(1000).repeat(1).playOn(tv_img)
            }
        }
    }

    private fun colorSeekBar() {
        //เหลือ colorSeekBar แล้วดึงค่าสีออกมา
        colorSeekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(
                colorBarPosition: Int,
                alphaBarPosition: Int,
                color: Int
            ) {
                iColor = color
                if (switchroom.isChecked)
                    setBGLight(color)
                colorModal = oColor(colorBarPosition, alphaBarPosition, color)
            }
        })
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