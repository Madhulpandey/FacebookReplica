package com.example.facebook

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {

    var mAuth:FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        IVuserImage.setOnClickListener {
            checkPermission()
        }
        mAuth= FirebaseAuth.getInstance()
       signInAnonymously()
    }

    fun buRegisterEvent(view: View) {
        BUregister.isEnabled=false
        SaveImageInFirebase()
    }

    fun signInAnonymously(){
        mAuth!!.signInAnonymously().addOnCompleteListener(this) { task ->
            Log.d("login info", task.isSuccessful.toString())
        }
    }

    val READIMAGE:Int=253
    fun checkPermission(){

        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf( android.Manifest.permission.READ_EXTERNAL_STORAGE),READIMAGE)
                return
            }
        }

        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            READIMAGE->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }else{
                    Toast.makeText(applicationContext,"Cannot access your images",Toast.LENGTH_LONG).show()
                }
            }
            else-> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }


    }

    val PICK_IMAGE_CODE=123
    fun loadImage(){

        var intent= Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==PICK_IMAGE_CODE  && data!=null && resultCode == RESULT_OK){

            val selectedImage=data.data
            val filePathColum= arrayOf(MediaStore.Images.Media.DATA)
            val cursor= contentResolver.query(selectedImage!!,filePathColum,null,null,null)
            cursor!!.moveToFirst()
            val coulomIndex=cursor.getColumnIndex(filePathColum[0])
            val picturePath=cursor.getString(coulomIndex)
            cursor.close()

            IVuserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))

        }

    }

    fun SaveImageInFirebase(){
        var currentUser =mAuth!!.currentUser
        val email:String=currentUser!!.email.toString()
        val storage= FirebaseStorage.getInstance()
        val storgaRef=storage.getReferenceFromUrl("gs://facebookrep-1fc16.appspot.com")
        val df= SimpleDateFormat("ddMMyyHHmmss")
        val dataobj= Date()
        val imagePath= SplitString(email) + "."+ df.format(dataobj)+ ".jpg"
        val ImageRef=storgaRef.child("images/"+imagePath )
        IVuserImage.isDrawingCacheEnabled=true
        IVuserImage.buildDrawingCache()

        val drawable=IVuserImage.drawable as BitmapDrawable
        val bitmap=drawable.bitmap
        val baos= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data= baos.toByteArray()
        val uploadTask=ImageRef.putBytes(data)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"fail to upload",Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->

            //var DownloadURL= taskSnapshot.storage.downloadUrl.toString()!!

            var ImageUrl:String?=null
            var DownloadURL = ImageRef.downloadUrl.addOnCompleteListener { task->

                ImageUrl = task.result.toString()
                val name= URLEncoder.encode(etName.text.toString(),"utf-8")
                ImageUrl=URLEncoder.encode(ImageUrl,"utf-8")
                val url="http://192.168.0.105/register.php?first_name="+etName.text+"&email="+etMail.text+"&pword="+etPwd.text+"&picture_path="+ImageUrl

                val thread = Thread {
                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(url)
                            .get()
                            .build()
                        try{
                            var response = client.newCall(request)
                            //Log.d("OKHTTP TRY UP", response.toString())
                            response.execute()
                            val responseBody = response.toString()
                            Log.d("OKHTTP TRY UP 2", responseBody)

                            val url= URL(url)

                            val urlConnect=url.openConnection() as HttpURLConnection
                            urlConnect.connectTimeout=7000

                            var inString= ConvertStreamToString(urlConnect.inputStream)

                            if(inString.contains("user is added")){
                                //Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()
                                finish()
                            }else{
                                BUregister.isEnabled=true
                            }

                        }catch (ex:Exception){
                            Log.d("OKHTTP TRY", ex.toString())
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                thread.start()
            }
        } //TODO:REGISTER TO DB


    }

    fun SplitString(email:String):String{
        val split= email.split("@")
        return split[0]
    }
//ADDED THIS IN MANIFEST AND IMAGE WORKED
//android:requestLegacyExternalStorage="true"

    //CALL HTTP



//    inner class MyAsyncTask: AsyncTask<String, String, String>() {
//
//        override fun onPreExecute() {
//            //Before task started
//        }
//        override fun doInBackground(vararg p0: String?): String {
//            try {
//
//                val url= URL(p0[0])
//
//                val urlConnect=url.openConnection() as HttpURLConnection
//                urlConnect.connectTimeout=7000
//
//                var inString= ConvertStreamToString(urlConnect.inputStream)
//                //Cannot access to ui
//                publishProgress(inString)
//            }catch (ex:Exception){}
//
//
//            return " "
//
//        }
//
//        override fun onProgressUpdate(vararg values: String?) {
//            try{
//                var json= JSONObject(values[0])
//
//                if(json.getString("msg")=="user is added"){
//                    Toast.makeText(applicationContext, json.getString("msg"), Toast.LENGTH_SHORT).show()
//                }else{
//                    finish()
//                }
//
//            }catch (ex:Exception){}
//        }
//
//        override fun onPostExecute(result: String?) {
//            //after task done
//        }
//
//
//    }


    fun ConvertStreamToString(inputStream:InputStream):String{

        val bufferReader= BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String=""

        try {
            do{
                line=bufferReader.readLine()
                if(line!=null){
                    AllString+=line
                }
            }while (line!=null)
            inputStream.close()
        }catch (ex:Exception){}



        return AllString
    }

}