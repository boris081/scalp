package com.uuuuk.scalp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.uuuuk.scalp.Camera.CameraActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.*
import java.net.URL


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            super.recreate()
        }

//        picButton.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/jpeg"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(intent, 1)
//        }
        picButton.setOnClickListener {
            val intent = Intent()
            intent.setClass(this,CameraActivity::class.java)
            startActivity(intent)
        }

        dataButton.setOnClickListener {
            val intent = Intent()
            intent.setClass(this,DataActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) { //取得圖檔的路徑位置
            val uri: Uri? = data?.data
            val file= tofile().getFilePathByUri(this, uri!!).toString()
            try {

                val body= MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image1.jpg", File(file).asRequestBody(null)).build()
                val request= Request.Builder()
                    .url("http://ef111768.ngrok.io/test/")
                    .post(body)
                    .build()
                val call= OkHttpClient().newBuilder().build().newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("get ", e.toString())
                    }
                    override fun onResponse(call: Call, response: Response) {
                        val res=JSONObject(response.body!!.string())
                        runOnUiThread{
                            DownloadImageTask(imageView).execute("http://ef111768.ngrok.io/output/image1.jpg")
                            textView2.text=res.getJSONObject("respond").getString("result")
                        }
                    }
                })
            } catch (e: FileNotFoundException) {
                Log.e("Exception", e.message.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    class DownloadImageTask(private val bmImage: ImageView) : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            val urldisplay = params[0]
            var mIcon11: Bitmap? = null
            try {
                val `in`: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
        }
    }

}
