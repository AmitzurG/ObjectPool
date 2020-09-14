package com.app.objectpool

import android.util.Log
import java.util.*

class ObjectPool private constructor() {
    private val freeObjects: Queue<ReusedObject> = LinkedList()
    private var objectNum = 1

    companion object {
        val instant by lazy { ObjectPool() }
        const val LOG_TAG = "ObjectPool"
    }

    init {
        freeObjects.addAll(createObjects())
    }

    // take object from the object pool
    fun pull() : ReusedObject {
        if (freeObjects.isEmpty()) {
            Log.i(LOG_TAG, "there aren't unused objects, create another five objects")
            freeObjects.addAll(createObjects(5))
        }
        val obj = freeObjects.poll()
        Log.i(LOG_TAG, "pull object ${obj.name}")
        return obj
    }

    // return object to reuse
    fun push(obj: ReusedObject) {
        Log.i(LOG_TAG, "push object ${obj.name}")
        freeObjects.offer(obj)
    }

    private fun createObjects(capacity: Int = 10): List<ReusedObject> {
        val objects = ArrayList<ReusedObject>()
        repeat(capacity) {
            objects.add(ReusedObject("Object number ${objectNum++}"))
        }
        return objects
    }

    class ReusedObject(val name: String) {
        @Throws(Throwable::class)
        protected fun finalize() {
            instant.push(this)
        }
    }
}



