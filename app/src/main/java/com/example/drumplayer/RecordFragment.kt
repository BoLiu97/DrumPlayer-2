package com.example.drumplayer


import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.radio_item.view.*


class RecordFragment : Fragment(), ViewModel.DataChangedListener {


    lateinit var viewModel: ViewModel

    lateinit var viewAdapter: RecyclerViewAdapter
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun updateRecyclerView() {
        viewAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ViewModel::class.java)
        } ?: throw Exception("bad activity")
        //viewModel.listener = this
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    //var stringList = ArrayList<String>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewManager = LinearLayoutManager(context)

        viewAdapter = RecyclerViewAdapter(viewModel.drumList.value!!) { drum: Drum ->
            recyclerViewItemSelected(drum)
        }

        radio_recycler.apply {
            this.layoutManager = viewManager
            this.adapter = viewAdapter
        }
        viewModel.drumList.observe(this, Observer {
            viewAdapter.drumList = it

            viewAdapter.notifyDataSetChanged()
        })
        println("test drum list " + viewModel.drumList.value)

        ItemTouchHelper(SwipeHandler()).attachToRecyclerView(
            radio_recycler
        )

    }


    inner class SwipeHandler() : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (viewModel.loginUser.value!!.equals(viewModel.drumList.value?.get(viewHolder.adapterPosition)!!.user)) {
                viewModel.removeMusic(viewHolder.adapterPosition)
                viewAdapter.notifyDataSetChanged()
            } else {
                val text = "You are not the user of this Music"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
                viewAdapter.notifyDataSetChanged()
            }
            //viewModel.drumList.value?.removeAt(viewHolder.adapterPosition)
        }


    }

    private fun recyclerViewItemSelected(drum: Drum) {
        if (drum.user.isNullOrEmpty()) {

        } else {
            viewModel.database.value?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    viewModel.timeA.value!!.clear()
                    viewModel.style.value!!.clear()
                    //val tValue = drum.timestamp
                    //val mValue = drum.radio
                    if (p0.child("user").child(drum.user).child(drum.idU).child("timestamp").exists()) {
                        val time = p0.child("user")
                            .child(drum.user)
                            .child(drum.idU)
                            .child("timestamp").getValue()!!
                        viewModel.timeA.value = time as ArrayList<Long>

                        val style = p0.child("user")
                            .child(drum.user)
                            .child(drum.idU)
                            .child("radio").getValue()!!
                        viewModel.style.value = style as ArrayList<Int>


                        //viewModel.style.value = drum.radio
                        println(" timeA = " + viewModel.timeA.value)
                        println(" style = " + viewModel.style.value)
                        //viewModel.start()
                        println("here!!")
                        viewModel.playMusic(viewModel.style.value!!, viewModel.timeA.value!!)
                    }
                    else{
                        Toast.makeText(context,"Invalid storage", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
    }


    class RecyclerViewAdapter(var drumList: ArrayList<Drum>, val clickListener: (Drum) -> Unit) :
        RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val viewItem =
                LayoutInflater.from(parent.context).inflate(R.layout.radio_item, parent, false)
            return RecyclerViewHolder(viewItem)
        }

        override fun getItemCount(): Int {
            return drumList.size
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            holder.bind(drumList[position], clickListener)
        }

        class RecyclerViewHolder(val viewItem: View) : RecyclerView.ViewHolder(viewItem) {

            fun bind(drum: Drum, clickListener: (Drum) -> Unit) {
                viewItem.run {
                    findViewById<TextView>(R.id.name).text = drum.user
                    findViewById<TextView>(R.id.msg_time_text).text = drum.recordtime.toString()
                }
                viewItem.playRadio.setOnClickListener {
                    clickListener(drum)
                }
            }
        }

    }
}
