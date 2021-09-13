package com.project.musicplayers.adapter

import android.content.Context
import android.util.Log
import android.util.SparseArray

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jean.jcplayer.model.JcAudio
import com.project.musicplayers.R
import com.project.musicplayers.adapter.AudioAdapterJc.AudioAdapterViewHolder
import com.project.musicplayers.model.ListSong

class ListSongAdapter(private val mContext: Context,
                      private val mSong: List<ListSong>,
                      private val mClick: OnClicks,

)
    : RecyclerView.Adapter<ListSongAdapter.ViewHolder?>() {

    val progressMap = SparseArray<Float>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
            .inflate(R.layout.list_song, viewGroup, false)
        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return mSong.size
    }

    interface OnClicks {
        fun onClick(
            posisi: Int,
        )
    }

    fun updateProgress(jcAudio: ListSong?, progress: Float) {
        val position: Int = mSong.indexOf(jcAudio)

        progressMap.put(position, progress)
        if (progressMap.size() > 1) {
            for (i in 0 until progressMap.size()) {
                if (progressMap.keyAt(i) != position) {

                    notifyItemChanged(progressMap.keyAt(i))
                    progressMap.delete(progressMap.keyAt(i))
                }
            }
        }
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, posisi: Int) {
        val song = mSong[posisi]

        Glide.with(mContext).load(song.getImageUrl()).into(holder.imageView)
        holder.titleTxt.text = song.getSongName()
        holder.artist.text = song.getSongArtist()
        holder.duration.text = song.getSongDuration()

        holder.container.setOnClickListener {
            mClick.onClick(holder.adapterPosition)
        }

        applyProgressPercentage(holder, progressMap[posisi, 0.0f])
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var imageView : ImageView = itemview.findViewById(R.id.image)
        var titleTxt : TextView = itemview.findViewById(R.id.titleasdasd)
        var artist : TextView = itemview.findViewById(R.id.artis)
        var duration : TextView = itemview.findViewById(R.id.duration)
        var container : ConstraintLayout = itemview.findViewById(R.id.container)
        var viewProgress : View= itemview.findViewById(R.id.song_progress_view);
        var viewAntiProgress : View = itemview.findViewById(R.id.song_anti_progress_view);
    }

    private fun applyProgressPercentage(holder: ViewHolder, percentage: Float) {
        Log.d(
            " AudioAdapterJc.TAG",
            "applyProgressPercentage() with percentage = $percentage"
        )
        val progress = holder.viewProgress.layoutParams as LinearLayout.LayoutParams
        val antiProgress = holder.viewAntiProgress.layoutParams as LinearLayout.LayoutParams
        progress.weight = percentage
        holder.viewProgress.layoutParams = progress
        antiProgress.weight = 1.0f - percentage
        holder.viewAntiProgress.layoutParams = antiProgress
    }




}