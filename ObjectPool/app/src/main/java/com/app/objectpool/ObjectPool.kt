package com.app.objectpool

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class ObjectPool private constructor() {
    private val freeObjects: Queue<ReusedObject> = LinkedList()
    private val usedObjects: Queue<ReusedObject> = LinkedList()
    private var objectNum = 1

    companion object {
        val instant by lazy { ObjectPool() }
        const val LOG_TAG = "ObjectPool"
    }

    init {
        freeObjects.addAll(createObjects())
    }

    // take object from the object pool
    fun pull(): ReusedObject {
        if (freeObjects.isEmpty()) {
            Log.i(LOG_TAG, "there aren't unused objects, create another five objects")
            freeObjects.addAll(createObjects(5))
        }
        val obj = freeObjects.poll()
        usedObjects.offer(obj)
        Log.i(LOG_TAG, "pull object ${obj.name}")
        return obj
    }

    // return object to reuse
    private fun push(obj: ReusedObject) {
        Log.i(LOG_TAG, "push object ${obj.name}")
        usedObjects.remove(obj)
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
        var action: () -> Unit = {
            // define the action that the reused object supposed to do, also can set any action
        }

        // possible to execute any action on the reused object
        fun execute(action: () -> Unit = this.action) {
            GlobalScope.launch {
                // delay to demonstrate that the object in used
                delay(2000)
                action()
                // after finish use the object return it to the pool
                instant.push(this@ReusedObject)
            }
        }
    }
}


