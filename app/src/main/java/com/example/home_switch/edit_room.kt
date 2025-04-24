package com.example.home_switch

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.RoomDatabase
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rtugeek.android.colorseekbar.ColorSeekBar

class edit_room : AppCompatActivity() {

    private lateinit var bt_edit: ImageButton
    private lateinit var bt_back: ImageButton
    private lateinit var et_Title: EditText
    private lateinit var et_SubTitle: EditText
    private lateinit var tv_img: ImageView
    private lateinit var tv_logo: ImageView
    private lateinit var colorSeekBar: ColorSeekBar
    private lateinit var switchroom: Switch
    private lateinit var imgbg: ImageView
    private var iColor: Int = 0x000000
    private lateinit var colorModal: oColor

    //private lateinit var courseList:RoomModel

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //private lateinit var firebaseManager: FirebaseManager

    private var backPressedTime = 0L

    private lateinit var connectionLiveData: ConnectionLiveData

    /*private var firebaseListener = object : FirebaseListener {
        override fun OnDataChange(courseList1: ArrayList<RoomModel>) {
            updateData(courseList1)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_room)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        /*var uid=intent.getStringExtra("uid").toString()
        databaseReference = FirebaseDatabase.getInstance().getReference(uid!!.toString())*/
        //firebaseManager = FirebaseManager.getFirebaseManager(firebaseListener)
        //databaseReference = firebaseManager.getDatabaseReference()

        //courseList = ArrayList<RoomModel>()

        /*var list = firebaseManager.getDataList()
        courseList.clear()
        for (c in list) {
            courseList.add(c)
        }*/

        initView()
        setImgSwitch()
        setSwitch()
        colorSeekBar()
        checkNetworkConnectionLiveData()

    }

    /*private fun updateData(courseList1: ArrayList<RoomModel>) {
        courseList.clear()
        for (c in courseList1) {
            courseList.add(c)
        }
    }*/

    private fun initView() {
        imgbg = findViewById(R.id.tv_imgbg)
        tv_img = findViewById(R.id.tv_img)
        tv_logo = findViewById(R.id.tv_logo)
        colorSeekBar = findViewById(R.id.color_seek_bar)
        //พอเปลี่ยนเวอร์ชั่นแล้วบัค colorSeekBar
        switchroom = findViewById(R.id.switchroom)
        bt_edit = findViewById(R.id.bt_edit)
        bt_back = findViewById(R.id.bt_back)
        et_Title = findViewById(R.id.et_Title)
        et_SubTitle = findViewById(R.id.et_SubTitle)

        settext()
        setcolor()
        UpdateInfo()

        bt_back.setOnClickListener {
            finish()
            //animation page
            overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
        }

        animation()

    }

    private fun animation() {
        //animation เมื่อ click img
        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }
        tv_img.setOnClickListener {
            YoYo.with(Techniques.Swing).duration(1000).repeat(1).playOn(tv_img)
        }
    }

    private fun settext() {

        //กำหนดขนาดตัวอักษร จำกัดจน.อักษร
        et_Title.filters += InputFilter.LengthFilter(10)
        et_SubTitle.filters += InputFilter.LengthFilter(80)

        //var number = intent.getIntExtra("number", 0)
        var courseList= intent.getSerializableExtra("roomModel") as RoomModel
        //set text from array
        et_Title.setText(courseList.getTitle())
        et_SubTitle.setText(courseList.getSubTitle())
    }

    private fun setcolor() {
        //set ค่าสีเริ่มต้นถ้าหากเปิดไฟ
        //var number = intent.getIntExtra("number", 0)
        var courseList=intent.getSerializableExtra("roomModel") as RoomModel
        colorSeekBar.colorBarPosition = courseList.getcolorBarPosition()!!
        var alphaBarPosition = courseList.getalphaBarPosition()
        colorSeekBar.alphaBarPosition = alphaBarPosition!!
    }

    //แก้ไขข้อมูล
    private fun UpdateInfo() {
        bt_edit.setOnClickListener {
            var roomModal = RoomModel(
                et_Title!!.text.toString(),
                et_SubTitle!!.text.toString(),
                switchroom!!.isChecked,
                colorModal.getcolorBarPosition(),
                colorModal.getalphaBarPosition(),
                colorModal.geticolor()
            )

            var number = intent.getIntExtra("number", 0)
            var uid=intent.getStringExtra("uid").toString()
            /*var keylist = FirebaseManager.getFirebaseManager(firebaseListener).getkeyList()
            var sKey = keylist[number]*/
            var sKey=intent.getStringExtra("sKey").toString()
            firebaseDatabase = FirebaseDatabase.getInstance()
            databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
            databaseReference.child(sKey).setValue(roomModal)

            var intent = Intent()
            intent.putExtra("number", number)
            setResult(RESULT_OK, intent)
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
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
            //setStroke(2, Color.parseColor("#000000"))
        }
        imgbg.setImageDrawable(drawable)
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

    private fun setImgSwitch() {

        //var number = intent.getIntExtra("number", 0)
        var courseList=intent.getSerializableExtra("roomModel") as RoomModel
        //img ดึงเช็คจาก switchroom
        courseList.getSwitch()?.let { switchroom.setChecked(it) }
        if (courseList.getSwitch() == false) {
            setBGLight(Color.parseColor("#FFFFFF"))
            tv_img.setImageResource(R.drawable.light_off)
            switchroom.text = resources.getString(R.string.lightisoff)
        } else {
            tv_img.setImageResource(R.drawable.light_on)
            switchroom.text = resources.getString(R.string.lightison)
        }
    }

    private fun setSwitch() {

        //set switch on/off
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

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            finish()
            //animation page
            overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
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