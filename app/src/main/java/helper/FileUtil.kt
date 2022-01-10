package helper

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File



object FileUtil {



    var downloadId: Long? = null
    //message variable for displaying toast in fragment
    var message : String? = null
    public fun saveFilesToLocalStorage(videoUrl: String, context: Context): String? {



        GlobalScope.async(Dispatchers.IO) {

            videoUrl?.let{

            }

            if (videoUrl != "") {

                /*  Util.download(videoUrl, Util.RootDirectoryForShareChat, ShareChatActivity.this,
                       "Sharechat " + System.currentTimeMillis() + ".mp4");*/

                //downloadmanager start
                val fileName = "Sharechat" + System.currentTimeMillis() + ".mp4"

                val uri = Uri.parse(videoUrl)
                val request = DownloadManager.Request(uri)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true).setTitle("Some name")
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("Downloading file")
                    .setTitle(fileName)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        File.separator + "Sharechat Video" + File.separator + fileName
                    )

                val mgr = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadId = mgr.enqueue(request)


                //adding code

                // using query method

                // using query method
                downloadId?.let {

                    var finishDownload = false
                    var progress: Int
                    while (!finishDownload) {
                        val cursor: Cursor =
                            mgr.query(DownloadManager.Query().setFilterById(downloadId!!))
                        if (cursor.moveToFirst()) {
                            val status: Int =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            when (status) {
                                DownloadManager.STATUS_FAILED -> {
                                    finishDownload = true
                                }
                                DownloadManager.STATUS_PAUSED -> {}
                                DownloadManager.STATUS_PENDING -> {}
                                DownloadManager.STATUS_RUNNING -> {
                                    val total: Long =
                                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                    if (total >= 0) {
                                        val downloaded: Long =
                                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                        progress = (downloaded * 100L / total).toInt()
                                        // if you use downloadmanger in async task, here you can use like this to display progress.
                                        // Don't forget to do the division in long to get more digits rather than double.
                                        //  publishProgress((int) ((downloaded * 100L) / total));
                                    }
                                }
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    progress = 100
                                    // if you use aysnc task  publishProgress(100);
                                    finishDownload = true
                                    message = "Download Completed"
                                }
                            }
                        }
                    }
                }

                //downloadmanager ends
            } else {
                message = "Download Failed"
            }
        }

        return message
    }


}