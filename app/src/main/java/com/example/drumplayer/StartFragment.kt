package com.example.drumplayer


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_start_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment(){
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ViewModel::class.java)
        } ?: throw Exception("bad activity")
        //viewModel.getProfile()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            compose.setOnClickListener {
                if (!name_text.text.isNullOrBlank()) {
                    //viewModel.name.value = name_text.text.toString()
                    //println("name is " + viewModel.name.value )
                    viewModel.loginUser.value = name_text.text.toString()
                    viewModel.registerUser(name_text.text.toString())
                    findNavController().navigate(R.id.action_global_practiceFragment)}
                else {
                    val text = "Please enter your name first!!!"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, text, duration)
                toast.show()
                }
//play previous
            }
            playlist.setOnClickListener {
                if (!name_text.text.isNullOrBlank()) {
                    //viewModel.name.value = name_text.text.toString()
                    //println("name is " + viewModel.name.value )
                    viewModel.loginUser.value = name_text.text.toString()
                    findNavController().navigate(R.id.action_global_recordFragment)
                } else {
                    val text = "Please enter your name first!!!"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.show()
                }
            }
    }

}





