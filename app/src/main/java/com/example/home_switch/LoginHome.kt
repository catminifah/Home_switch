package com.example.home_switch

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

class LoginHome : AppCompatActivity() {

    private lateinit var et_Email: EditText
    private lateinit var et_Password: EditText
    private lateinit var bt_login: Button
    private lateinit var bt_account: Button
    private lateinit var bt_Forgotpassword: Button
    private lateinit var bt_google: ImageButton

    private var backPressedTime = 0L
    private lateinit var auth: FirebaseAuth
    private lateinit var connectionLiveData: ConnectionLiveData

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                var intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_home)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        // Initialize Firebase Auth
        auth = Firebase.auth

        animationbackground()
        initView()
        checkUser()
        checkNetworkConnectionLiveData()

        /*
        https://www.youtube.com/watch?v=QAKq8UBv4GI
        https://firebase.google.com/docs/auth/android/start#kotlin+ktx_4
        https://www.youtube.com/watch?v=mhLlbWQ0p4s
        https://www.geeksforgeeks.org/google-signing-using-firebase-authentication-in-android-using-java/
        https://stackoverflow.com/questions/41015443/how-to-force-google-account-chooser-after-sign-out-in-android-firebase-auth
        */
    }

    private fun animationbackground() {
        var constraintLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        var animationDrawable:AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(5000)
        animationDrawable.start()
    }

    private fun initView() {

        et_Email = findViewById(R.id.et_Email)
        et_Password = findViewById(R.id.et_Password)
        bt_login = findViewById(R.id.bt_login)
        bt_account = findViewById(R.id.bt_account)
        bt_Forgotpassword = findViewById(R.id.bt_Forgotpassword)
        bt_google = findViewById(R.id.bt_google)
        et_Password.filters += InputFilter.LengthFilter(20)

        Button()

    }

    private fun Button() {
        bt_account.setOnClickListener {
            val iIntent = Intent(this, Register::class.java)
            resultLauncher.launch(iIntent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            et_Email.text.clear()
            et_Password.text.clear()
        }

        bt_Forgotpassword.setOnClickListener {
            val iIntent = Intent(this, Forgotpassword::class.java)
            resultLauncher.launch(iIntent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            et_Email.text.clear()
            et_Password.text.clear()
        }

        bt_login.setOnClickListener {
            performLogin()
        }

        bt_google.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id))
                .requestEmail().build()

            val signInClient = GoogleSignIn.getClient(this, options)
            GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut()
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        val iIntent = Intent(this, MainActivity::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    //Toast.makeText(this@login_home,"Successfully logged in",Toast.LENGTH_LONG).show()
                    resultLauncher.launch(iIntent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    //finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginHome, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun performLogin() {
        //lets get input from the user and the user
        var email = et_Email.text.toString()
        var password = et_Password.text.toString()
        if (et_Email?.text.toString().isEmpty() && et_Password?.text.toString().isEmpty()) {
            //Toast.makeText(this, "Enter edit all", Toast.LENGTH_SHORT).show()
        } else if (et_Email?.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
        } else if (et_Password?.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
        }  else if (et_Password?.text.toString().length<6||et_Password?.text.toString().length>20) {
            Toast.makeText(this, "Enter password must be 6-20 characters long", Toast.LENGTH_SHORT).show()
        } else {
            LoginUser(email, password)
        }
    }

    private fun LoginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val verification = auth.currentUser?.isEmailVerified
                    if (verification == true){
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(this@LoginHome.toString(), "signInWithEmail:success")
                        val iIntent = Intent(this, MainActivity::class.java)
                        resultLauncher.launch(iIntent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        et_Email.text.clear()
                        et_Password.text.clear()
                        //finish()
                    }else{
                        Toast.makeText(this,"Please verify your Email!",Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(this@LoginHome.toString(), "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Check email or password again",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }

    //กดจากแอพกดปุ่มออก 2 ทีออก
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(applicationContext, "Please back again to exit app", Toast.LENGTH_SHORT)
                .show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    //เช็คว่า user ล็อคอินค้างอยู่ในระบบหรือไม่ กำหนดว่าหน้าเริ่มต้นควรจะเป็นหน้าไหน
    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = auth.currentUser
        if (firebaseUser != null){
            //user not null, user is loggedin
            val showLogo = Intent(this, MainActivity::class.java)
            startActivity(showLogo)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        //https://www.youtube.com/watch?v=kxdoLfRL6DY
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