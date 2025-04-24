package com.example.home_switch

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rtugeek.android.colorseekbar.ColorSeekBar

class info_room : AppCompatActivity() {

    private lateinit var tv_Title: TextView
    private lateinit var tv_img: ImageView
    private lateinit var tv_SubTitle: TextView
    private lateinit var tv_info: TextView
    private lateinit var tv_Switch: TextView
    private lateinit var tv_logo: ImageView
    private lateinit var bt_edit: ImageButton
    private lateinit var bt_home: ImageButton
    lateinit var img_user: ImageView

    //private lateinit var tv_switch: TextView
    private lateinit var imgbg: ImageView
    private lateinit var img_settingcolor: ImageView
    private lateinit var switchroom: Switch

    private var iColor = 0
    private var ColorBarPosition = 0
    private var AlphaBarPosition = 0

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: DatabaseReference

    private var backPressedTime = 0L
    private lateinit var connectionLiveData: ConnectionLiveData

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                var number = data!!.getIntExtra("number", 0)
                var intent = Intent()
                intent.putExtra("page", "edit")
                intent.putExtra("number", number)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_room)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        animationbackground()

        initView()
        setImageUser()
        setSwitch()
        animation()
        getInfo()
        button()
        checkNetworkConnectionLiveData()

    }

    private fun animationbackground() {
        var constraintLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        var animationDrawable: AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(1000)
        animationDrawable.setExitFadeDuration(1000)
        animationDrawable.start()
    }

    private fun initView() {
        imgbg = findViewById(R.id.tv_imgbg)
        tv_Title = findViewById(R.id.tv_Title)
        tv_img = findViewById(R.id.tv_img)
        tv_SubTitle = findViewById(R.id.tv_SubTitle)
        tv_Title = findViewById(R.id.tv_Title)
        tv_img = findViewById(R.id.tv_img)
        tv_SubTitle = findViewById(R.id.tv_SubTitle)
        tv_info = findViewById(R.id.tv_info)
        tv_Switch = findViewById(R.id.tv_Switch)
        tv_logo = findViewById(R.id.tv_logo)

        switchroom = findViewById(R.id.switchroom)

        //tv_switch = findViewById(R.id.tv_switch)
        bt_edit = findViewById(R.id.bt_edit)
        bt_home = findViewById(R.id.bt_home)
        img_settingcolor = findViewById(R.id.img_settingcolor)
        img_user = findViewById(R.id.img_user)

        tv_Title.setOnClickListener { Title() }
        tv_SubTitle.setOnClickListener { SubTitle() }

        img_settingcolor.setOnClickListener { settingcolor() }

    }

    private fun Title() {
        val bulider = AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.title_dialog, null)
        bulider.setView(customView)
        val dialog = bulider.create()

        val et_Title = customView.findViewById<TextView>(R.id.et_Title)
        val bt_yes = customView.findViewById<ImageButton>(R.id.bt_yes)
        val bt_no = customView.findViewById<ImageButton>(R.id.bt_no)

        et_Title.filters += InputFilter.LengthFilter(10)
        var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
        et_Title.text = tv_Title.text


        bt_yes.setOnClickListener {

            if (iColor == 0) {
                var roomModal = RoomModel(
                    et_Title!!.text.toString(),
                    tv_SubTitle!!.text.toString(),
                    switchroom.isChecked,
                    roomModel.getcolorBarPosition(),
                    roomModel.getalphaBarPosition(),
                    roomModel.geticolor()
                )
                var uid = intent.getStringExtra("uid").toString()
                var sKey = intent.getStringExtra("sKey").toString()
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                databaseReference.child(sKey).setValue(roomModal)
                tv_Title!!.text = et_Title!!.text.toString()
            } else {
                var roomModal = RoomModel(
                    et_Title!!.text.toString(),
                    tv_SubTitle!!.text.toString(),
                    switchroom.isChecked,
                    ColorBarPosition,
                    AlphaBarPosition,
                    iColor
                )
                var uid = intent.getStringExtra("uid").toString()
                var sKey = intent.getStringExtra("sKey").toString()
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                databaseReference.child(sKey).setValue(roomModal)
                tv_Title!!.text = et_Title!!.text.toString()
            }
            dialog.dismiss()
        }
        bt_no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun SubTitle() {
        val bulider = AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.subtitle_dialog, null)
        bulider.setView(customView)
        val dialog = bulider.create()

        val et_SubTitle = customView.findViewById<TextView>(R.id.et_SubTitle)
        val bt_yes = customView.findViewById<ImageButton>(R.id.bt_yes)
        val bt_no = customView.findViewById<ImageButton>(R.id.bt_no)

        et_SubTitle.filters += InputFilter.LengthFilter(80)
        var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
        et_SubTitle.text = tv_SubTitle.text


        bt_yes.setOnClickListener {

            if (iColor == 0) {
                var roomModal = RoomModel(
                    tv_Title.text.toString(),
                    et_SubTitle!!.text.toString(),
                    switchroom.isChecked,
                    roomModel.getcolorBarPosition(),
                    roomModel.getalphaBarPosition(),
                    roomModel.geticolor()
                )
                var uid = intent.getStringExtra("uid").toString()
                var sKey = intent.getStringExtra("sKey").toString()
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                databaseReference.child(sKey).setValue(roomModal)
                tv_SubTitle!!.text = et_SubTitle!!.text.toString()
            } else {
                var roomModal = RoomModel(
                    tv_Title.text.toString(),
                    et_SubTitle!!.text.toString(),
                    switchroom.isChecked,
                    ColorBarPosition,
                    AlphaBarPosition,
                    iColor
                )
                var uid = intent.getStringExtra("uid").toString()
                var sKey = intent.getStringExtra("sKey").toString()
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                databaseReference.child(sKey).setValue(roomModal)
                tv_SubTitle!!.text = et_SubTitle!!.text.toString()
            }
            dialog.dismiss()
        }
        bt_no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun settingcolor() {
        val bulider = AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.colorseekbar_dialog, null)
        bulider.setView(customView)
        val dialog = bulider.create()
        val bt_yes = customView.findViewById<ImageButton>(R.id.bt_yes)
        val bt_no = customView.findViewById<ImageButton>(R.id.bt_no)

        var li = customView.findViewById<LinearLayout>(R.id.layoutid)

        var colorSeekBar = customView.findViewById<ColorSeekBar>(R.id.color_seek_bar)
        //สร้างเงื่อนไขเมื่อเข้ามาเปลี่ยนค่าสีที่จะเปลี่ยนเป็น0 จะใช้เงื่อนไขดึง ค่าสีมาจาก main ถ้าไม่ใช่ดึงค่าสีที่เซฟในนี้ก่อนหน้า

        if (iColor == 0) {
            var courseList = intent.getSerializableExtra("roomModel") as RoomModel
            colorSeekBar.colorBarPosition = courseList.getcolorBarPosition()!!
            var alphaBarPosition = courseList.getalphaBarPosition()
            colorSeekBar.alphaBarPosition = alphaBarPosition!!

        } else {
            colorSeekBar.colorBarPosition = ColorBarPosition
            colorSeekBar.alphaBarPosition = AlphaBarPosition
        }

        var icolor = 0
        var iColorBarPosition = 0
        var iAlphaBarPosition = 0

        colorSeekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(
                colorBarPosition: Int,
                alphaBarPosition: Int,
                color: Int
            ) {
                icolor = color
                iColorBarPosition = colorBarPosition
                iAlphaBarPosition = alphaBarPosition
                val drawable = GradientDrawable().apply {
                    colors = intArrayOf(
                        icolor,
                        Color.parseColor("#FFFFFF")
                    )
                    gradientType = GradientDrawable.RADIAL_GRADIENT
                    shape = GradientDrawable.OVAL
                    gradientRadius = 300F
                }
                li.background = drawable
            }
        })

        bt_yes.setOnClickListener {
            iColor = icolor
            ColorBarPosition = iColorBarPosition
            AlphaBarPosition = iAlphaBarPosition
            var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
            var roomModal = RoomModel(
                tv_Title.text.toString(),
                tv_SubTitle!!.text.toString(),
                switchroom.isChecked,
                ColorBarPosition,
                AlphaBarPosition,
                iColor
            )
            var uid = intent.getStringExtra("uid").toString()
            var sKey = intent.getStringExtra("sKey").toString()
            firebaseDatabase = FirebaseDatabase.getInstance()
            databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
            databaseReference.child(sKey).setValue(roomModal)

            //เช็คเปิด/ปิด
            if (switchroom.isChecked == true) {
                tv_img.setImageResource(R.drawable.light_on)
                setBGLight(iColor)
            }

            //Toast.makeText(baseContext, "color = $iColor", Toast.LENGTH_SHORT,).show()
            dialog.dismiss()
        }

        bt_no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun getInfo() {

        var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
        tv_Title!!.text = roomModel.getTitle()
        tv_SubTitle!!.text = roomModel.getSubTitle()

        var hexColor = roomModel.geticolor()

        if (roomModel.getSwitch() == true) {
            tv_img.setImageResource(R.drawable.light_on)
            //tv_switch.text = "Switch : " + resources.getString(R.string.lightison)
            if (hexColor != null) {
                setBGLight(hexColor)
            }
        } else {
            tv_img.setImageResource(R.drawable.light_off)
            //tv_switch.text = "Switch : " + resources.getString(R.string.lightisoff)
            setBGLight(Color.parseColor("#FFFFFF"))
        }

        var courseList = intent.getSerializableExtra("roomModel") as RoomModel
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

    private fun button() {
        //next page -> edit
        bt_edit.setOnClickListener {
            var number = intent.getIntExtra("number", 0)
            var uid = intent.getStringExtra("uid").toString()
            var sKey = intent.getStringExtra("sKey").toString()
            var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
            roomModel.setTitle(tv_Title.text.toString())
            roomModel.setSubTitle(tv_SubTitle.text.toString())
            if (iColor != 0) {
                roomModel.seticolor(iColor)
                roomModel.setcolorBarPosition(ColorBarPosition)
                roomModel.setalphaBarPosition(AlphaBarPosition)
            }
            roomModel.setSwitch(switchroom.isChecked)
            val iIntent = Intent(this, edit_room::class.java)
            iIntent.putExtra("number", number)
            iIntent.putExtra("uid", uid)
            iIntent.putExtra("sKey", sKey)
            iIntent.putExtra("roomModel", roomModel)
            resultLauncher.launch(iIntent)
            //animation next page
            overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
        }
        //button back -> home
        bt_home.setOnClickListener {
            finish()
            //animation next page
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
        }
    }

    private fun setSwitch() {

        //set switch on/off
        switchroom.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switchroom.text = resources.getString(R.string.lightison)
                tv_img.setImageResource(R.drawable.light_on)
                if (iColor != 0) {
                    setBGLight(iColor)
                    var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
                    var roomModal = RoomModel(
                        tv_Title.text.toString(),
                        tv_SubTitle!!.text.toString(),
                        switchroom.isChecked,
                        ColorBarPosition,
                        AlphaBarPosition,
                        iColor
                    )
                    var uid = intent.getStringExtra("uid").toString()
                    var sKey = intent.getStringExtra("sKey").toString()
                    firebaseDatabase = FirebaseDatabase.getInstance()
                    databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                    databaseReference.child(sKey).setValue(roomModal)
                } else {
                    var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
                    var hexColor = roomModel.geticolor()
                    if (hexColor != null) {
                        setBGLight(hexColor)
                        var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
                        var roomModal = RoomModel(
                            tv_Title.text.toString(),
                            tv_SubTitle!!.text.toString(),
                            switchroom.isChecked,
                            roomModel.getcolorBarPosition(),
                            roomModel.getalphaBarPosition(),
                            roomModel.geticolor()
                        )
                        var uid = intent.getStringExtra("uid").toString()
                        var sKey = intent.getStringExtra("sKey").toString()
                        firebaseDatabase = FirebaseDatabase.getInstance()
                        databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                        databaseReference.child(sKey).setValue(roomModal)
                    }
                }
            } else {
                switchroom.text = resources.getString(R.string.lightisoff)
                tv_img.setImageResource(R.drawable.light_off)
                setBGLight(Color.parseColor("#FFFFFF"))
                if (iColor != 0) {
                    var roomModal = RoomModel(
                        tv_Title.text.toString(),
                        tv_SubTitle!!.text.toString(),
                        switchroom.isChecked,
                        ColorBarPosition,
                        AlphaBarPosition,
                        iColor
                    )
                    var uid = intent.getStringExtra("uid").toString()
                    var sKey = intent.getStringExtra("sKey").toString()
                    firebaseDatabase = FirebaseDatabase.getInstance()
                    databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                    databaseReference.child(sKey).setValue(roomModal)
                } else {
                    var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
                    var hexColor = roomModel.geticolor()
                    if (hexColor != null) {
                        var roomModel = intent.getSerializableExtra("roomModel") as RoomModel
                        var roomModal = RoomModel(
                            tv_Title.text.toString(),
                            tv_SubTitle!!.text.toString(),
                            switchroom.isChecked,
                            roomModel.getcolorBarPosition(),
                            roomModel.getalphaBarPosition(),
                            roomModel.geticolor()
                        )
                        var uid = intent.getStringExtra("uid").toString()
                        var sKey = intent.getStringExtra("sKey").toString()
                        firebaseDatabase = FirebaseDatabase.getInstance()
                        databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                        databaseReference.child(sKey).setValue(roomModal)
                    }
                }
            }
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
        }
        imgbg.setImageDrawable(drawable)
    }

    private fun setImageUser() {

        val user = FirebaseAuth.getInstance().currentUser
        var uid = user!!.uid
        database = FirebaseDatabase.getInstance()
            .getReference(uid!!.toString())
        database.child("InfoUser")
            .get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                val model = it.getValue(UserModel::class.java)
                //เพิ่มกรณีที่userไม่ได้สร้างข้อมูลส่วนตัว
                if (model != null) {
                    if (model.getimage()!=null){
                        img_user.setImageResource(model.getimage()!!)
                    }
                }else{
                    img_user.setImageResource(R.drawable.adduser)
                }
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

    }

    private fun animation() {
        //set animation start

        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        tv_info.visibility = View.VISIBLE
        tv_info.startAnimation(animationFadeIn)
        tv_Switch.visibility = View.VISIBLE
        tv_Switch.startAnimation(animationFadeIn)
        tv_logo.visibility = View.VISIBLE
        tv_logo.startAnimation(animationFadeIn)
        tv_Title.visibility = View.VISIBLE
        tv_Title.startAnimation(animationFadeIn)
        tv_SubTitle.visibility = View.VISIBLE
        tv_SubTitle.startAnimation(animationFadeIn)
        tv_img.visibility = View.VISIBLE
        tv_img.startAnimation(animationFadeIn)
        //tv_switch.visibility = View.VISIBLE
        //tv_switch.startAnimation(animationFadeIn)

        animationclick()

    }

    fun animationclick() {

        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }
        tv_info.setOnClickListener {
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_info)
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_Switch)
        }
        tv_Switch.setOnClickListener {
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_info)
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_Switch)
        }

        //animation img click

        tv_img.setOnClickListener {
            tv_img.animate().apply {
                duration = 1000
                rotationBy(360f)
            }.withEndAction {
                tv_img.animate().apply {
                    duration = 1000
                    rotationBy(360f)
                }.start()
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            finish()
            //animation next page
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
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