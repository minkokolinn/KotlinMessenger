package com.example.user.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {
    lateinit var fbauth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        fbauth= FirebaseAuth.getInstance()

        tv_reg_la.setOnClickListener {
            var i=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(i)
        }

        btn_login_la.setOnClickListener {
            var email=et_email_la.text.toString()
            var pass=et_pass_la.text.toString()
        }

    }
}