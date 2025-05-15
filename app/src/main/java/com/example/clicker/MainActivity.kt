package com.example.clicker

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var clickButton: ImageButton
    private lateinit var upgradeButton1: Button
    private lateinit var upgradeButton2: Button
    private lateinit var upgradeButton3: Button

    private var score: Long = 0
    private var clickValue: Long = 1
    private var autoClickerCount: Int = 0
    private var doublePointsCount: Int = 0
    private var superClickerCount: Int = 0

    private var autoClickerCost: Long = 10
    private var doublePointsCost: Long = 50
    private var superClickerCost: Long = 100

    private val handler = Handler(Looper.getMainLooper())
    private var autoClickRunnable: Runnable? = null
    private lateinit var preferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        clickButton = findViewById(R.id.clickButton)
        upgradeButton1 = findViewById(R.id.upgradeButton1)
        upgradeButton2 = findViewById(R.id.upgradeButton2)
        upgradeButton3 = findViewById(R.id.upgradeButton3)

        loadGameData()
        updateScoreText()
        updateUpgradeButtons()

        val clickAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        clickAnim.duration = 100

        clickButton.setOnClickListener { v ->
            v.startAnimation(clickAnim)
            score += clickValue
            updateScoreText()
            updateUpgradeButtons()
        }

        upgradeButton1.setOnClickListener {
            if (score >= autoClickerCost) {
                score -= autoClickerCost
                autoClickerCount++
                autoClickerCost = (autoClickerCost * 1.5).toLong()
                updateScoreText()
                updateUpgradeButtons()
                saveGameData()

                if (autoClickerCount == 1) {
                    startAutoClicker()
                }
            } else {
                Toast.makeText(this, "Недостаточно очков!", Toast.LENGTH_SHORT).show()
            }
        }

        upgradeButton2.setOnClickListener {
            if (score >= doublePointsCost) {
                score -= doublePointsCost
                doublePointsCount++
                doublePointsCost = (doublePointsCost * 1.5).toLong()
                clickValue = 1 + doublePointsCount.toLong()
                updateScoreText()
                updateUpgradeButtons()
                saveGameData()
            } else {
                Toast.makeText(this, "Недостаточно очков!", Toast.LENGTH_SHORT).show()
            }
        }

        upgradeButton3.setOnClickListener {
            if (score >= superClickerCost) {
                score -= superClickerCost
                superClickerCount++
                superClickerCost = (superClickerCost * 2).toLong()
                clickValue = 1 + doublePointsCount + (superClickerCount * 5).toLong()
                updateScoreText()
                updateUpgradeButtons()
                saveGameData()
            } else {
                Toast.makeText(this, "Недостаточно очков!", Toast.LENGTH_SHORT).show()
            }
        }

        if (autoClickerCount > 0) {
            startAutoClicker()
        }
    }

    private fun updateScoreText() {
        scoreText.text = "Очки: $score"
    }

    private fun updateUpgradeButtons() {
        upgradeButton1.text = "Авто-кликер (Стоимость: $autoClickerCost)"
        upgradeButton2.text = "Двойные очки (Стоимость: $doublePointsCost)"
        upgradeButton3.text = "Супер кликер (Стоимость: $superClickerCost)"

        upgradeButton1.isEnabled = score >= autoClickerCost
        upgradeButton2.isEnabled = score >= doublePointsCost
        upgradeButton3.isEnabled = score >= superClickerCost
    }

    private fun startAutoClicker() {

        autoClickRunnable?.let {
            handler.removeCallbacks(it)
        }


        autoClickRunnable = object : Runnable {
            override fun run() {
                score += autoClickerCount
                updateScoreText()
                updateUpgradeButtons()
                handler.postDelayed(this, 1000) // Запуск каждую секунду
            }
        }

        autoClickRunnable?.let {
            handler.post(it)
        }
    }

    private fun saveGameData() {
        preferences = getSharedPreferences("ClickerGameData", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putLong("score", score)
        editor.putLong("clickValue", clickValue)
        editor.putInt("autoClickerCount", autoClickerCount)
        editor.putInt("doublePointsCount", doublePointsCount)
        editor.putInt("superClickerCount", superClickerCount)
        editor.putLong("autoClickerCost", autoClickerCost)
        editor.putLong("doublePointsCost", doublePointsCost)
        editor.putLong("superClickerCost", superClickerCost)

        editor.apply()
    }

    private fun loadGameData() {
        preferences = getSharedPreferences("ClickerGameData", Context.MODE_PRIVATE)

        score = preferences.getLong("score", 0)
        clickValue = preferences.getLong("clickValue", 1)
        autoClickerCount = preferences.getInt("autoClickerCount", 0)
        doublePointsCount = preferences.getInt("doublePointsCount", 0)
        superClickerCount = preferences.getInt("superClickerCount", 0)
        autoClickerCost = preferences.getLong("autoClickerCost", 10)
        doublePointsCost = preferences.getLong("doublePointsCost", 50)
        superClickerCost = preferences.getLong("superClickerCost", 100)
    }

    override fun onPause() {
        super.onPause()
        saveGameData()
    }

    override fun onDestroy() {
        super.onDestroy()

        autoClickRunnable?.let {
            handler.removeCallbacks(it)
        }
    }
}