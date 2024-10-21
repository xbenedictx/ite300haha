package com.example.ite300haha

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var difficultySeekBar: SeekBar
    private lateinit var difficultyTextView: TextView
    private lateinit var soundSwitch: Switch
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        difficultySeekBar = findViewById(R.id.difficultySeekBar)
        difficultyTextView = findViewById(R.id.difficultyTextView)
        soundSwitch = findViewById(R.id.soundSwitch)
        saveButton = findViewById(R.id.saveButton)

        loadSettings()

        difficultySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateDifficultyText(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        saveButton.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun loadSettings() {
        val sharedPref = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)
        val difficulty = sharedPref.getInt("difficulty", 1)
        val sound = sharedPref.getBoolean("sound", true)

        difficultySeekBar.progress = difficulty - 1
        updateDifficultyText(difficulty - 1)
        soundSwitch.isChecked = sound
    }

    private fun saveSettings() {
        val sharedPref = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("difficulty", difficultySeekBar.progress + 1)
            putBoolean("sound", soundSwitch.isChecked)
            apply()
        }
    }

    private fun updateDifficultyText(progress: Int) {
        val difficultyText = when (progress) {
            0 -> "Easy"
            1 -> "Medium"
            2 -> "Hard"
            else -> "Unknown"
        }
        difficultyTextView.text = "Difficulty: $difficultyText"
    }
}