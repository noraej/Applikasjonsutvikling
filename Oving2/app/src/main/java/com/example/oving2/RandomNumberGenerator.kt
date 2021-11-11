package com.example.oving2

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.widget.Toast

class RandomNumberGenerator : Activity() {
    private var upperLimit = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_number_generator)
        upperLimit = intent.getIntExtra("upperLimit", 100)
        createRandomNumber()
    }


    private fun createRandomNumber() {
        val randomNumber = (0..upperLimit).random()
        Toast.makeText(this, "$randomNumber", Toast.LENGTH_LONG).show()
        setResult(RESULT_OK, Intent().putExtra("randomNumber", randomNumber))
        finish()
    }
}