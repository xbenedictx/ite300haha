package com.example.ite300haha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageButton>(R.id.startButton).setOnClickListener {
            showGameDescription()
        }

        findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun showGameDescription() {
        AlertDialog.Builder(this)
            .setTitle("Game Description")
            .setMessage("Hangman is a word-guessing game. Try to guess the hidden word by suggesting letters. You have 6 attempts to guess the word correctly.")
            .setPositiveButton("Start Game") { _, _ ->
                startActivity(Intent(this, GameActivity::class.java))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}