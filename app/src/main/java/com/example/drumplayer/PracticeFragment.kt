package com.example.drumplayer


import android.annotation.TargetApi
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_CLOSEST
import android.media.MediaPlayer.SEEK_NEXT_SYNC
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import kotlinx.android.synthetic.main.fragment_practice.*
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * A simple [Fragment] subclass.
 */
class PracticeFragment : Fragment() {
    var bassSound: MediaPlayer? =null
    var cymbalSound: MediaPlayer? = null
    var snareSound: MediaPlayer? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ViewModel::class.java)
        } ?: throw Exception("bad activity")
        //viewModel.getProfile()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_practice, container, false)
    }


    lateinit var viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        record.setOnClickListener{
            if(!viewModel.isRecording.value!!){
                record.setBackgroundResource(R.drawable.ic_stop_black_24dp)
                viewModel.isRecording.value= true
                viewModel.recordCheck()
                println(" timeA start record = "+ viewModel.timeA.value)
                println(" style = "+ viewModel.style.value)

            }else{
                record.setBackgroundResource(R.drawable.ic_fiber_manual_record_black_24dp)
                viewModel.isRecording.value= false
                viewModel.finishRecord()
                println(" timeA end record  = "+ viewModel.timeA.value)
                println(" style = "+ viewModel.style.value)

            }

        }
        play.setOnClickListener{
            if (viewModel.isPlaying.value!!){
                val text = "it is playing"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }else if(viewModel.isRecording.value!!){
                val text = "it is recording"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }else if(viewModel.timeA.value!!.size<=0){
                val text = "it is nothing to play"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }else{
                viewModel.start()
                observeCounter()
            }

        }
        save.setOnClickListener{
            // view model int in sd
            //store array of time and music into to our database
            if(!viewModel.isRecording.value!!&& !viewModel.isPlaying.value!!&&viewModel.timeA.value!!.size>0){
                viewModel.store()
                Toast.makeText(context,"save successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_global_recordFragment)
            }else{
                val text = "Please finish playing/recording"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }


        }
        bass.setOnClickListener{
            //bassSound!!.reset()
            if(!bassSound!!.isPlaying) {
                viewModel.bass()
                bassSound!!.start()
            }
        }
        cymbal.setOnClickListener{
            if(!cymbalSound!!.isPlaying) {
                viewModel.cymbal()
                cymbalSound!!.start()
            }

        }
        snare.setOnClickListener{
            if(!snareSound!!.isPlaying) {
                viewModel.snare()
                snareSound!!.start()
            }
        }
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bassSound = MediaPlayer.create(context,R.raw.bass)
        cymbalSound = MediaPlayer.create(context, R.raw.crash)
        snareSound = MediaPlayer.create(context, R.raw.snare)
        
    }
    fun observeCounter() {
        viewModel.getWorkInfo().observe(this, Observer {
            if (it != null) {
                when (it.state) {
                    WorkInfo.State.ENQUEUED -> {
                    }
                    WorkInfo.State.RUNNING -> {
//                        it.progress.getInt("counter", 3)?.run {
//                            timer_text.setText(this.toString())
//                        }
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        viewModel.isPlaying.value = false
                    }
                }
            }
        })
    }


}
