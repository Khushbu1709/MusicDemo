package com.example.music


import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.music.Music.ACTION_START_PLAYER
import com.example.music.Music.ACTION_STOP_PLAYER
import com.example.music.Music.PLAY
import com.example.music.Music.checked
import com.example.music.Music.livedata
import com.example.music.databinding.ActivityServiceBinding


class ServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Allow permission", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Not allow permission", Toast.LENGTH_SHORT).show()
                }
            }

        /**
         *  If user not allow permission then not able to see notification in android 13 or + device
         * */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcherPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        livedata.observe(this) {
            if (it.equals(PLAY)) {
                binding.musicStart.setText(R.string.music_start)
            } else {
                binding.musicStart.setText(R.string.music_stop)
            }
        }

        binding.musicStart.setOnClickListener {
            if (checked) {
                binding.musicStart.text = resources.getString(R.string.music_stop)
                checked = false
                startService(
                    Intent(
                        this,
                        MusicServicesClass::class.java
                    ).setAction(ACTION_START_PLAYER)
                )
            } else {
                checked = true
                binding.musicStart.text = resources.getString(R.string.music_start)
                startService(
                    Intent(
                        this,
                        MusicServicesClass::class.java
                    ).setAction(ACTION_STOP_PLAYER)
                )

            }
        }
    }

}




