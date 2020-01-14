package com.yogi.e_lapor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaporAdapter (private val mCtx: Context, private val dataList: List<DataLapor>) : RecyclerView.Adapter<LaporAdapter.DataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_lapor, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = dataList[position]
        holder.tvKategori.text = data.kategori
        holder.tvJudul.text = data.judul
        holder.tvIsi.text = data.isi
        holder.tvLatLong.text = data.latitude + "/" + data.longitude
        holder.tvTimestamp.text = data.timestamp
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvKategori: TextView = itemView.findViewById(R.id.tv_kategori)
        var tvJudul: TextView = itemView.findViewById(R.id.tv_judul)
        var tvIsi: TextView = itemView.findViewById(R.id.tv_isi)
        var tvLatLong: TextView = itemView.findViewById(R.id.tv_latlong)
        var tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
    }

}