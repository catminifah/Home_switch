package com.example.home_switch

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Infouser : AppCompatActivity() {

    private lateinit var img_imguser: ImageView
    private lateinit var tv_logo: ImageView
    //private lateinit var bt_edit: ImageButton
    private lateinit var bt_home: ImageButton
    private lateinit var bt_Reset: Button
    private lateinit var bt_logout: Button
    private lateinit var tv_username: TextView
    private lateinit var tv_name: TextView
    private lateinit var tv_email: TextView
    private var backPressedTime = 0L

    private lateinit var tv_BoxSwitch: TextView
    private lateinit var tv_Switch_off: TextView
    private lateinit var tv_Switch_on: TextView

    private lateinit var img_BoxSwitch: ImageView
    private lateinit var img_Switch_off: ImageView
    private lateinit var img_Switch_on: ImageView

    private lateinit var img_home: ImageView
    private lateinit var img_email: ImageView
    private lateinit var img_key: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var IntImage:Int = 0

    // declare the GoogleSignInClient
    lateinit var mGoogleSignInClient: GoogleSignInClient

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
        setContentView(R.layout.activity_infouser)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        auth = FirebaseAuth.getInstance()
        GoogleSignInOptions()

        animationbackground()
        initView()
        setInfoUser()
        button()
        animationclick()
        checkNetworkConnectionLiveData()
        //https://www.youtube.com/watch?v=DWIGAkYkpg8&t=882s
    }

    private fun initView() {

        img_imguser = findViewById(R.id.img_imguser)
        tv_logo = findViewById(R.id.tv_logo)
        //bt_edit = findViewById(R.id.bt_edit)
        bt_home = findViewById(R.id.bt_home)
        bt_Reset = findViewById(R.id.bt_Reset)
        bt_logout = findViewById(R.id.bt_logout)
        tv_username = findViewById(R.id.tv_username)
        tv_name = findViewById(R.id.tv_name)
        tv_email = findViewById(R.id.tv_email)

        tv_BoxSwitch = findViewById(R.id.tv_BoxSwitch)
        tv_Switch_off = findViewById(R.id.tv_Switch_off)
        tv_Switch_on = findViewById(R.id.tv_Switch_on)

        img_BoxSwitch = findViewById(R.id.img_BoxSwitch)
        img_Switch_off = findViewById(R.id.img_Switch_off)
        img_Switch_on = findViewById(R.id.img_Switch_on)

        img_home = findViewById(R.id.img_home)
        img_email = findViewById(R.id.img_email)
        img_key = findViewById(R.id.img_key)

        val user = FirebaseAuth.getInstance().currentUser
        var email = user!!.email
        tv_email.text = email.toString()

        setBoxSwitch()
        animation()

    }

    private fun animation() {
        img_BoxSwitch.setOnClickListener {
            YoYo.with(Techniques.Tada).duration(1000).repeat(1).playOn(img_BoxSwitch)
        }
        img_Switch_off.setOnClickListener {
            YoYo.with(Techniques.Wave).duration(1000).repeat(1).playOn(img_Switch_off)
        }
        img_Switch_on.setOnClickListener {

        }
        img_home.setOnClickListener {
            YoYo.with(Techniques.Landing).duration(1000).repeat(1).playOn(img_home)
        }
        img_email.setOnClickListener {
            YoYo.with(Techniques.Shake).duration(1000).repeat(1).playOn(img_email)
        }
        img_key.setOnClickListener {
            YoYo.with(Techniques.Pulse).duration(1000).repeat(1).playOn(img_key)
        }
    }

    private fun setBoxSwitch() {
        var BoxSwitch = intent.getIntExtra("countbox",0)
        if (BoxSwitch!=null&&BoxSwitch!=0){

            tv_BoxSwitch.text = BoxSwitch.toString()
            var countOn = intent.getIntExtra("countOn",0)
            tv_Switch_on.text = countOn.toString()
            var countOff = BoxSwitch-countOn
            tv_Switch_off.text = countOff.toString()
        }
    }

    private fun setInfoUser() {
        val user = FirebaseAuth.getInstance().currentUser
        var uid = user!!.uid
        database = FirebaseDatabase.getInstance().getReference(uid!!.toString())
        database.child("InfoUser").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val model = it.getValue(UserModel::class.java)
            //เพิ่มกรณีที่userไม่ได้สร้างข้อมูลส่วนตัว
            if (model != null) {
                if (model.getimage()!=null&&model.getimage()!=0){
                    if (model.getimage()!=R.drawable.avatar){
                        img_imguser.setImageResource(model.getimage()!!)
                        IntImage= model.getimage()!!
                    }else{
                        img_imguser.setImageResource(R.drawable.adduser)
                        IntImage= model.getimage()!!
                    }

                }
                if (model.getUsername()!=""){
                    tv_name.text = model.getUsername().toString()
                }
            }else{
                img_imguser.setImageResource(R.drawable.adduser)
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun animationbackground() {

        var constraintLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        var animationDrawable: AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(1000)
        animationDrawable.setExitFadeDuration(1000)
        animationDrawable.start()

    }

    private fun button(){
        //button back -> home
        bt_home.setOnClickListener {
            val iIntent = Intent(this, MainActivity::class.java)
            resultLauncher.launch(iIntent)
            finish()
            //animation next page
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
        }

        img_imguser.setOnClickListener {
            val iIntent = Intent(this, Images_user::class.java)
            if (tv_name.text.toString()==""){
                iIntent.putExtra("username", "")
            }else{
                iIntent.putExtra("username", tv_name.text.toString())
            }
            iIntent.putExtra("IntImage", IntImage)

            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)

            resultLauncher.launch(iIntent)
            //animation next page
            overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
            finish()
        }

        bt_Reset.setOnClickListener {
            showResetDialog()
        }

        bt_logout.setOnClickListener {
            showLogoutDialog()
        }

        tv_name.setOnClickListener {
            username()
        }
        tv_username.setOnClickListener {
            username()
        }

        /*bt_edit.setOnClickListener {
            val iIntent = Intent(this, edit_infouser::class.java)
            if (tv_name.text.toString()==""){
                iIntent.putExtra("username", "")
            }else{
                iIntent.putExtra("username", tv_name.text.toString())
            }
            iIntent.putExtra("IntImage", IntImage)

            var BoxSwitch = intent.getIntExtra("countbox",0)
            var countOn = intent.getIntExtra("countOn",0)
            iIntent.putExtra("countbox",BoxSwitch)
            iIntent.putExtra("countOn",countOn)

            resultLauncher.launch(iIntent)
            //animation next page
            overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
            finish()
        }*/

    }

    private fun username() {
        val bulider = androidx.appcompat.app.AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.username_dialog, null)
        bulider.setView(customView)
        val dialog = bulider.create()

        val et_Username = customView.findViewById<TextView>(R.id.et_Username)
        val bt_yes = customView.findViewById<ImageButton>(R.id.bt_yes)
        val bt_no = customView.findViewById<ImageButton>(R.id.bt_no)

        et_Username.filters += InputFilter.LengthFilter(10)
        et_Username.text = tv_name.text

        bt_yes.setOnClickListener {
            var usermodel = UserModel(et_Username.text.toString(),tv_email.text.toString(),IntImage)
            val user = FirebaseAuth.getInstance().currentUser
            var uid = user!!.uid
            database = FirebaseDatabase.getInstance().getReference(uid!!.toString())
            database.child("InfoUser").setValue(usermodel).addOnSuccessListener {
                tv_name.text = et_Username.text
                dialog.dismiss()
            }
        }

        bt_no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showResetDialog(){
        val bulider = AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_reset_dialog,null)
        bulider.setView(customView)

        val dialog = bulider.create()

        val btnYes = customView.findViewById<Button>(R.id.btn_yes)
        val btnNo = customView.findViewById<Button>(R.id.btn_no)
        val textemail = customView.findViewById<TextView>(R.id.tv_email)

        textemail.text = "Email : "+ intent.getStringExtra("email").toString()

        btnYes.setOnClickListener {
            var email: String = tv_email.text.toString()
            if (email.isEmpty()/*et_Email?.text.toString().isEmpty()*/) {
                email = intent.getStringExtra("email").toString()
                resetPassword(email)
            } else {
                resetPassword(email)
            }
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    //ทำไว้เพื่อจะล็อคอินออก ห้ามลบ***
    fun GoogleSignInOptions(){
        //We have to configure the GoogleSignInOptions object, call requestIdToken in Mainactivity.kt as follows:
        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
        // pass the same server client ID used while implementing the LogIn feature earlier.
    }

    private fun showLogoutDialog(){
        val bulider = AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_layout_dialog,null)
        bulider.setView(customView)

        val user = FirebaseAuth.getInstance().currentUser
        val email = user!!.email

        val dialog = bulider.create()

        val btnYes = customView.findViewById<Button>(R.id.btn_yes)
        val btnNo = customView.findViewById<Button>(R.id.btn_no)
        val textemail = customView.findViewById<TextView>(R.id.tv_email)

        textemail.text = "Email : "+email

        btnYes.setOnClickListener {
            //logout email
            auth.signOut()
            //logout google
            mGoogleSignInClient.signOut()
            /*val iIntent = Intent(this, MainActivity::class.java)
            iIntent.putExtra("page","infouser")*/
            var intent = Intent()
            intent.putExtra("page","infouser")
            setResult(RESULT_OK, intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    fun animationclick() {

        tv_logo.setOnClickListener {
            YoYo.with(Techniques.RubberBand).duration(1000).repeat(1).playOn(tv_logo)
        }

    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Please Check your Email!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
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

    //ปุ่มออก 1 ไปหน้า main
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            val iIntent = Intent(this, MainActivity::class.java)
            resultLauncher.launch(iIntent)
            finish()
            //animation next page
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
        }
        backPressedTime = System.currentTimeMillis()
    }

}