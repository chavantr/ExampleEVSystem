package com.mywings.emergencyvehicle.joint

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mywings.emergencyvehicle.R
import com.mywings.emergencyvehicle.models.Hospital
import kotlinx.android.synthetic.main.layout_hospital_row.view.*

class HospitalAdapter(lst: List<Hospital>?) : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    var lstHospitals = lst

    private lateinit var onHospitalSelectListener: OnHospitalSelectListener

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): HospitalViewHolder {
        return HospitalViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_hospital_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = lstHospitals!!.size

    override fun onBindViewHolder(viewHolder: HospitalViewHolder, position: Int) {

        viewHolder.lblName.text = lstHospitals!![position].name

        viewHolder.lblName.setOnClickListener {
            onHospitalSelectListener.onHospitalSelected(lstHospitals!![position])
        }

    }

    fun setOnHospitalListener(onHospitalSelectListener: OnHospitalSelectListener) {
        this.onHospitalSelectListener = onHospitalSelectListener
    }


    inner class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblName = itemView.lblName
    }

}