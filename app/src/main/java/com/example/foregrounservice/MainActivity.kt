package com.example.foregrounservice

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import com.example.foregrounservice.Model.song

class MainActivity : AppCompatActivity() {
    lateinit var start:Button
    lateinit var stop:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start=findViewById(R.id.start_service)
        stop=findViewById(R.id.stop_service)

        start.setOnClickListener{
          //  var song= song("thang tu","anh tuan",R.raw.file_music,R.drawable.ic_launcher_foreground);
            val intent=Intent(this@MainActivity,Myservice::class.java)
//           val bundle= Bundle();
//            bundle.putSerializable("key_song",song)
//            intent.putExtras(bundle)
           startService(intent)



        }
        stop.setOnClickListener{
            stopService(Intent(this@MainActivity,Myservice::class.java))
        }
    }
}