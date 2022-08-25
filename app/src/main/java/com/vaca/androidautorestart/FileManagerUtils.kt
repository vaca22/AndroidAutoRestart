package com.vaca.androidautorestart

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 创建日期：2021/3/10 17:32
 * @author HelloWord
 * 包名： com.viatomtech.o2smart.tool
 * 类说明：
 */
class FileManagerUtils {

    companion object {
        const val FILE_NAME = "GaGa/"
        /**
         * 创建文件夹
         */
        fun getInitFile(mContext: Context): File {
            val file: File?
            //是否安卓10
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //目录类型【/storage/emulated/0/Android/data/com.viatom.vihealth/files】
                val dirpath = mContext.getExternalFilesDir("")//外部私有目录 (随着用户删除app而删除)
                //专有目录存储
                //val fileString = dirpath.toString() + File.separator
                file = File(dirpath.toString() + File.separator)
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e("this", "创建文件失败 大于10")
                    }
                }
            } else {
                //【/storage/emulated/0/ViHealth】
                var rootPath = Environment.getExternalStorageDirectory()
                if (rootPath == null) {
                    rootPath = Environment.getDataDirectory()
                }
                file = File(rootPath, FILE_NAME)
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e("this", "创建文件失败 小于10")
                    }
                }
            }
            Log.e("this", "创建文件成功：$file")
            return file
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun getLogFile(mContext: Context, mAppMainLogFolder: String, mCrashInfoFolder: String, crashLogFileName: String): Uri? {
            //是否安卓10
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val resolver = mContext.contentResolver
//                val values = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, crashLogFileName)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" +
//                            FILE_NAME + mAppMainLogFolder + "/" + mCrashInfoFolder)     //end "/" is not mandatory
//                }
//                return resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
//            } else {
//                val resolver = mContext.contentResolver
//                val values = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, crashLogFileName)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" +
//                            FILE_NAME + mAppMainLogFolder + "/" + mCrashInfoFolder)     //end "/" is not mandatory
//                }
//                return resolver.insert(MediaStore.Files.getContentUri("external"), values)
//            }

//            return File(Environment.DIRECTORY_DOCUMENTS + "/" +
//                    FILE_NAME + mAppMainLogFolder + "/" + mCrashInfoFolder, crashLogFileName)
            val resolver = mContext.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, crashLogFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" +
                        FILE_NAME + mAppMainLogFolder + "/" + mCrashInfoFolder)     //end "/" is not mandatory
            }
            return resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
        }

//        /**
//         * 共有目录
//         */
//        fun getPublicInitFile(): File {
//            val file: File?
//            //【/storage/emulated/0/ViHealth】
//            var rootPath = Environment.getExternalStorageDirectory()
//            if (rootPath == null) {
//                rootPath = Environment.getDataDirectory()
//            }
//            file = File(rootPath, FILE_NAME)
//            if (!file.exists()) {
//                if (!file.mkdirs()) {
//                    LogUtils.e(this, "创建文件失败")
//                }
//            }
//            LogUtils.e(this, "创建文件成功：$file")
//            return file
//        }

        /**
         * 写数据本地文件夹
         */
        fun writeFile(dir: File, dirName: String, deviceName: String, data: ByteArray) {
            if (dir == null || dirName == null || data == null) {
                Log.e("this", "写入文件失败")
                return
            }
            val fileName = deviceName + "_" + dirName
            if (!dir.exists()) {
                dir.mkdir()
            }
            if (!dir.isDirectory) {
                Log.e("this", "不是一个目录写入文件失败")
                return
            }
            val file = File(dir, fileName)
            try {
                val fos = FileOutputStream(file)
                fos.write(data)
                fos.close()
                Log.e("this", "写入文件成功：$fileName")
            } catch (e: Exception) {
                Log.e("this", "写入文件异常")
            }
        }

        /**
         * 读取本地文件 返回 bytes
         */
        fun readFile(dir: File?, fileName: String?): ByteArray? {
            if (dir == null || fileName == null) {
                Log.e("this", "读取文件失败")
                return null
            }
            if (!dir.isDirectory) {
                Log.e("this", "不是一个目录读取文件失败")
                return null
            }
            val file = File(dir, fileName)
            Log.e("this", "read file：$file")
            if (!file.exists()) {
                Log.e("this", "文件不存在：$fileName")
                return null
            }
            try {
                val fis = FileInputStream(file)
                val tempBuffer = ByteArray(file.length().toInt())
                fis.read(tempBuffer)
                fis.close()
                return tempBuffer
            } catch (e: Exception) {
            }
            return null
        }

        /**
         * 检查文件夹是否存在
         */
        fun isFileExist(context: Context, fileName: String): Boolean {
            val file = getInitFile(context)
            return if (file == null || fileName == null || !file.isDirectory) {
                false
            } else File(file, fileName).exists()
        }

        /**
         * 删除文件夹中的所有文件
         */
        fun deleteAllInfo(dir: File?) {
            if (dir == null || !dir.exists() || !dir.isDirectory) {
                return
            }
            Log.e("this", "删除所有文件夹：$dir")
            val files = dir.listFiles() ?: return
            for (i in files.indices) files[i].delete()
        }

        /**
         * 删除一个文件
         */
        fun delFile(context: Context, fileName: String) {
            val dir = getInitFile(context)
            if (dir == null || fileName == null || !dir.isDirectory) {
                Log.e("this", "删除文件失败：")
                return
            }
            val file = File(dir, fileName)
            if (file.exists()) {
                Log.e("this", "删除文件：$fileName")
                file.delete()
            }
        }
    }
}