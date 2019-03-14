package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import org.json.JSONArray

class GetPointAsync : AsyncTask<Void, Void, JSONArray>() {

    private lateinit var onPointListener: OnPointListener

    private val httpConnectionUtil = HttpConnectionUtil()

    override fun doInBackground(vararg p0: Void?): JSONArray? {

        val response = httpConnectionUtil.requestGet(EmerConstants.URL + EmerConstants.GET_POINTS)

        return JSONArray(response)

    }

    override fun onPostExecute(result: JSONArray?) {
        super.onPostExecute(result)
        onPointListener.onPoints(result!!)
    }


    fun setOnPointListener(onPointListener: OnPointListener) {
        this.onPointListener = onPointListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }


}