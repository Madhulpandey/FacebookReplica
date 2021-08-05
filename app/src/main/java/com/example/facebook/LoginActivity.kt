package com.example.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etPwd
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.etMail
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    /////THREAD

    fun buLoginEvent(view: View?) {
        //user
        val url="http://192.168.0.105/Login.php?email="+etMail.text.toString()+"&password="+ etPwd.text.toString()
        val thread = Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                try{
                    var response = client.newCall(request)
                    Log.d("OKHTTP TRY UP", response.toString())
                    response.execute()
                    val responseBody = response.toString()
                    Log.d("OKHTTP TRY UP 2", responseBody)

                    val url= URL(url)

                    val urlConnect=url.openConnection() as HttpURLConnection
                    urlConnect.connectTimeout=7000

                    var inString= ConvertStreamToString(urlConnect.inputStream)
//                    var json=JSONObject(inString)

                    if(inString.contains("pass login")){

                        Log.d("Checking", inString)
                        var parts=inString.split("\"");
                        var indexName=parts.indexOf("first_name")
                        var first_name=parts.get(indexName+2)
                        var indexUserID=parts.indexOf("user_id")
                        var user_id=parts.get(indexUserID+2)
                        Log.d("Checkingggg", first_name)
                        val saveSettings=SaveSettings(applicationContext)
                        Log.d("USERID-1", "buLoginEvent: "+user_id)
                        saveSettings.saveSettings(user_id)
                        finish()
                    }else{
                        Log.d("Checkingggg", "cannot login")
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

    fun buRegisterUserEvent(view: View) {
        var intent= Intent(this,RegisterActivity::class.java)
        startActivity(intent)

    }

    fun ConvertStreamToString(inputStream: InputStream):String{

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