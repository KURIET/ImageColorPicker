package com.bizzarestudy.imagecolorpicker.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class Share(
    private val context: Context
) {
    private val providerAuthority: String by lazy {
        context.packageName + ".flutter.share_provider"
    }

    private val shareCacheFolder: File
        get() = File(context.cacheDir, "share_plus")
    
    fun shareFile(
        fileUri: Uri?,
        text: String,
        subject: String
    ) {
        clearShareCacheFolder()
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
    private fun getUriForPath(path: String): Uri? {
        var file = File(path)
        if (fileIsInShareCache(file)) {
            // If file is saved in '.../caches/share_plus' it will be erased by 'clearShareCacheFolder()'
            throw IOException("Shared file can not be located in '${shareCacheFolder.canonicalPath}'")
        }
        file = copyToShareCacheFolder(file)
        return FileProvider.getUriForFile(context, providerAuthority, file)
    }

    private fun fileIsInShareCache(file: File): Boolean {
        return try {
            val filePath = file.canonicalPath
            filePath.startsWith(shareCacheFolder.canonicalPath)
        } catch (e: IOException) {
            false
        }
    }

    @Throws(IOException::class)
    private fun copyToShareCacheFolder(file: File): File {
        val folder = shareCacheFolder
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val newFile = File(folder, file.name)
        file.copyTo(newFile, true)
        return newFile
    }

}