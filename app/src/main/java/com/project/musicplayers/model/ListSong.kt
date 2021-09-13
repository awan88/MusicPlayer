package com.project.musicplayers.model

class ListSong  {
    private var songGenre = ""
    private var songName = ""
    private var songUrl = ""
    private var imageUrl = ""
    private var songArtist = ""
    private var songDuration = ""

    constructor()
    constructor(
        songGenre : String,
        songName: String,
        songUrl: String,
        imageUrl: String,
        songArtist: String,
        songDuration: String
    ) {
        this.songGenre = songGenre
        this.songName = songName
        this.songUrl = songUrl
        this.imageUrl = imageUrl
        this.songArtist = songArtist
        this.songDuration = songDuration
    }

    fun getSongGenre():String{
        return songGenre
    }
    fun setSongGenre(songGenre: String) {
        this.songGenre = songGenre
    }

    fun getSongName():String{
        return songName
    }
    fun setSongName(songName: String) {
        this.songName = songName
    }

    fun getSongUrl():String{
        return songUrl
    }
    fun setSongUrl(songUrl: String) {
        this.songUrl = songUrl
    }

    fun getImageUrl():String{
        return imageUrl
    }
    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getSongArtist():String{
        return songArtist
    }
    fun setSongArtist(songArtist: String) {
        this.songArtist = songArtist
    }

    fun getSongDuration():String{
        return songDuration
    }
    fun setSongDuration(songDuration: String) {
        this.songDuration = songDuration
    }


}