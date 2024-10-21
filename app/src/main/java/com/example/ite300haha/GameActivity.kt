package com.example.ite300haha

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class GameActivity : AppCompatActivity() {

    private lateinit var hangmanImageViews: List<ImageView>
    private lateinit var wordToGuessTextView: TextView
    private lateinit var attemptsRemainingTextView: TextView
    private lateinit var letterButtonsLayout: GridLayout
    private lateinit var newGameButton: ImageButton
    private lateinit var hintButton: ImageButton
    private lateinit var settingsButton: ImageButton

    private val words = listOf("KOTLIN", "ANDROID", "PROGRAMMING", "HANGMAN")
    private lateinit var currentWord: String
    private lateinit var displayWord: StringBuilder
    private var attemptsRemaining = 6
    private var difficulty = 1
    private var soundEnabled = true
    private var hintUsed = false

    private lateinit var correctSound: MediaPlayer
    private lateinit var incorrectSound: MediaPlayer
    private lateinit var gameOverSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initializeViews()
        loadSettings()
        initializeSounds()
        startNewGame()
    }

    private fun initializeViews() {
        hangmanImageViews = listOf(
            findViewById(R.id.hangmanImageView0),
            findViewById(R.id.hangmanImageView1),
            findViewById(R.id.hangmanImageView2),
            findViewById(R.id.hangmanImageView3),
            findViewById(R.id.hangmanImageView4),
            findViewById(R.id.hangmanImageView5)
        )
        wordToGuessTextView = findViewById(R.id.wordToGuessTextView)
        attemptsRemainingTextView = findViewById(R.id.attemptsRemainingTextView)
        letterButtonsLayout = findViewById(R.id.letterButtonsLayout)
        newGameButton = findViewById(R.id.newGameButton)
        hintButton = findViewById(R.id.hintButton)
        settingsButton = findViewById(R.id.settingsButton)

        wordToGuessTextView.setTextColor(Color.BLACK)
        attemptsRemainingTextView.setTextColor(Color.BLACK)

        newGameButton.setOnClickListener { startNewGame() }
        hintButton.setOnClickListener { giveHint() }
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        setupLetterButtons()
    }

    private fun setupLetterButtons() {
        for (i in 0 until letterButtonsLayout.childCount) {
            val imageButton = letterButtonsLayout.getChildAt(i) as ImageButton
            val letter = 'A' + i
            imageButton.tag = letter.toString()
            imageButton.setOnClickListener { onLetterGuessed(letter) }
        }
    }

    private fun loadSettings() {
        val sharedPref = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)
        difficulty = sharedPref.getInt("difficulty", 1)
        soundEnabled = sharedPref.getBoolean("sound", true)
    }

    private fun initializeSounds() {
        correctSound = MediaPlayer.create(this, R.raw.correct_sound)
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound)
        gameOverSound = MediaPlayer.create(this, R.raw.game_over_sound)
    }

    private fun startNewGame() {
        currentWord = words.filter { it.length >= difficulty * 3 }.random()
        displayWord = StringBuilder("_ ".repeat(currentWord.length).trim())
        attemptsRemaining = 6
        hintUsed = false
        updateDisplay()
        resetLetterButtons()
        resetHangmanImage()
        hintButton.isEnabled = true
        newGameButton.isEnabled = true
    }

    private fun resetHangmanImage() {
        hangmanImageViews.forEachIndexed { index, imageView ->
            if (index == 0) {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.hangman_0)
            } else {
                imageView.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateDisplay() {
        wordToGuessTextView.text = displayWord.toString()
        attemptsRemainingTextView.text = "Attempts remaining: $attemptsRemaining"
    }

    private fun resetLetterButtons() {
        for (i in 0 until letterButtonsLayout.childCount) {
            letterButtonsLayout.getChildAt(i).isEnabled = true
        }
    }

    private fun onLetterGuessed(letter: Char) {
        letterButtonsLayout.findViewWithTag<ImageButton>(letter.toString())?.isEnabled = false

        if (currentWord.contains(letter)) {
            for (i in currentWord.indices) {
                if (currentWord[i] == letter) {
                    displayWord[i * 2] = letter
                }
            }
            if (soundEnabled) correctSound.start()
            provideVisualFeedback(true)
        } else {
            attemptsRemaining--
            if (soundEnabled) incorrectSound.start()
            provideVisualFeedback(false)
            updateHangmanImage()
        }
        updateDisplay()
        checkGameEnd()
    }

    private fun provideVisualFeedback(correct: Boolean) {
        val color = if (correct) Color.GREEN else Color.RED
        wordToGuessTextView.setTextColor(color)
        android.os.Handler().postDelayed({
            wordToGuessTextView.setTextColor(Color.BLACK)
        }, 500)
    }

    private fun updateHangmanImage() {
        val imageIndex = minOf(6 - attemptsRemaining, 5)
        hangmanImageViews[imageIndex].apply {
            setImageResource(resources.getIdentifier("hangman_$imageIndex", "drawable", packageName))
            visibility = View.VISIBLE
        }
    }

    private fun checkGameEnd() {
        when {
            displayWord.toString().replace(" ", "") == currentWord -> {
                disableAllButtons()
                showGameEndDialog("Congratulations!", "You win!")
            }
            attemptsRemaining <= 0 -> {
                disableAllButtons()
                showGameEndDialog("Game Over", "The word was $currentWord")
            }
        }
    }

    private fun showGameEndDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Play Again") { _, _ ->
                startNewGame()
            }
            .setNegativeButton("Main Menu") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun disableAllButtons() {
        for (i in 0 until letterButtonsLayout.childCount) {
            letterButtonsLayout.getChildAt(i).isEnabled = false
        }
        hintButton.isEnabled = false
        newGameButton.isEnabled = false
    }

    private fun giveHint() {
        if (!hintUsed) {
            val unrevealedIndices = displayWord.mapIndexed { index, c ->
                if (c == '_' && index % 2 == 0) index / 2 else null
            }.filterNotNull()
            if (unrevealedIndices.isNotEmpty()) {
                val hintIndex = unrevealedIndices.random()
                displayWord[hintIndex * 2] = currentWord[hintIndex]
                updateDisplay()
                checkGameEnd()
                hintUsed = true
                hintButton.isEnabled = false
            } else {
                Toast.makeText(this, "No more hints available!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "You can only use hint once", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }

    override fun onDestroy() {
        super.onDestroy()
        correctSound.release()
        incorrectSound.release()
        gameOverSound.release()
    }
}