package com.blinkitcloneuser.utils


import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.blinkitcloneuser.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {
    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String) {
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.dismiss()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var firebaseAuthInstance : FirebaseAuth? = null
    fun getAuthInstance(): FirebaseAuth {
        if(firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getCurrentUserId() : String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun setStatusBarColor(context: Context, color: Int, activity: Activity) {
        activity.window?.apply {
            val statusBarColors = ContextCompat.getColor(context, color)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

        }
    }

    fun getRandomId(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..25).map {
            chars.random()
        }.joinToString("")
    }

    fun getCurrentData(): String? {
        val currentData = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentData.format(formatter)
    }
}