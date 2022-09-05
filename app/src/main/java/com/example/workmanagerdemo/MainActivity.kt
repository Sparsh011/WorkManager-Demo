package com.example.workmanagerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOTR = findViewById<Button>(R.id.btnOneTimeRequest)
        val tvOTR = findViewById<TextView>(R.id.tvOneTimeRequest)
        val btnPeriodicReq = findViewById<Button>(R.id.btnPeriodicReq)

        btnOTR.setOnClickListener{
//            WorkManager doesn't have any constraints by default
            val otrConstraints = Constraints.Builder()
                .setRequiresCharging(false) // Checks whether phone is charging or not
                .setRequiredNetworkType(NetworkType.CONNECTED) // Checks whether phone is connected to internet or not
                .build()
//            These are the constraints we can define. The code will be executed only if these constraints are met

            val data = Data.Builder()
            data.putString("inputKey", "input value")

            val sampleWork = OneTimeWorkRequest.Builder(OneTimeRequestWorker::class.java)
                .setInputData(data.build())
                .setConstraints(otrConstraints)
                .build()

            WorkManager.getInstance(this).enqueue(sampleWork)

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(sampleWork.id)
                .observe(this) { workInfo -> // observes change
                    OneTimeRequestWorker.Companion.logger(workInfo.state.name)

                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.ENQUEUED -> {
                                tvOTR.text = "Taks Enqueued"
                            }
                            WorkInfo.State.BLOCKED -> {
                                tvOTR.text = "Task Blocked"
                            }

                            WorkInfo.State.RUNNING -> {
                                tvOTR.text = "Task Running"
                            }
                            else -> {
                                tvOTR.text = "Task state is in else part"
                            }
                        }
                    }

                    if (workInfo != null && workInfo.state.isFinished){
                        when (workInfo.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                tvOTR.text = "Taks Successful"
                                val successOutputData = workInfo.outputData
                                val outputText = successOutputData.getString("outputKey")
                                Log.i("Worker Output", "$outputText")
                            }
                            WorkInfo.State.FAILED -> {
                                tvOTR.text = "Task Failed"
                            }

                            WorkInfo.State.CANCELLED -> {
                                tvOTR.text = "Task Cancelled"
                            }
                            else -> {
                                tvOTR.text = "In Task state isFinished else part"
                            }
                        }
                    }
                }
        }

        btnPeriodicReq.setOnClickListener{
            val pdrConstraints = Constraints.Builder()
                .setRequiresCharging(false) // Checks whether phone is charging or not
                .setRequiredNetworkType(NetworkType.CONNECTED) // Checks whether phone is connected to internet or not
                .setRequiresBatteryNotLow(true)
                .build()

            val periodicWorkRequest = PeriodicWorkRequest.Builder(PeriodicRequestWorker::class.java, 15, TimeUnit.MINUTES) // In every 15 minutes, this will be executed
                .setConstraints(pdrConstraints)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "Periodic Work Request",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }
}