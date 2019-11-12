package com.example.drumplayer

import android.content.Context
import android.media.MediaPlayer
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class DrumWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    var bassSound: MediaPlayer = MediaPlayer.create(context, R.raw.bass)
    var crashSound: MediaPlayer = MediaPlayer.create(context, R.raw.crash)
    var snareSound: MediaPlayer = MediaPlayer.create(context, R.raw.snare)
    //for the inputdata can it be the arraylist or it have to be the int and string 
    override fun doWork(): Result {
        var music = inputData.getIntArray("MusicArray")!!
        var time = inputData.getLongArray("TimeArray")!!

        for(i in 0 until music.size ){
            // time should be the millis 
            Thread.sleep(time[i])
            if(music[i] == 0){
                bassSound.start()
            } else if(music[i] == 1){
                crashSound.start()
            } else if(music[i] == 2){
                snareSound.start()
            }
            
        }
        

        return Result.success()
    }
}