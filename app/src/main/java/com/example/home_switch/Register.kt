package com.example.home_switch

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jetbrains.annotations.Nullable
import java.util.Random
import java.util.concurrent.TimeUnit


open class Register : AppCompatActivity() {

    private lateinit var et_Email: TextInputLayout
    private lateinit var et_Password: TextInputLayout
    private lateinit var et_cfPassword: TextInputLayout
    private lateinit var bt_continue: Button
    private lateinit var bt_back: ImageButton

    private lateinit var auth: FirebaseAuth
    private var backPressedTime = 0L
    private lateinit var connectionLiveData: ConnectionLiveData

    private lateinit var et_otp1: EditText
    private lateinit var et_otp2: EditText
    private lateinit var et_otp3: EditText
    private lateinit var et_otp4: EditText
    private lateinit var bt_Verify: Button

    private var code: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        auth = Firebase.auth

        initView()
        checkNetworkConnectionLiveData()

    }

    private fun initView() {

        et_Email = findViewById(R.id.et_Email)
        et_Password = findViewById(R.id.et_Password)
        et_cfPassword = findViewById(R.id.et_cfPassword)
        bt_continue = findViewById(R.id.bt_continue)
        bt_back = findViewById(R.id.bt_back)

        //et_Password.filters += InputFilter.LengthFilter(20)
        //et_cfPassword.filters += InputFilter.LengthFilter(20)

        bt_continue.setOnClickListener {

            var email: String = et_Email.editText?.text?.toString() ?: ""//et_Email.text.toString()
            var password: String =
                et_Password.editText?.text?.toString() ?: ""//et_Password.text.toString()
            var cfpassword: String =
                et_cfPassword.editText?.text?.toString() ?: ""//et_cfPassword.text.toString()

            if (email.isEmpty() && password.isEmpty() && cfpassword.isEmpty()/*et_cfPassword?.text.toString().isEmpty()*/) {
                //Toast.makeText(this, "Enter edit all", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            } else if (cfpassword.isEmpty()/*et_cfPassword?.text.toString().isEmpty()*/) {
                Toast.makeText(this, "Enter confirm password", Toast.LENGTH_SHORT).show()
            } else {
                if (password != cfpassword) {
                    Toast.makeText(this, "password mismatch", Toast.LENGTH_SHORT).show()
                } else {
                    //randomcode()
                    //showOTPDialog()
                    createUser(email, password)
                }
            }
        }

        bt_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    /*//สุ่มค่าส่งเลข OTP
    private fun randomcode() {
        var random: Random = Random()
        code = random.nextInt(8999) + 1000
        var url = "https://codedaffsfe.000webhostapp.com/sendEmail.php"
        var requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this@Register, "Send OTP to email :"  + response, Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@Register, "Send OTP to email :" + error, Toast.LENGTH_SHORT).show()
            }) {
            @Nullable
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val mydata: MutableMap<String, String> = HashMap()
                mydata.put("email",et_Email.editText?.text?.toString() ?: "")
                mydata.put("code",code.toString())
                return mydata
            }
        }
        requestQueue.add(stringRequest)
    }*/

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //ส่งจดหมายยื่นยันสมัครสำเร็จ
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        Log.d(this@Register.toString(), "createUserWithEmail:success")
                    }?.addOnFailureListener {
                        Log.d(this@Register.toString(), it.toString())
                    }
                    finish()
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    auth.signOut()
                    Toast.makeText(baseContext, "We have sent verification mail to $email", Toast.LENGTH_SHORT,).show()
                    et_Email.editText?.text?.clear()
                    et_Password.editText?.text?.clear()
                    et_cfPassword.editText?.text?.clear()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(this@Register.toString(), "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "This email has already been taken.", Toast.LENGTH_SHORT,).show()
                }
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

    /*private fun showOTPDialog() {

        //https://www.youtube.com/watch?v=672Gmm5k8DY
        //https://www.youtube.com/watch?v=06YKlMdWyMM
        //https://firebase.google.com/docs/auth/android/email-link-auth#kotlin+ktx
        val bulider = AlertDialog.Builder(this)
        val customView = LayoutInflater.from(this).inflate(R.layout.otp_dialog_layout, null)
        bulider.setView(customView)
        val dialog = bulider.create()

        val textemail = customView.findViewById<TextView>(R.id.tv_email)
        textemail.text = "Email : " + et_Email.editText?.text?.toString() ?: ""
        et_otp1 = customView.findViewById(R.id.et_otp1)
        et_otp2 = customView.findViewById(R.id.et_otp2)
        et_otp3 = customView.findViewById(R.id.et_otp3)
        et_otp4 = customView.findViewById(R.id.et_otp4)
        bt_Verify = customView.findViewById(R.id.bt_Verify)

        bt_Verify.setOnClickListener {
            var entercodeotp = et_otp1.text.toString() +
                    et_otp2.text.toString() +
                    et_otp3.text.toString() +
                    et_otp4.text.toString()

            if(entercodeotp == code.toString()){
                var email: String = et_Email.editText?.text?.toString() ?: ""
                var password: String = et_Password.editText?.text?.toString() ?: ""
                createUser(email, password)
            }
        }

        numberotpmove()
        dialog.show()
    }

    private fun numberotpmove() {
        et_otp1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    et_otp2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        et_otp2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    et_otp3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        et_otp3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    et_otp4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }*/

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            finish()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
        backPressedTime = System.currentTimeMillis()
    }

}