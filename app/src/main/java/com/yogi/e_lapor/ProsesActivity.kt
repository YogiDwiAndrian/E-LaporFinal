package com.yogi.e_lapor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.ArrayList

class ProsesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: LaporAdapter? = null
    private lateinit var dataList: MutableList<DataLapor>
    private lateinit var dbLapor: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proses)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        dataList = ArrayList()
        adapter = LaporAdapter(this, dataList)
        recyclerView.setAdapter(adapter)

//        //1. SELECT * FROM data
        dbLapor = FirebaseDatabase.getInstance().getReference("data")

        //3. SELECT * FROM data WHERE status = ""
        val query = FirebaseDatabase.getInstance().getReference("data").orderByChild("status") .equalTo("Proses")
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataList.clear()
            if (dataSnapshot.exists()) {
                for (snapshot in dataSnapshot.children) {
                    val data = snapshot.getValue(DataLapor::class.java)!!
                    dataList .add(data)
                }
                adapter!!.notifyDataSetChanged()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }
}