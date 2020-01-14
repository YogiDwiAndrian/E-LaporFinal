package com.yogi.e_lapor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lapor_menu.setOnClickListener{
            val intent = Intent(this, LaporActivity::class.java)
            startActivity(intent)
        }
        pending_menu.setOnClickListener{
            val intent = Intent(this, PendingActivity::class.java)
            startActivity(intent)
        }
        proses_menu.setOnClickListener{
            val intent = Intent(this, ProsesActivity::class.java)
            startActivity(intent)
        }
    }
}
