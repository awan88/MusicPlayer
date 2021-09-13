package com.project.musicplayers.ui

import android.app.Dialog
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.*
import com.mai.progressdialog.ProgressDialogMai
import com.project.musicplayers.R
import java.util.*
import java.util.concurrent.TimeUnit

class UploadActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var storageReference: StorageReference
    private val database = Firebase.database.reference

    private var uriSong: Uri? = null
    private var uriImage: Uri? = null

    private var fileName = ""
    private var genreSong = ""
    private var songUrl = ""
    private var artistName = ""
    private var imageUrl = ""
    private var songLength = ""

    private lateinit var progressDialogMai: ProgressDialogMai
    private lateinit var progressDialog: Dialog
    private lateinit var selectSongNameEditText: EditText
    private lateinit var artistNameEditText: EditText
    private lateinit var genreEditText: EditText
    private lateinit var selectImage: ImageView
    private lateinit var ivToolbarBack : ImageView
    private lateinit var selectSong: ImageButton
    private lateinit var uploadButton: Button

    lateinit var jcPlayerView: JcPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        initUI()
        initProgressDialog()

        jcPlayerView = findViewById(R.id.jcplayerupload)

    }

    private fun initProgressDialog() {
        progressDialogMai = ProgressDialogMai(this)
        progressDialogMai.setCorner(30)
        progressDialogMai.setTextColor(R.color.colorAccent)
        progressDialogMai.setDialogWidth(0.9)
        progressDialogMai.setTextSize(7)
        progressDialogMai.setColorProgress(R.color.colorAccent)
        progressDialogMai.setColorDialog(R.color.colorPrimaryDark)

        progressDialog = progressDialogMai.dialog()
    }

    private fun initUI() {
        storageReference = FirebaseStorage.getInstance().reference
        ivToolbarBack = findViewById(R.id.ivToolbarBack)
        selectSongNameEditText = findViewById(R.id.selectSong)
        selectSong = findViewById(R.id.selectSongButton)
        genreEditText = findViewById(R.id.genreEditText)
        artistNameEditText = findViewById(R.id.artistNameEditText)
        selectImage = findViewById(R.id.selectImage)
        uploadButton = findViewById(R.id.uploadSongButton)

        selectSong.setOnClickListener(this)
        selectImage.setOnClickListener(this)
        uploadButton.setOnClickListener(this)
        ivToolbarBack.setOnClickListener(this)
    }

    private val pickSong = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "batal pilih lagu", Toast.LENGTH_SHORT).show()
            } else {
                uriSong = uri
                fileName = getFileName(uriSong!!).toString()
                selectSongNameEditText.setText(fileName)
                songLength = getSongDuration(uriSong)
            }
        }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uriImage = uri
                Glide.with(this).load(uri).into(selectImage)
            } else {
                Toast.makeText(this, "batal pilih thumnail", Toast.LENGTH_SHORT).show()
            }

        }

    private fun upload() {
        when {
            uriSong == null -> {
                Toast.makeText(this, "Silahkan pilih lagi", Toast.LENGTH_SHORT).show()
            }
            selectSongNameEditText.text.toString() == "" -> {
                Toast.makeText(this, "nama lagu kosong!", Toast.LENGTH_SHORT).show()
            }
            genreEditText.text.toString() == "" -> {
                Toast.makeText(this, "genre, kosong", Toast.LENGTH_SHORT).show()
            }
            artistNameEditText.text.toString() == "" -> {
                Toast.makeText(this, "tambah nama artis, nama album", Toast.LENGTH_SHORT).show()
            }
            uriImage == null -> {
                Toast.makeText(this, "pilih thumnail", Toast.LENGTH_SHORT).show()
            }
            else -> {
                genreSong = genreEditText.text.toString()
                fileName = selectSongNameEditText.text.toString()
                artistName = artistNameEditText.text.toString()
                uploadImageToServer()
                uploadFileToServer()
            }
        }
    }

    private fun uploadImageToServer() {
        val filePath = storageReference.child("Thumbnails").child(fileName)

        val uploadTask: StorageTask<*>
        uploadTask = filePath.putFile(uriImage!!)
        progressDialog.show()
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@Continuation filePath.downloadUrl
        }).addOnCompleteListener { task ->
            val urlimg = task.result
            imageUrl = urlimg.toString()
        }
    }

    private fun uploadFileToServer() {

        val filePath = storageReference.child("Audios").child(fileName)
        val uploadTaskSong: StorageTask<*>
        uploadTaskSong = filePath.putFile(uriSong!!)
        progressDialog.show()

        uploadTaskSong.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@Continuation filePath.downloadUrl
        }).addOnCompleteListener { task ->
            val urlSong = task.result
            songUrl = urlSong.toString()
            uploadDetailsToDatabase()
        }

        uploadTaskSong.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            progressDialogMai.setText("Upload file : $progress %")
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "Upload Gagal! silahkan coba lagi!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadDetailsToDatabase() {
        val dataKey = database.push().key
        val dataSong = HashMap<String, Any>()
        dataSong["songGenre"] = genreSong
        dataSong["songName"] = fileName
        dataSong["songUrl"] = songUrl
        dataSong["imageUrl"] = imageUrl
        dataSong["songArtist"] = artistName
        dataSong["songDuration"] = songLength

        database.child("Songs").child(dataKey!!).updateChildren(dataSong)
            .addOnCompleteListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Lagu berhasil di simpan ke database", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (Objects.equals(uri.scheme, "content")) {
            contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            assert(result != null)
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    private fun getSongDuration(song: Uri?): String {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(applicationContext, song)
        val durationString =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val time = durationString.toLong()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time).toInt()
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(time).toInt()
        val seconds = totalSeconds - minutes * 60
        return if (seconds.toString().length == 1) {
            "$minutes:0$seconds"
        } else {
            "$minutes:$seconds"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ivToolbarBack -> {
                onBackPressed()
            }

            R.id.selectSongButton -> {
                pickSong.launch("audio/mp3")
            }

            R.id.selectImage -> {
                pickImage.launch("image/*")
            }

            R.id.uploadSongButton -> {
                upload()
            }
        }
    }
}