package id.co.minumin.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns.DISPLAY_NAME
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Created by pertadima on 18,February,2021
 */
internal object DriveFileUtil {
    fun getPath(context: Context, uri: Uri): String {
        // check here to KITKAT or new version
        val isKitKat = true
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isGoogleDriveUri(uri)) return getDriveFilePath(uri, context)
        }
        return ""
    }

    fun isGoogleDriveUri(uri: Uri) = "com.google.android.apps.docs.storage" == uri.authority ||
            "com.google.android.apps.docs.storage.legacy" == uri.authority

    @SuppressLint("Recycle")
    private fun getDriveFilePath(
        uri: Uri,
        context: Context
    ): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val file = File(context.cacheDir, name)
        try {
            val inputStream =
                context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * BYTE_ARRAY * BYTE_ARRAY
            val bytesAvailable = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }

    private const val BYTE_ARRAY = 1024
}