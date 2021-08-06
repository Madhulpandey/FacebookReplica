package com.example.facebook
//            val thread = Thread {
//                    try {
//                        val client = OkHttpClient()
//                        val request = Request.Builder()
//                            .url(url)
//                            .get()
//                            .build()
//                        try{
//                            var response = client.newCall(request)
//                            //Log.d("OKHTTP TRY UP", response.toString())
//                            response.execute()
//                            val responseBody = response.toString()
//                            Log.d("OKHTTP TRY UP 2", responseBody)
//
//                            val url= URL(url)
//
//                            val urlConnect=url.openConnection() as HttpURLConnection
//                            urlConnect.connectTimeout=7000
//
//                            var inString= ConvertStreamToString(urlConnect.inputStream)
//                            Toast.makeText(this, inString, Toast.LENGTH_SHORT).show()
//                            if(inString.contains("tweet is added")){
//
//                            }else{
//                                BUregister.isEnabled=true
//                            }
//
//                        }catch (ex:Exception){
//                            Log.d("OKHTTP TRY", ex.toString())
//                        }
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                thread.start()
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import kotlinx.android.synthetic.main.posts_ticket.view.*
import org.json.JSONArray
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
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var ListTweets=ArrayList<Ticket>()
    var adpater:MyTweetAdpater?=null
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val saveSettings=SaveSettings(this)
        saveSettings.loadSettings()

        //set adapter
        adpater= MyTweetAdpater(this,ListTweets)
        lvTweets.adapter=adpater

            //TODO remove for DEBUG
//        ListTweets.clear()
//        ListTweets.add(Ticket("0","him","url","add"))
//        adpater!!.notifyDataSetChanged()
        SearchInDatabase("%",0)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        val sv: SearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        val sm= getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                // TODO SEARCH IN DB LoadQuery("%$query%")
                SearchInDatabase(query,0)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


        return super.onCreateOptionsMenu(menu)
    }

    override  fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homePage->{
                //TODO GO HOME
                //http://192.168.0.104/Twistlist.php?op=3&query=new&StartFrom=0
                SearchInDatabase("%",0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun SearchInDatabase(SearchText:String,startFrom:Int){
        val text= URLEncoder.encode(SearchText,"utf-8")
        DownloadURL= URLEncoder.encode(DownloadURL,"utf-8")

        val url="http://192.168.0.105/Tweetlist.php?op=3&query="+text+"&StartFrom="+startFrom
        Log.d("MADHUL", "SearchInDatabase: "+url)
        MyAsyncTask().execute(url)
    }


    inner class  MyTweetAdpater: BaseAdapter {
        var listNotesAdpater=ArrayList<Ticket>()
        var context: Context?=null
        constructor(context:Context, listNotesAdpater:ArrayList<Ticket>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var mytweet=listNotesAdpater[p0]

            if(mytweet.tweetDate.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)

                myView.iv_attach.setOnClickListener {
                    loadImage()
                }
                myView.iv_post.setOnClickListener {
                    ListTweets.add(0,Ticket("0","him","url","loading","","","0"))
                    adpater!!.notifyDataSetChanged()

                    //CALL HTTP
                    val post_text= URLEncoder.encode(myView.etPost.text.toString(),"utf-8")
                    DownloadURL= URLEncoder.encode(DownloadURL,"utf-8")

                    val url="http://192.168.0.105/Posts.php?user_id="+SaveSettings.userID +"&post_text="+post_text+"&post_picture="+DownloadURL
                    MyAsyncTask().execute(url)
                    myView.etPost.setText("")

                }
                return myView
            } else if(mytweet.tweetDate.equals("loading")){
                var myView=layoutInflater.inflate(R.layout.loading_ticket,null)
                return myView
            } else{
                var myView=layoutInflater.inflate(R.layout.posts_ticket,null)
                myView.txt_tweet.text = mytweet.tweetText
                //myView.txt_tweet_date.text=mytweet.tweetDate
                /////////////////////////////////////////////////////////
                Log.d("MANAC TRIAL-1", "getView: "+mytweet.tweetDate)
                Picasso.get().load(mytweet.tweetImageURL).into(myView.tweet_picture)
                Log.d("PICTURE LOAD", "getView: "+mytweet.tweetImageURL)
//                TODO SHOW USERNAME AND IMAGE
                Picasso.get().load(mytweet.personImage).into(myView.picture_path)
                Log.d("PICTURE LOAD", "getView: "+mytweet.personImage)
                myView.txtUserName.text = mytweet.personName
                myView.txtUserName.setOnClickListener {
                    //http://192.168.0.105/Tweetlist.php?op=2&user_id=1&StartFrom=0
                    val url="http://192.168.0.105/Tweetlist.php?op=2&user_id="+mytweet.personID+"&StartFrom=0"
                    MyAsyncTask().execute(url)
                  //  myView.etPost.setText("")
                    //notifyDataSetChanged()
                    adpater!!.notifyDataSetChanged()
                }
                return myView
            }
        }
        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }
        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }
        override fun getCount(): Int {
            return listNotesAdpater.size
        }
    }
//
//    //load image

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
            Log.d("mac img", "onActivityResult: "+picturePath)
            UploadImage(BitmapFactory.decodeFile(picturePath))

            /*
            val filePathColum= arrayOf(MediaStore.Images.Media.DATA)
            val cursor= contentResolver.query(selectedImage!!,filePathColum,null,null,null)
            cursor!!.moveToFirst()
            val coulomIndex=cursor.getColumnIndex(filePathColum[0])
            val picturePath=cursor.getString(coulomIndex)
            cursor.close()
            Log.d("reac img", "onActivityResult: "+picturePath)
            IVuserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            */
        }

    }

    var DownloadURL:String?="noImage"
    fun UploadImage(bitmap: Bitmap){
        ListTweets.add(0,Ticket("0","him","url","loading","","","0"))
        adpater!!.notifyDataSetChanged()

        val storage= FirebaseStorage.getInstance()
        val storgaRef=storage.getReferenceFromUrl("gs://facebookrep-1fc16.appspot.com")
        val df= SimpleDateFormat("ddMMyyHHmmss")
        val dataobj= Date()
        val imagePath= SaveSettings.userID + "."+ df.format(dataobj)+ ".jpg"
        val ImageRef=storgaRef.child("imagePost/"+imagePath )
        val baos= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data= baos.toByteArray()
        val uploadTask=ImageRef.putBytes(data)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"fail to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->

            //DownloadURL= taskSnapshot.storage.downloadUrl.toString()!!
            //DownloadURL=ImageRef.downloadUrl.toString()
            ImageRef.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for 'users/me/profile.png' in uri
            DownloadURL=uri.toString()
            //    Log.d("IMAGELINK", "UploadImage: "+uri.toString())
            }.addOnFailureListener {
                // Handle any errors
            }

            ListTweets.removeAt(0)
            adpater!!.notifyDataSetChanged()

        }
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

    inner class MyAsyncTask: AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            //Before task started
        }
        override fun doInBackground(vararg p0: String?): String {
            try {

                val url= URL(p0[0])       ///p0[0]=link

                val urlConnect=url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000

                var inString= ConvertStreamToString(urlConnect.inputStream)
                //Cannot access to ui
                publishProgress(inString)
            }catch (ex:Exception){

            }
            return " "
        }
        override fun onProgressUpdate(vararg values: String?) {
            try{
                val json= JSONObject(values[0])        ///values[0] == inString
                //if(inString.contains("post is added")){
                Toast.makeText(applicationContext, json.getString("msg"), Toast.LENGTH_SHORT).show()
                ListTweets.clear()
                if(json.getString("msg")=="post is added"){
                    DownloadURL="noImage"
                    ListTweets.removeAt(0)
                    adpater!!.notifyDataSetChanged()
                }else if(json.getString("msg")=="has tweet"){
                    ListTweets.clear()
                    ListTweets.add(Ticket("0","him","url","add","","","0"))
                    val tweets = JSONArray(json.getString("info"))
                    for(i in 0..tweets.length()-1){
                        val tweet=tweets.getJSONObject(i)
                        ListTweets.add(Ticket(tweet.getString("post_id"),tweet.getString("post_text"),tweet.getString("post_picture"),tweet.getString("post_picture"),tweet.getString("first_name"),tweet.getString("picture_path"),tweet.getString("user_id")))
                    }
                }else if(json.getString("msg")=="no tweet"){
                    ListTweets.clear()
                    ListTweets.add(Ticket("0","him","url","add","","","0"))
                }
                adpater!!.notifyDataSetChanged()

            }catch (ex:Exception){}
        }
        override fun onPostExecute(result: String?) {
            //after task done
        }
    }

}