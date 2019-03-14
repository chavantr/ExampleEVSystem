package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import org.json.JSONObject

class UpdateLightAsync : AsyncTask<JSONObject, Void, String>() {

    private val httpConnectionUtil = HttpConnectionUtil()
    private lateinit var onUpdateLightListener: OnUpdateLightListener

    override fun doInBackground(vararg params: JSONObject?): String {
        return httpConnectionUtil.requestPost("", params[0])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onUpdateLightListener.onUpdateLight(result!!)
    }

    fun setOnUpdateLightListener(onUpdateLightListener: OnUpdateLightListener, request: JSONObject) {
        this.onUpdateLightListener = onUpdateLightListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }

}