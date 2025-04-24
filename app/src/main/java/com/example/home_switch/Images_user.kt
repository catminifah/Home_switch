package com.example.home_switch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Images_user : AppCompatActivity() {

    private lateinit var bt_back: ImageButton
    private lateinit var tv_logo: ImageView
    lateinit var courseGRV: GridView
    lateinit var courseList: List<ImagesModel>
    private var backPressedTime = 0L
    private lateinit var connectionLiveData: ConnectionLiveData

    private lateinit var database: DatabaseReference

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
        setContentView(R.layout.activity_images_user)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        initView()
        setAdapter()
        checkNetworkConnectionLiveData()

    }

    private fun initView() {

        bt_back = findViewById(R.id.bt_back)
        tv_logo = findViewById(R.id.tv_logo)

        animationclick()

        bt_back.setOnClickListener {
            val iIntent = Intent(this, Infouser::class.java)
            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            var IntImage = intent.getIntExtra("IntImage",0)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)
            iIntent.putExtra("IntImage", IntImage)
            /*var username = intent.getStringExtra("username")
            iIntent.putExtra("username", username)
            var img = intent.getIntExtra("image",0)
            if (img!=0){
                iIntent.putExtra("image",img)
            }*/
            resultLauncher.launch(iIntent)
            finish()
            //animation page
            overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
        }

    }

    fun animationclick() {

        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }

    }

    private fun setAdapter() {
        courseGRV = findViewById(R.id.idGRV)
        courseList = ArrayList<ImagesModel>()

        addCourseList()

        courseGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            val user = FirebaseAuth.getInstance().currentUser
            var uid = user!!.uid
            var email = user!!.email
            database = FirebaseDatabase.getInstance().getReference(uid!!.toString())

            var username = intent.getStringExtra("username")

            var usermodel = UserModel(username,email,courseList[position].courseImg)
            database.child("InfoUser").setValue(usermodel).addOnSuccessListener {
                val iIntent = Intent(this, Infouser::class.java)
                var BoxSwitch = intent.getIntExtra("countbox",0)
                var countOn = intent.getIntExtra("countOn",0)
                iIntent.putExtra("countbox",BoxSwitch)
                iIntent.putExtra("countOn",countOn)
                var username = intent.getStringExtra("username")
                iIntent.putExtra("username", username)
                setResult(RESULT_OK, iIntent)
                resultLauncher.launch(iIntent)
                finish()
                //animation page
                overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
            }

            /*val iIntent = Intent(this, Infouser::class.java)
            /*var img = courseList[position].courseImg
            var IDimg = courseList[position].courseName
            iIntent.putExtra("image", img)
            iIntent.putExtra("IDimage", IDimg)
            iIntent.putExtra("page","imageuser")*/

            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)
            var username = intent.getStringExtra("username")
            iIntent.putExtra("username", username)
            setResult(RESULT_OK, iIntent)
            resultLauncher.launch(iIntent)
            finish()*/
            //overridePendingTransition(R.anim.static_animation, R.anim.zoom_out)
            //Toast.makeText(applicationContext, courseList[position].courseName + " selected", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addCourseList() {

        courseList = courseList + ImagesModel("00", R.drawable.avatar)
        courseList = courseList + ImagesModel("01", R.drawable.user_boy)
        courseList = courseList + ImagesModel("02", R.drawable.user_girl)
        courseList = courseList + ImagesModel("03", R.drawable.user_girl02)
        courseList = courseList + ImagesModel("04", R.drawable.user_man02)
        courseList = courseList + ImagesModel("05", R.drawable.user_man03)
        courseList = courseList + ImagesModel("06", R.drawable.user_man04)
        courseList = courseList + ImagesModel("07", R.drawable.user_man05)
        courseList = courseList + ImagesModel("08", R.drawable.user_person)
        courseList = courseList + ImagesModel("09", R.drawable.user_person02)
        courseList = courseList + ImagesModel("10", R.drawable.user_person03)
        courseList = courseList + ImagesModel("11", R.drawable.user_person04)
        courseList = courseList + ImagesModel("12", R.drawable.user_person05)
        courseList = courseList + ImagesModel("13", R.drawable.user_person06)
        courseList = courseList + ImagesModel("14", R.drawable.user_person07)
        courseList = courseList + ImagesModel("15", R.drawable.user_person08)
        courseList = courseList + ImagesModel("16", R.drawable.user_quechua)
        courseList = courseList + ImagesModel("17", R.drawable.user_quechua02)
        courseList = courseList + ImagesModel("18", R.drawable.user_woman)
        courseList = courseList + ImagesModel("19", R.drawable.user_woman02)
        courseList = courseList + ImagesModel("20", R.drawable.user_woman03)
        courseList = courseList + ImagesModel("21", R.drawable.user_recruiter)
        courseList = courseList + ImagesModel("22", R.drawable.user_recruiter02)
        courseList = courseList + ImagesModel("23", R.drawable.user_racer)
        courseList = courseList + ImagesModel("24", R.drawable.user_auditor)
        courseList = courseList + ImagesModel("25", R.drawable.user_woman04)
        courseList = courseList + ImagesModel("26", R.drawable.user_man06)
        courseList = courseList + ImagesModel("27", R.drawable.user_man07)
        courseList = courseList + ImagesModel("28", R.drawable.user_superhero)
        courseList = courseList + ImagesModel("29", R.drawable.user_redridinghood)
        courseList = courseList + ImagesModel("30", R.drawable.user_manager)
        courseList = courseList + ImagesModel("31", R.drawable.user_girl03)
        courseList = courseList + ImagesModel("32", R.drawable.user_woman05)
        courseList = courseList + ImagesModel("33", R.drawable.user_woman06)
        courseList = courseList + ImagesModel("34", R.drawable.user_avatar)
        courseList = courseList + ImagesModel("35", R.drawable.user_businesswoman)
        courseList = courseList + ImagesModel("36", R.drawable.user_man08)
        courseList = courseList + ImagesModel("37", R.drawable.user_man09)
        courseList = courseList + ImagesModel("38", R.drawable.user_girl04)
        courseList = courseList + ImagesModel("39", R.drawable.user_auditor02)
        courseList = courseList + ImagesModel("40", R.drawable.user_man10)
        courseList = courseList + ImagesModel("41", R.drawable.user_teacher)
        courseList = courseList + ImagesModel("42", R.drawable.user_oldman)
        courseList = courseList + ImagesModel("43", R.drawable.user_man11)
        courseList = courseList + ImagesModel("44", R.drawable.user_designer)
        courseList = courseList + ImagesModel("45", R.drawable.user_mechanic)
        courseList = courseList + ImagesModel("46", R.drawable.user_physical)
        courseList = courseList + ImagesModel("47", R.drawable.user_man12)
        courseList = courseList + ImagesModel("48", R.drawable.user_teenage)

        val courseAdapter = ImagesGridAdapter(courseList = courseList, this@Images_user)
        courseGRV.adapter = courseAdapter

    }

    //ปุ่มออก 1 ไปหน้า Info
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            val iIntent = Intent(this, Infouser::class.java)
            var IntImage = intent.getIntExtra("IntImage",0)
            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("IntImage", IntImage)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)
            var username = intent.getStringExtra("username")
            iIntent.putExtra("username", username)
            var img = intent.getIntExtra("image",0)
            if (img!=0){
                iIntent.putExtra("image",img)
            }
            resultLauncher.launch(iIntent)
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