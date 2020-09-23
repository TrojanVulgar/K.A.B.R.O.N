package com.rifcode.holalapremium.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;


public class DialogUtils {




        public static AlertDialog.Builder CustomAlertDialog(View view, Activity activity){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setView(view);

            return alertDialogBuilder;
        }


}
