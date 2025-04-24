package com.example.home_switch

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.home_switch.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var courseGRV: GridView
    private lateinit var courseList: ArrayList<RoomModel>
    lateinit var bt_add: ImageButton
    lateinit var tv_user: TextView
    lateinit var img_user: ImageView
    lateinit var img_BoxSwitch: ImageView
    lateinit var tv_date: TextView
    lateinit var tv_tHome: TextView
    lateinit var tv_tSwitch: TextView
    lateinit var tv_BoxSwitch: TextView
    lateinit var tv_logo: ImageView
    lateinit var courseAdapter: BaseAdapter
    private var backPressedTime = 0L

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectionLiveData: ConnectionLiveData

    // declare the GoogleSignInClient
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //There are no request codes
                val data: Intent? = result.data
                var page = data!!.getStringExtra("page")
                if (page!!.compareTo("add") == 0) {
                    courseAdapter.notifyDataSetChanged()
                    tv_BoxSwitch.text = courseList.size.toString()
                } else if (page!!.compareTo("edit") == 0) {
                    courseAdapter.notifyDataSetChanged()
                    tv_BoxSwitch.text = courseList.size.toString()
                }else if (page!!.compareTo("infouser") == 0) {
                    checkUser()
                }
            }
        }

    private var firebaseListener = object : FirebaseListener {
        override fun OnDataChange(courseList1: ArrayList<RoomModel>) {
            this@MainActivity.runOnUiThread(Runnable { updateData(courseList1) })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        courseList = ArrayList<RoomModel>()

        //เชื่อมข้อมูลระบบล็อคอิน เพื่อไว้ใช้เช็คสถานะการล็อคอิน
        auth = Firebase.auth
        courseList.clear()
        GoogleSignInOptions()

        initView()
        setAdapter()
        setImageUser()

        firebaseManager = FirebaseManager.getFirebaseManager(firebaseListener)
        firebaseManager.setCurrentDatabase()

        onItemClick()
        onItemLongClick()
        buttonAdd()
        buttonUser()
        checkUser()

        checkNetworkConnectionLiveData()
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
                    tv_user.text = "Hello "+model.getUsername()+"!!!"
                }
            }else{
                img_user.setImageResource(R.drawable.adduser)
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
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

    private fun initView() {
        courseGRV = findViewById(R.id.g_home)
        courseList = ArrayList<RoomModel>()
        bt_add = findViewById(R.id.bt_add)
        tv_user = findViewById(R.id.tv_user)
        img_user = findViewById(R.id.img_user)
        img_BoxSwitch = findViewById(R.id.img_BoxSwitch)
        tv_date = findViewById(R.id.tv_date)
        tv_tHome = findViewById(R.id.tv_tHome)
        tv_tSwitch = findViewById(R.id.tv_tSwitch)
        tv_logo = findViewById(R.id.tv_logo)
        tv_BoxSwitch = findViewById(R.id.tv_BoxSwitch)

        animation()
        getdatetime()
    }

    //ทำไว้เพื่อจะล็อคอินออก ห้ามลบ***
    fun GoogleSignInOptions(){
        //We have to configure the GoogleSignInOptions object, call requestIdToken in Mainactivity.kt as follows:
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
        // pass the same server client ID used while implementing the LogIn feature earlier.
    }

    fun getdatetime() {
        // day
        val time = Calendar.getInstance().time
        val formatterdate = SimpleDateFormat("dd\nMM\nyyyy")
        val currentdate = formatterdate.format(time)
        tv_date.text = currentdate.toString()
    }

    fun setAdapter() {
        courseAdapter = GridRVAdapter(courseList, this@MainActivity)
        courseGRV.adapter = courseAdapter
    }

    private fun updateData(courseList1: ArrayList<RoomModel>) {
        courseList.clear()
        for (c in courseList1) {
            courseList.add(c)
        }
        tv_BoxSwitch.text = courseList.size.toString()
        courseAdapter.notifyDataSetChanged()
    }

    fun animation() {
        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        tv_tHome.visibility = View.VISIBLE
        tv_tHome.startAnimation(animationFadeIn)
        tv_tSwitch.visibility = View.VISIBLE
        tv_tSwitch.startAnimation(animationFadeIn)
        tv_logo.visibility = View.VISIBLE
        tv_logo.startAnimation(animationFadeIn)

        YoYo.with(Techniques.Shake).duration(1000).repeat(1).playOn(img_BoxSwitch)

        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }
        tv_tHome.setOnClickListener {
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_tHome)
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_tSwitch)
        }
        tv_tSwitch.setOnClickListener {
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_tHome)
            YoYo.with(Techniques.BounceInDown).duration(1000).repeat(1).playOn(tv_tSwitch)
        }
        img_BoxSwitch.setOnClickListener {
            //YoYo.with(Techniques.Hinge).duration(1000).repeat(1).playOn(img_BoxSwitch)
            YoYo.with(Techniques.Tada).duration(1000).repeat(1).playOn(img_BoxSwitch)
            //DropOut Shake Swing (ใส่หลอดไฟหน้าinfouser) Tada Wave (ใส่หลอดไฟหน้าinfouser)
        }
    }

    //next page info item
    fun onItemClick() {
        //เมื่อคลิก item ไปยังหน้า infomation
        courseGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            val user = FirebaseAuth.getInstance().currentUser
            val uid = user!!.uid
            var keylist = firebaseManager.getkeyList()
            var sKey = keylist[position]
            var roomModel:RoomModel=(courseList[position])
            val iIntent = Intent(this, info_room::class.java)
            iIntent.putExtra("uid",uid)
            iIntent.putExtra("number", position)
            iIntent.putExtra("sKey",sKey)
            iIntent.putExtra("roomModel",roomModel)
            resultLauncher.launch(iIntent)
            overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
        }
    }

    //delete item
    fun onItemLongClick() {
        //เมื่อคลิก item ค้างไว้ dialog เด้งมาถามว่าต้องการลบ item นี้มั้ย
        courseGRV.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->

                var keylist = firebaseManager.getkeyList()
                val factory = LayoutInflater.from(this)
                val view: View = factory.inflate(R.layout.img_alertdialog, null)
                val dialogBuilder = AlertDialog.Builder(this).create()
                dialogBuilder.setView(view)
                //แปลงสีโดยใช้ html
                var sTitle = courseList[position].getTitle()
                var sKey = keylist[position]
                dialogBuilder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Title : $sTitle</font>"))
                dialogBuilder.setMessage(Html.fromHtml("<font color='#FFFFFF'>Do you want delete?</font>"))

                //set bg set color button
                dialogBuilder.setOnShowListener {
                    dialogBuilder.window!!.setBackgroundDrawableResource(R.drawable.button11)
                    dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.parseColor("#5591EB"))
                    dialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(Color.parseColor("#3F51B5"))
                }

                dialogBuilder.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes") { dialog, which ->
                    //delete item
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user!!.uid
                    firebaseDatabase = FirebaseDatabase.getInstance()
                    databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
                    databaseReference.child(sKey).removeValue()
                    tv_BoxSwitch.text = courseList.size.toString()
                }
                dialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, "No") { dialog, which ->
                    //cancel
                    dialog.cancel()
                }

                dialogBuilder.show()

                return@OnItemLongClickListener true
            }
    }

    //next page add item
    fun buttonAdd() {
        //ปุ่มหน้าเพิ่ม item
        bt_add.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            val uid = user!!.uid

            val iIntent = Intent(this, add_room::class.java)
            iIntent.putExtra("uid",uid)
            resultLauncher.launch(iIntent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    private fun buttonUser(){
        img_user.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val email = user!!.email

            var countbox = courseList.size
            var countOn = 0
            courseList.forEachIndexed { index, element ->
                if (courseList[index].getSwitch() == true){
                    countOn++
                }
            }

            val iIntent = Intent(this, Infouser::class.java)
            iIntent.putExtra("email",email)
            iIntent.putExtra("countbox",countbox)
            iIntent.putExtra("countOn",countOn)
            resultLauncher.launch(iIntent)
            finish()
            overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
        }
    }

    //เช็คว่า user ล็อคอินค้างอยู่ในระบบหรือไม่
    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            //user null, user is logout
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    //กดจากแอพกดปุ่มออก 2 ทีออก
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
            System.exit(0)
        } else {
            Toast.makeText(applicationContext, "Please back again to exit app", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}