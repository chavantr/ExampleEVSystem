package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import com.mywings.emergencyvehicle.models.Hospital
import org.json.JSONArray

class GetHospitalAsync : AsyncTask<Void, Void, List<Hospital>?>() {


    private lateinit var onHospitalListener: OnHospitalListener

    override fun doInBackground(vararg p0: Void?): List<Hospital>? {
        val response = HttpConnectionUtil().requestGet(EmerConstants.URL + EmerConstants.GET_HOSPITALS)
        val jHospitals = JSONArray(response)
        if (jHospitals.length() > 0) {
            var hospitals = ArrayList<Hospital>()
            for (i in 0..(jHospitals.length() - 1)) {
                val jNode = jHospitals.getJSONObject(i)
                var hospital = Hospital()
                hospital.id = jNode.getInt("Id")
                hospital.name = jNode.getString("Name")
                hospital.lat = jNode.getString("Lat")
                hospital.lng = jNode.getString("Lng")
                hospitals.add(hospital)

            }
            return hospitals
        }
        return null
    }

    override fun onPostExecute(result: List<Hospital>?) {
        super.onPostExecute(result)
        onHospitalListener.onHospitalSuccess(result)
    }

    fun setOnHospitalListener(onHospitalListener: OnHospitalListener) {
        this.onHospitalListener = onHospitalListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }
}