package com.bizzarestudy.imagecolorpicker.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Share(
    private val context: Context
) {

    private val providerAuthority: String by lazy {
        context.packageName
    }

    private val shareCacheFolder: File
        get() = File(context.cacheDir, "color_picker")

    fun shareFile(
        bitmap: Bitmap,
        text: String,
        subject: String
    ) {
        clearShareCacheFolder()
        val fileUri = getUriForPath(bitmap)

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

    private fun clearShareCacheFolder() {
        val folder = shareCacheFolder
        val files = folder.listFiles()
        if (folder.exists() && !files.isNullOrEmpty()) {
            files.forEach { it.delete() }
            folder.delete()
        }
    }

    @Throws(IOException::class)
    private fun getUriForPath(bitmap: Bitmap): Uri? {
        val file = getCacheFile(bitmap)
        return FileProvider.getUriForFile(context, providerAuthority, file)
    }

    private fun getCacheFile(bitmap: Bitmap): File {
        val myDir = File(shareCacheFolder.path).apply { mkdirs() }
        val file = File(myDir, "temp.png")
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

}