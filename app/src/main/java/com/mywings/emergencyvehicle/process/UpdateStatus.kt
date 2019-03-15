package com.mywings.emergencyvehicle.process

import android.os.AsyncTask

class UpdateStatus : AsyncTask<Int, Void, String?>() {

    override fun doInBackground(vararg params: Int?): String? {
        return HttpConnectionUtil().requestGet(EmerConstants.URL + EmerConstants.UPDATE_STATUS + "?id=${params[0]}")
    }

}