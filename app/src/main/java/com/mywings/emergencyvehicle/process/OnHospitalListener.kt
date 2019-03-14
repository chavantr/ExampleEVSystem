package com.mywings.emergencyvehicle.process

import com.mywings.emergencyvehicle.models.Hospital

interface OnHospitalListener {
    fun onHospitalSuccess(result: List<Hospital>?)
}