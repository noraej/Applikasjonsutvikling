package com.example.oving2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import java.lang.Math.log

class Task2Activity : AppCompatActivity() {

    //TODO: generer nye random tall i feltene. Ellers done

    private var number1: Int = 3
    private var number2: Int = 5
    private var upperLimit: Int = 10
    private var number1TextView: TextView? = null
    private var number2TextView: TextView? = null
    private var answerView: EditText? = null
    private var upperLimitView: EditText? = null

    private val startForResultNumberOne =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    if (result.data != null) {
                        val generatedNumber1 = result.data!!.getIntExtra("randomNumber", number1)
                        number1 = generatedNumber1
                        number1TextView?.text = "${getString(R.string.number)} 1: $number1"
                    }
                }
                else -> {
                }
            }
        }

    private val startForResultNumberTwo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    if (result.data != null) {
                        val generatedNumber2 = result.data!!.getIntExtra("randomNumber", number2)
                        number2 = generatedNumber2
                        number2TextView?.text = "${"Tall"} 2: $number2"
                    }
                }
                else -> {
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task2)

            // Get necessary views
            number1TextView = findViewById<View>(R.id.numberOne) as TextView
            number2TextView = findViewById<View>(R.id.numberTwo) as TextView
            answerView = findViewById<View>(R.id.answer) as EditText
            upperLimitView = findViewById<View>(R.id.upperlimit) as EditText

            // Update view values
            number1TextView?.text = "${getString(R.string.number)} 1: $number1"
            number2TextView?.text = "${getString(R.string.number)} 2: $number2"
            upperLimitView?.setText("$upperLimit")
    }

    fun onClickAdder(v: View?) {
        val correctAnswer = number1 + number2
        val givenAnswer = Integer.parseInt(answerView?.text.toString())
        showResult(
            if(correctAnswer == givenAnswer) getString(R.string.Riktig)
            else "${getString(R.string.Feil)}: $correctAnswer"
        )
        answerView?.getText()?.clear()
    }

    fun onClickMultiplicatior(v: View?) {
        val correctAnswer = number1 * number2
        val givenAnswer = Integer.parseInt(answerView?.text.toString())
        showResult(
            if(correctAnswer == givenAnswer) getString(R.string.Riktig)
            else "${getString(R.string.Feil)}: $correctAnswer"
        )
        answerView?.getText()?.clear()
    }

    private fun showResult(message: String){
        AlertDialog.Builder(this)
            .setTitle("Task 2")
            .setMessage(message)
            .setPositiveButton("Generer nytt tall") { _, _ ->
                run {
                    upperLimit = Integer.parseInt(upperLimitView?.text.toString())
                    val intent = Intent(this, RandomNumberGenerator::class.java)
                    intent.putExtra("upperLimit", upperLimit)
                    startForResultNumberOne.launch(intent)
                    startForResultNumberTwo.launch(intent)

                }
            }
            .setNegativeButton("Prøv igjen", null)
            .show()
    }





}