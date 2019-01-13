package com.mywings.emergencyvehicle.process

import org.json.JSONArray

interface OnPointListener {
    fun onPoints(result: JSONArray)
}