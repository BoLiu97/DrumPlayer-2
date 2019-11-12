package com.example.drumplayer

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.fragment_practice.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.common.collect.DiscreteDomain.integers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ViewModel(application: Application) : AndroidViewModel(application) {
    var drumList = MutableLiveData<ArrayList<Drum>>()
    var nameList = MutableLiveData<ArrayList<Drum>>()
    var isPlaying = MutableLiveData<Boolean>()
    var isRecording = MutableLiveData<Boolean>()
    var startTime = MutableLiveData<Date>()//The time when the app is launched
    var currentDrum = MutableLiveData<Drum>()
    var database = MutableLiveData<DatabaseReference>()
    var fireDatabase = MutableLiveData<DatabaseReference>()
    var loginUser =  MutableLiveData<String>()
    var timeA = MutableLiveData<ArrayList<Long>>()
    var style =MutableLiveData<ArrayList<Int>>()
    var name = MutableLiveData<String>()
    var isUse =  MutableLiveData<Boolean>()
    //var forName = Stri
    // drum define number as music
    // 0 bass
    // 1 cymbal
    // 2 snare
    init {

        loginUser.value = ""
        timeA.value = ArrayList()
        style.value = ArrayList()
        //forName = String()
        drumList.value = ArrayList()
        nameList.value = ArrayList()
        name.value = ""
        isPlaying.value = false
        isRecording.value = false
        isUse.value = false


        database.value = FirebaseDatabase.getInstance().reference
        fireDatabase.value = FirebaseDatabase.getInstance().reference

        database.value?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                drumList.value!!.clear()
                //userList.value!!.clear()
                var userN = arrayListOf<String>()
                p0.child("users").children.forEach {
                    it.getValue(Drum::class.java)?.let {
                        userN.add(it.user)
                        //nameList.value?.add(it)
                    }
                }
                if(userN.size>0) {
                    for (i in userN.indices) {
                        //val uName = nameList.value.
                        p0.child("user").child(userN[i]).children.forEach {
                            it.getValue(Drum::class.java)?.let {
                                drumList.value?.add(it)
                            }
                        }
                    }
                }
                listener?.updateRecyclerView()
            }
        })

    }
    fun recordCheck(){
        startTime.value = Calendar.getInstance().time
        timeA.value!!.clear()
        style.value!!.clear()
        //currentDrum.value=null
    }

    fun registerUser(userName: String) {
        val DrumPlay = Drum(
            radio = arrayListOf(),
            timestamp = arrayListOf(),
            recordtime = Calendar.getInstance().time,
            user = userName
        )
        println(DrumPlay.user)
        database.value?.child("users")?.child(DrumPlay.user)?.setValue(DrumPlay)
        //forName.value = DrumPlay
        name.value =  DrumPlay.user
        //name.value = DrumPlay.user
    }
    lateinit var drumWorker: OneTimeWorkRequest
    val UNIQUE_WORK = "starting"

    fun updateList(){
        //drumList = database.value?.child("user")
    }
    fun start(){
        if (!isPlaying.value!! && !isRecording.value!!&& timeA.value!!.size > 0) {
            isPlaying.value = true
            // put array of time and music
            var intArray = currentDrum.value!!.radio
            val longArray = currentDrum.value!!.timestamp.toLongArray()

            //val iiiArray = currentDrum.value!!.radio.toIntArray()

            var ret = IntArray(intArray.size)
            for (i in ret.indices) {
                ret[i] = intArray[i]
            }
            val MData = Data.Builder()
                .putIntArray("MusicArray", ret)
                .putLongArray("TimeArray", longArray)
                .build()
            //val TData = Data.Builder().putLongArray(.build()
            drumWorker = OneTimeWorkRequest.Builder(DrumWorker::class.java)
                .setInputData(MData)
                //.setInputData(TData)
                .addTag(UNIQUE_WORK)
                .build()
            WorkManager.getInstance(getApplication())
                .enqueueUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, drumWorker)
        }
    }
    fun finishRecord(){
        val life = Calendar.getInstance().time
        currentDrum.value= Drum(
            radio = style.value!!,
            timestamp = timeA.value!!,
            recordtime = life,
            user = name.value!!
        )
    }
    fun store(){
        // store the arrallist of time and music into currentDrum
        //database.value?.child("time")?.child(time.toString())?.setValue(time)
        //record the current time to set time for current drum
        val time =Random().nextInt(1000000000)
        val ran = time.toString()
        viewModelScope.launch {
            async {database.value?.child("user")?.
                child(currentDrum.value!!.user)?.
                child(ran)?.
                setValue(
                    Drum(
                        radio=  currentDrum.value!!.radio,
                        timestamp= currentDrum.value!!.timestamp,
                        recordtime =currentDrum.value!!.recordtime,
                        user =  currentDrum.value!!.user,
                        idU = ran

                    ))}.await()
            println("finished")
        }

    }
    fun bass(){
        if(isRecording.value!!) {
            val now = Calendar.getInstance().time
            val time = now.time - startTime.value!!.time
            startTime.value!!.time = now.time//maybe
            timeA.value!!.add(time)
            style.value!!.add(0)
//            currentDrum.value?.timestamp!!.add(time as String)
//            currentDrum.value?.radio!!.add(0)
        }
    }
    fun cymbal(){
        if(isRecording.value!!) {
            val now = Calendar.getInstance().time
            val time = now.time - startTime.value?.time!!
            startTime.value!!.time = now.time
            timeA.value!!.add(time)
            style.value!!.add(1)
            //currentDrum.value!!.timestamp.add(time as String)
            //currentDrum.value!!.radio.add(1)
        }
    }
    fun snare(){
        if(isRecording.value!!) {
            val now = Calendar.getInstance().time
            val time = now.time - startTime.value?.time!!
            startTime.value!!.time = now.time
            timeA.value!!.add(time)
            style.value!!.add(2)
            //currentDrum.value!!.timestamp.add(time as String)
            //currentDrum.value!!.radio.add(2)
        }
    }
    fun playMusic(instruments: ArrayList<Int>, time: ArrayList<Long>){
        var intArray = instruments.toIntArray()
        val longArray = time.toLongArray()
        val MData = Data.Builder()
            .putIntArray("MusicArray", intArray)
            .putLongArray("TimeArray", longArray)
            .build()
        drumWorker = OneTimeWorkRequest.Builder(DrumWorker::class.java)
            .setInputData(MData)
            //.setInputData(TData)
            .addTag(UNIQUE_WORK)
            .build()
        WorkManager.getInstance(getApplication())
            .enqueueUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, drumWorker)

    }
    var listener: DataChangedListener? = null
    interface DataChangedListener {
        fun updateRecyclerView()
    }
    fun getWorkInfo(): LiveData<WorkInfo> {
        return WorkManager.getInstance(getApplication()).getWorkInfoByIdLiveData(drumWorker.id)
    }
    fun removeMusic(index:Int){
        println("current user"+loginUser.value )
        val idd = drumList.value?.get(index)!!.user
            database.value?.child("user")
                ?.child(idd)
                ?.child(drumList.value?.get(index)!!.idU)
                ?.removeValue()
        drumList.value?.removeAt(index)

    }

}