package com.project.musicplayers.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.musicplayers.R
import com.project.musicplayers.adapter.ListSongAdapter
import com.project.musicplayers.model.ListSong
import java.util.ArrayList

class DetailActivity : AppCompatActivity(), JcPlayerManagerListener {

    private lateinit var jcplayerDetail : JcPlayerView
    private val database = Firebase.database.reference

    private lateinit var listView: RecyclerView
    private var listSongAdapter : ListSongAdapter? = null
    private var mSongs : ArrayList<ListSong>? = null
    lateinit var contex: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        contex = this
        initUI()
        initListener()

    }

    private fun initUI() {
        jcplayerDetail = findViewById(R.id.jcplayerDetail)
        listView = findViewById(R.id.recDetail)
        listView.layoutManager = LinearLayoutManager(contex)
        jcplayerDetail.jcPlayerManagerListener = this
    }

    private fun initListener() {
        mSongs = ArrayList()
        database.child("Songs")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    mSongs!!.clear()
                    for (dataSnapshot in p0.children) {
                        val allData = dataSnapshot.getValue(ListSong::class.java)
                        mSongs!!.add(allData!!)
                    }

                    listSongAdapter = ListSongAdapter(contex, mSongs!!, object : ListSongAdapter.OnClicks {
                        override fun onClick(posisi: Int) {
                            //jcPlayerView.playAudio(jcAudios[posisi])
                            //jcPlayerView.createNotification()
                        }

                    })
                    listSongAdapter!!.notifyDataSetChanged()
                    listView.adapter = listSongAdapter!!

                }
            })
    }



    override fun onCompletedAudio() {

    }

    override fun onContinueAudio(status: JcStatus) {

    }

    override fun onJcpError(throwable: Throwable) {

    }

    override fun onPaused(status: JcStatus) {

    }

    override fun onPlaying(status: JcStatus) {

    }

    override fun onPreparedAudio(status: JcStatus) {

    }

    override fun onStopped(status: JcStatus) {

    }

    override fun onTimeChanged(status: JcStatus) {

    }
}