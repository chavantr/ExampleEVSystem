package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import org.json.JSONObject

class UpdateLocationAsync : AsyncTask<JSONObject, Void, String>() {

    private val httpConnectionUtil = HttpConnectionUtil()
    private lateinit var onUpdateLocationListener: OnUpdateLocationListener
    override fun doInBackground(vararg params: JSONObject?): String {
        return httpConnectionUtil.requestPost("", params[0]);
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onUpdateLocationListener.onLocationUpdateSuccess(result!!)
    }

    fun setOnUpdateLocationListener(onUpdateLocation: OnUpdateLocationListener, request: JSONObject) {
        this.onUpdateLocationListener = onUpdateLocation
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }
}