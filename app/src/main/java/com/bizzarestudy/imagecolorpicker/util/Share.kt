package com.bizzarestudy.imagecolorpicker.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

class Share(
    private val context: Context
) {

    fun shareFile(
        fileUri: Uri?,
        text: String,
        subject: String
    ) {
        val shareIntent = Intent().apply { 
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooserIntent = Intent.createChooser(shareIntent, null)
        val resInfoList = context.packageManager.queryIntentActivities(
            chooserIntent, PackageManager.MATCH_DEFAULT_ONLY
        )
        resInfoList.forEach { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
        startActivity(chooserIntent)
    }

    private fun startActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}