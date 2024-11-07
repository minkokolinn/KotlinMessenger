package com.example.user.kotlinmessenger

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var fbauth:FirebaseAuth
    var selectedPhotoUri:Uri?=null
    lateinit var fbst:FirebaseStorage
    lateinit var fbdb:FirebaseDatabase
    lateinit var userref:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fbauth= FirebaseAuth.getInstance()
        fbst= FirebaseStorage.getInstance()
        fbdb= FirebaseDatabase.getInstance()
        userref=fbdb.getReference("user")

        tv_login_am.setOnClickListener {
            finish()
        }

        btn_register_am.setOnClickListener {
            var name=et_name_am.text.toString()
            var email=et_email_am.text.toString()
            var pass=et_pass_am.text.toString()
            var cpass=et_cpass_am.text.toString()
            if (name.isEmpty()||email.isEmpty()||pass.isEmpty()||cpass.isEmpty()){
                var ab=AlertDialog.Builder(this)
                ab.setTitle("Error!!!")
                ab.setMessage("Fields shouldn't be empty!!!")
                ab.setPositiveButton("OK",object :DialogInterface.OnClickListener{
                    override fun onClick(dialoginterface: DialogInterface?, p1: Int) {
                        dialoginterface!!.dismiss()
                    }
                })
                ab.show()
            }
            else if (selectedPhotoUri==null){
                var ab=AlertDialog.Builder(this)
                ab.setTitle("Error")
                ab.setMessage("Please select your profile picture")
                ab.setPositiveButton("OK",object :DialogInterface.OnClickListener{
                    override fun onClick(dialoginterface: DialogInterface?, p1: Int) {
                        dialoginterface!!.dismiss()
                    }
                })
                ab.show()
            }
            else if (pass!=cpass){
                var ab=AlertDialog.Builder(this)
                ab.setTitle("Error!!!")
                ab.setMessage("Password and Comfirm password are not same!!\nThey must be same.........\nPlease try again!")
                ab.setPositiveButton("OK",object :DialogInterface.OnClickListener{
                    override fun onClick(dialoginterface: DialogInterface?, p1: Int) {
                        dialoginterface!!.dismiss()
                    }
                })
                ab.show()
            }else{
                fbauth.createUserWithEmailAndPassword(email,cpass)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                e("MainActivity",it.result.user.uid)
                                registertheuser()
                                Toast.makeText(this@MainActivity,"Your account '"+it.result.user.email+"' is successfully registered",Toast.LENGTH_LONG).show()
                                finish()
                        }
                        .addOnFailureListener {
                            var ab=AlertDialog.Builder(this)
                            ab.setTitle("Registration Error!!!")
                            ab.setMessage(it.message+"\nPlease try again!!!")
                            ab.setPositiveButton("Try Again",object :DialogInterface.OnClickListener{
                                override fun onClick(dialoginterface2: DialogInterface?, p1: Int) {
                                    dialoginterface2!!.dismiss()
                                }
                            })
                            ab.show()
                            Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_LONG).show()
                        }

            }
        }

        btn_select_photo_am.setOnClickListener {
            var intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }

    private fun registertheuser(){
        if (selectedPhotoUri==null) return

        var filename=UUID.randomUUID().toString()
        var ref=fbst.getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("Main","Successfully uploaded image : ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {

                    }
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==0&&resultCode == Activity.RESULT_OK&&data!=null){
            selectedPhotoUri=data.data
            var bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            civ_profile_am.setImageBitmap(bitmap)
            btn_select_photo_am.alpha=0f
            btn_select_photo_am.text = ""
        }
    }
}
