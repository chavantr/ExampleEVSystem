package com.mywings.emergencyvehicle.joint

import com.mywings.emergencyvehicle.models.Hospital

interface OnHospitalSelectListener {
    fun onHospitalSelected(selected: Hospital?)
}