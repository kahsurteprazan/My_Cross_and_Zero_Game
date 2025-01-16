package com.example.mycrossandzerogame

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.Transliterator.Position
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mycrossandzerogame.SettingsActivity.Companion.PREF_LVL
import com.example.mycrossandzerogame.SettingsActivity.Companion.PREF_RULES
import com.example.mycrossandzerogame.SettingsActivity.Companion.PREF_SOUND_VALUE
import com.example.mycrossandzerogame.SettingsActivity.SettingsInfo
import com.example.mycrossandzerogame.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameField: Array<Array<String>>

    private lateinit var settingsInfo: SettingsActivity.SettingsInfo

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)

        binding.toPopupMenu.setOnClickListener {
            showPopupMenu()
        }

        binding.toGameClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.cell11.setOnClickListener {
            makeStepOfUser(1, 1)
        }

        binding.cell12.setOnClickListener {
            makeStepOfUser(1, 2)
        }

        binding.cell13.setOnClickListener {
            makeStepOfUser(1, 3)
        }

        binding.cell21.setOnClickListener {
            makeStepOfUser(2, 1)
        }

        binding.cell22.setOnClickListener {
            makeStepOfUser(2, 2)
        }

        binding.cell23.setOnClickListener {
            makeStepOfUser(2, 3)
        }

        binding.cell31.setOnClickListener {
            makeStepOfUser(3, 1)
        }

        binding.cell32.setOnClickListener {
            makeStepOfUser(3, 2)
        }

        binding.cell33.setOnClickListener {
            makeStepOfUser(3, 3)
        }

        setContentView(binding.root)

        val time = intent.getLongExtra(MainActivity.EXTRA_TIME, 0)
        val gameField = intent.getStringExtra(MainActivity.EXTRA_GAME_FIELD)

        if (gameField != null && time != 0L && gameField != "") {
            restartGame(time, gameField)
        } else {
            initGameField()
        }

        settingsInfo = getStingsInfo()

        mediaPlayer = MediaPlayer.create(this, R.raw.test2)
        mediaPlayer.isLooping = true
        setVolumeMediaPlayer(settingsInfo.soundValue)

        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

    private fun setVolumeMediaPlayer(soundValue: Int) {
        val volume = soundValue / 100.0
        mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
    }

    private fun initGameField() {
        gameField = Array(3) { Array(3) { "" } }
    }

    private fun makeStep(row: Int, colum: Int, symbol: String) {
        gameField[row][colum] = symbol

        makeStepUI("$row$colum", symbol)
    }

    private fun makeStepUI(position: String, symbol: String) {
        val resId = when (symbol) {
            "x" -> R.drawable.ic_cross
            "0" -> R.drawable.ic_zero
            else -> return
        }
        when (position) {
            "11" -> binding.cell11.setImageResource(resId)
            "12" -> binding.cell11.setImageResource(resId)
            "13" -> binding.cell11.setImageResource(resId)
            "21" -> binding.cell11.setImageResource(resId)
            "22" -> binding.cell11.setImageResource(resId)
            "23" -> binding.cell11.setImageResource(resId)
            "31" -> binding.cell11.setImageResource(resId)
            "32" -> binding.cell11.setImageResource(resId)
            "33" -> binding.cell11.setImageResource(resId)
        }
    }

    private fun makeStepOfUser(row: Int, colum: Int) {
        if (isEmptyField(row, colum)) {
            makeStep(row, colum, "x")

            val status = checkGameField(row, colum, "x")
            if (status.status) {
                showGameStatus(STATUS_PLAYER_WIN)
                return
            }

            if (!isFilledGameField()) {
                makeStepOfAI()

                val statusAi = checkGameField(row, colum, "0")
                if (statusAi.status) {
                    showGameStatus(STATUS_PLAYER_LOSE)
                    return
                }

                if (isFilledGameField()) {
                    showGameStatus(STATUS_PLAYER_DRAW)
                    return
                }
            } else {
                showGameStatus(STATUS_PLAYER_DRAW)
                return
            }


        } else {
            Toast.makeText(this, "Поле уже занято", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isEmptyField(row: Int, colum: Int): Boolean {
        return gameField[row][colum] == " "
    }

    private fun makeStepOfAI() {
        when (settingsInfo.lvl) {
            0 -> makeStepOfAIEasyLvl()
            1 -> makeStepOfAIMediumLvl()
            2 -> makeStepOfAIHardLvl()
        }
    }

    private fun makeStepOfAIHardLvl() {
        TODO("Not yet implemented")
    }

    private fun makeStepOfAIMediumLvl() {
        TODO("Not yet implemented")
    }

    private fun makeStepOfAIEasyLvl() {
        var randRow = 0
        var randColum = 0

        do {
            randRow = (0..2).random()
            randColum = (0..2).random()
        } while (isEmptyField(randRow, randColum))

        makeStep(randRow, randColum, "0")
    }

    private fun checkGameField(row: Int, colum: Int, symbol: String): StatusInfo {
        var row = 0
        var colum = 0
        var leftDiagonal = 0
        var rightDiagonal = 0
        val n = gameField.size

        for (i in 0..2) {
            if (gameField[row][i] == symbol)
                colum++
            else if (gameField[colum][i] == symbol)
                row++
            else if (gameField[i][i] == symbol)
                leftDiagonal++
            else if (gameField[i][n - i - 1] == symbol)
                rightDiagonal++
        }

        return when (settingsInfo.rules) {
            1 -> {
                if (colum == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }

            2 -> {
                if (row == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }

            3 -> {
                if (colum == n || row == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }

            4 -> {
                if (leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }

            5 -> {
                if (colum == n || leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }

            6 -> {
                if (row == n || leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }

            7 -> {
                if (colum == n || row == n || leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            else -> StatusInfo(false, "")
        }
    }

    data class StatusInfo(val status: Boolean, val side: String)

    private fun showGameStatus(status: Int) {
        val dialog = Dialog(this, R.style.Theme_MyCrossAndZeroGame)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
            setContentView(R.layout.dialog_popup_status_game)
            setCancelable(true)
        }

        val image = dialog.findViewById<ImageView>(R.id.dialog_image)
        val text = dialog.findViewById<TextView>(R.id.dialog_text)
        val button = dialog.findViewById<Button>(R.id.dialog_ok)

        button.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        when (status) {
            STATUS_PLAYER_WIN -> {
                image.setImageResource(R.drawable.ic_win)
                text.text = getString(R.string.dialog_status_win)
            }

            STATUS_PLAYER_LOSE -> {
                image.setImageResource(R.drawable.ic_lose)
                text.text = getString(R.string.dialog_status_lose)
            }

            STATUS_PLAYER_DRAW -> {
                image.setImageResource(R.drawable.ic_draw)
                text.text = getString(R.string.dialog_status_draw)
            }
        }

        dialog.show()
    }

    private fun showPopupMenu() {
        val dialog = Dialog(this, R.style.Theme_MyCrossAndZeroGame)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
            setContentView(R.layout.dialog_popup_menu)
            setCancelable(true)
        }

        val toContinue = dialog.findViewById<ImageView>(R.id.dialog_continue)
        val toSetting = dialog.findViewById<TextView>(R.id.dialog_settings)
        val toExit = dialog.findViewById<Button>(R.id.dialog_exit)

        toContinue.setOnClickListener {
            dialog.hide()
        }

        toSetting.setOnClickListener {
            dialog.hide()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

            settingsInfo = getStingsInfo()
            setVolumeMediaPlayer(settingsInfo.soundValue)
        }

        toExit.setOnClickListener {
            val elapsedMills = SystemClock.elapsedRealtime() - binding.chronometer.base
            val gameField = convertGameFieldToString(gameField)
            saveGame(elapsedMills, gameField)
            dialog.dismiss()
            onBackPressedDispatcher.onBackPressed()
        }

        dialog.show()
    }

    private fun isFilledGameField(): Boolean {
        gameField.forEach { strings ->
            if (strings.find { it == " " } != null)
                return false
        }
        return true
    }

    private fun convertGameFieldToString(gameField: Array<Array<String>>): String {
        val tmpArray = arrayListOf<String>()
        gameField.forEach { tmpArray.add(it.joinToString(";")) }
        return tmpArray.joinToString("\n")
    }

    private fun saveGame(time: Long, gameField: String) {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()) {
            putLong(PREF_TIME, time)
            putString(PREF_GAME_FIELD, gameField)
            apply()
        }
    }

    private fun restartGame(time: Long, gameField: String) {
        binding.chronometer.base = SystemClock.elapsedRealtime() - time

        this.gameField = arrayOf()

        val rows = gameField.split("\n")

        rows.forEach {
            val columns = it.split(";")
            this.gameField += columns.toTypedArray()
        }

        this.gameField.forEachIndexed { indexRow, strings ->
            strings.forEachIndexed { indexColumn, s ->
                makeStep(indexRow, indexColumn, this.gameField[indexRow][indexRow])
            }
        }
    }

    private fun getStingsInfo(): SettingsInfo {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)) {
            val soundValue = getInt(PREF_SOUND_VALUE, 0)
            val lvl = getInt(PREF_LVL, 0)
            val rules = getInt(PREF_RULES, 0)

            return SettingsInfo(soundValue, lvl, rules)
        }
    }

    companion object {
        const val STATUS_PLAYER_WIN = 1
        const val STATUS_PLAYER_LOSE = 2
        const val STATUS_PLAYER_DRAW = 3

        const val PREF_TIME = "pref_time"
        const val PREF_GAME_FIELD = "pref_game_field"
    }
}