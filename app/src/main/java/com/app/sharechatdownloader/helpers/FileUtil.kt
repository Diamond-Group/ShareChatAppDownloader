package com.app.sharechatdownloader.helpers

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.app.sharechatdownloader.model.Video
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File


object FileUtil {

    var downloadId: Long? = null

    //message variable for displaying toast in fragment
    var message: String? = "downloading..."

    @SuppressLint("Range")
     fun saveFilesToLocalStorage(videoUrl: String, context: Context): String? {
            if (videoUrl != "") {
                //downloadmanager start
                val fileName = "Sharechat_" + System.currentTimeMillis() + ".mp4"
                val uri = Uri.parse(videoUrl)
                val request = DownloadManager.Request(uri)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true).setTitle("Some name")
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("Downloading file")
                    .setTitle(fileName)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        File.separator + "Sharechat Video" + File.separator + fileName
                    )

                val mgr = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadId = mgr.enqueue(request)

                // using query method
                downloadId?.let {

                    var finishDownload:Boolean = false
                    var progress: Int
                    while (!finishDownload) {
                        val cursor: Cursor =
                            mgr.query(DownloadManager.Query().setFilterById(downloadId!!))
                        if (cursor.moveToFirst()) {
                            var status: Int =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            when (status) {
                                DownloadManager.STATUS_FAILED -> {
                                    finishDownload = true
                                    break
                                }
                                DownloadManager.STATUS_PAUSED -> {break}
                                DownloadManager.STATUS_PENDING -> {break}
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
                                    break
                                }
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    progress = 100
                                    // if you use aysnc task  publishProgress(100);
                                    finishDownload = true
                                    message = "Download Completed"
                                    break
                                }
                            }
                        }
                    }
                }

                //downloadmanager ends
            } else {
                message = "Download Failed"
            }


        return message
    }


    @SuppressLint("Range")
    fun getAllVideos(context: Context): ArrayList<Video> {
        val tempList = ArrayList<Video>()

        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION
        )

        val selection = MediaStore.Video.Media.DATA + " like?"
        val selectionArgs = arrayOf("%Sharechat Video%")

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, MediaStore.Video.Media.DATE_ADDED + " DESC"
        )

        if (cursor != null)
            if (cursor.moveToNext()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val durationC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                            .toLong()

                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val video = Video(
                            title = titleC,
                            id = idC,
                            //folderName = folderC,
                            duration = durationC,
                            size = sizeC,
                            path = pathC,
                            artUri = Uri.parse(pathC),
                            thumbnailUri = artUriC
                        )
                        tempList.add(video)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } while (cursor.moveToNext())
                cursor.close()
            }
        return tempList
    }

    //new list
    fun newList(context: Context): ArrayList<Video> {
        var newList: ArrayList<Video>? = getAllVideos(context)
        /*  if (newList != null) {
              for (index in 0..newList.size - 1) {
                  Log.d(TAG, "New list is ${newList[index].artUri}")
              }
          } else {
              Log.d(TAG, "New List is empty ")
          }*/
        return newList!!
    }

    @SuppressLint("Range")
    public fun saveFilesToLocalStorageBelow10(videoUrl: String, context: Context): String? {
        if (videoUrl != "") {
            //downloadmanager start
                val fileName = "Sharechat_" + System.currentTimeMillis() + ".mp4"
                val uri = Uri.parse(videoUrl)
                val request = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true).setTitle("Some name")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Downloading file")
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator + "Sharechat Videox" + File.separator + fileName
                )

                // DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE).enqueue(request);
                val mgr = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                mgr.enqueue(request)


            //downloadmanager ends
        } else {
            message = "Download Failed"
        }

        return message

    }

}