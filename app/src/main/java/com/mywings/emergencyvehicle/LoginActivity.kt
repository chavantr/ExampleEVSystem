package com.mywings.emergencyvehicle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mywings.emergencyvehicle.process.LoginAsync
import com.mywings.emergencyvehicle.process.OnLoginListener
import com.mywings.emergencyvehicle.process.ProgressDialogUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), OnLoginListener {


    private lateinit var progressDialogUtil: ProgressDialogUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressDialogUtil = ProgressDialogUtil(this)

        btnSignIn.setOnClickListener {
            if (validate()) {
                init()
            } else {
                Toast.makeText(this@LoginActivity, "Enter username and password", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun init() {
        progressDialogUtil.show()
        val loginAsync = LoginAsync()
        val jRequest = JSONObject()
        val param = JSONObject()
        param.put("Username", txtUserName.text)
        param.put("Password", txtPassword.text)
        jRequest.put("request", param)
        loginAsync.setOnLoginListener(this, jRequest)
    }

    override fun onLoginSuccess(result: String?) {
        progressDialogUtil.hide()
        if (result.equals("1", true)) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this@LoginActivity, "Enter valid username and password", Toast.LENGTH_LONG).show()
        }
    }

    private fun validate() = txtUserName.text!!.isNotEmpty() && txtPassword.text!!.isNotEmpty()
}
