package com.mywings.emergencyvehicle.process;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {

    public ProgressDialogUtil(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private ProgressDialog progressDialog;

    public void show() {
        progressDialog.show();
    }


    public void show(String light) {
        progressDialog.setMessage("Please wait \n" + "Updating light : " + light);
        progressDialog.show();
    }

    public void show(String direction, int type) {
        progressDialog.setMessage("Please wait \n" + "Updating direction : " + direction);
        progressDialog.show();
    }


    public void hide() {
        progressDialog.hide();
    }

}
