package com.example.quizgame.CheckInternet

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class networkChnage : BroadcastReceiver() {
    var conmmon = common()
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!conmmon.checkConnectivity(context)) {
//            var alert=AlertDialog.Builder(context)
            Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }
}