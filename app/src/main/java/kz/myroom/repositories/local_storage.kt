package kz.myroom.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.io.File
import org.apache.commons.io.FileUtils
import android.widget.Toast
import java.io.IOException


interface ILocalRepository {
    var isRegistered: Boolean
    fun createFileInDir(fileName: String): File
    fun getDataFromFile(fileName: String): String?
    fun clearCache(): Boolean
}

class LocalStorageImpl(
    private val context: Context,
    private val sharedPreference: SharedPreferences
) : ILocalRepository {

    companion object {
        const val IS_REGISTERED = "is_registered"
    }

    override var isRegistered: Boolean
        get() = sharedPreference.getBoolean(IS_REGISTERED, false)
        set(value) {
            sharedPreference.edit().putBoolean(IS_REGISTERED, value).apply()
        }

    override fun clearCache(): Boolean {
        val cacheDirectory = context.cacheDir
        val applicationDirectory = File(cacheDirectory.parent)
        if (applicationDirectory.exists()) {
            val fileNames = applicationDirectory.list()
            for (fileName in fileNames) {
                if (fileName != "lib") {
                    deleteFile(File(applicationDirectory, fileName))
                }
            }
        }
        return true
    }

    override fun createFileInDir(fileName: String): File {
        val newFile = File(context.cacheDir, fileName)
        if (!newFile.exists()) {
            newFile.createNewFile()
        }
        return newFile
    }

    override fun getDataFromFile(fileName: String): String? {
        try {
            return FileUtils.readFileToString(File("${context.cacheDir}/$fileName"),"UTF_8")
        } catch (e: IOException) {
            Log.d("local_storage", "getDataFromFile: ");
            return ""
        }
    }

    private fun deleteFile(file: File?): Boolean {
        var deletedAll = true
        if (file != null) {
            if (file.isDirectory) {
                val children = file.list()
                for (i in children.indices) {
                    deletedAll = deleteFile(File(file, children[i])) && deletedAll
                }
            } else {
                deletedAll = file.delete()
            }
        }
        return deletedAll
    }

}