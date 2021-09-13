package com.project.musicplayers.ui

import android.Manifest

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.view.JcPlayerView
import com.example.jean.jcplayer.model.JcAudio
import com.google.firebase.database.*
import java.util.ArrayList
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.project.musicplayers.R
import com.project.musicplayers.adapter.ListSongAdapter
import com.project.musicplayers.model.ListSong
import androidx.recyclerview.widget.SimpleItemAnimator
import com.project.musicplayers.adapter.AudioAdapterJc


class MainActivity : AppCompatActivity(), View.OnClickListener, JcPlayerManagerListener {

    private val jcAudios = ArrayList<JcAudio>()
    private val database = Firebase.database.reference

    private lateinit var listView: RecyclerView
    private var listSongAdapter: ListSongAdapter? = null
    private var mUsers: ArrayList<ListSong>? = null
    lateinit var context: Context

    private lateinit var tvToolbarTitle : TextView
    private lateinit var btnUpload : LinearLayout
    lateinit var jcPlayerView: JcPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        retrieveSongs()

    }

    private fun initUI() {
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
        jcPlayerView = findViewById(R.id.jcplayer)
        listView = findViewById(R.id.myListView)
        btnUpload = findViewById(R.id.btnUpload)
        context = this


        tvToolbarTitle.text = "Aone Music"
        btnUpload.setOnClickListener(this)
    }

    private fun retrieveSongs() {
        mUsers = ArrayList()
        listView.setHasFixedSize(true)
        listView.layoutManager = LinearLayoutManager(this)
        (listView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        database.child("Songs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUsers!!.clear()
                    for (ds in dataSnapshot.children) {
                        val songObj = ds.getValue(ListSong::class.java)
                        mUsers!!.add(songObj!!)
                        jcAudios.add(JcAudio.createFromURL(
                                songObj.getSongName(),
                                songObj.getSongUrl()
                            )
                        )
                    }

                    listSongAdapter = ListSongAdapter(context, mUsers!!, object : ListSongAdapter.OnClicks {
                            override fun onClick(posisi: Int) {
                                jcPlayerView.playAudio(jcAudios[posisi])
                                jcPlayerView.createNotification()
                            }

                        })
                    jcPlayerView.initPlaylist(jcAudios, null)
                    listSongAdapter!!.notifyDataSetChanged()
                    listView.adapter = listSongAdapter
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun cekPermission() {
        val intent = Intent(this, UploadActivity::class.java)
        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                startActivity(intent)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    this@MainActivity,
                    "Izin aplikasi diperlukan \n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setRationaleMessage("Izin Aplikasi Diperlukan")
            .setDeniedMessage("Jika Anda menolak izin," + "Anda tidak dapat menggunakan layanan ini \n\n Harap aktifkan izin di pengaturan izin aplikasi")
            .setGotoSettingButtonText("Pengaturan")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    override fun onClick(v: View?) {
       when(v?.id){
           R.id.btnUpload ->{
               cekPermission()
               //jcPlayerView.pause()
               jcPlayerView.continueAudio()
           }
       }
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
        updateProgress(status)
    }

    private fun updateProgress(jcStatus: JcStatus) {

        runOnUiThread {
            var progress = ((jcStatus.duration - jcStatus.currentPosition).toFloat()
                    / jcStatus.duration.toFloat())
            progress = 1.0f - progress
            ListSongAdapter.updateProgres(jcStatus.jcAudio, progress)

        }
    }
}