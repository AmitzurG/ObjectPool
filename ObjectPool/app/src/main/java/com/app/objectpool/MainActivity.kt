package com.app.objectpool

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        useObjectPoolButton.setOnClickListener {
            useObjectFromPool()
            // look logcat, with tag="ObjectPoll", to see the behavior of the object pool pull/push
        }
        useFewObjectPoolButton.setOnClickListener {
            useFewObjectsFromPool()
            // look logcat, with tag="ObjectPoll", to see the behavior of the object pool pull/push
        }
    }

    @SuppressLint("SetTextI18n")
    private fun useObjectFromPool() {
        val reusedObj = ObjectPool.instant.pull()
        useObjectTextView.text = "use ${reusedObj.name} object"
        useObject(reusedObj)
    }

    @SuppressLint("SetTextI18n")
    private fun useFewObjectsFromPool(objectsNum: Int = 100) = lifecycleScope.launch(Dispatchers.Default) {
        repeat(objectsNum) {
            val reusedObj = ObjectPool.instant.pull()
            withContext(Dispatchers.Main) { useObjectTextView.text = "use ${reusedObj.name}" }
            useObject(reusedObj)
        }
    }

    private fun useObject(obj: ObjectPool.ReusedObject) = lifecycleScope.launch(Dispatchers.Default) {
        Log.i(ObjectPool.LOG_TAG, "use the ${obj.name} object")
        // delay to demonstrate that the object in used
        delay(2000)
    }
}
