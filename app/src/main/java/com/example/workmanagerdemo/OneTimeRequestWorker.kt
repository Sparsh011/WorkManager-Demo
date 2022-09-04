package com.example.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class OneTimeRequestWorker(context: Context, params: WorkerParameters): Worker(context, params) {
//    Must be implemented in every worker class
    override fun doWork(): Result {
        val inputValue = inputData.getString("inputKey")
        Log.i("Worker input", "$inputValue")
//      TODO - Put your background running tasks here such as downloading huge files or stocking notifications
        return Result.success(createOutputData())
    }

    private fun createOutputData(): Data{
        return Data.Builder().putString("outputKey", "Output value").build()
    }

    object Companion{
        fun logger(message: String) = Log.i("WorkRequest status", message)
    }
}