package com.example.oving2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class Task1Activity : Activity() {
    private val randomNumberRequestCode = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickGenerateRandomNumber(v: View?){
        val intent = Intent(this, RandomNumberGenerator::class.java)
        startActivityForResult(intent, randomNumberRequestCode)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         data: Intent) {
        if (resultCode != RESULT_OK) {
            Log.e("onActivityResult()", "Noe gikk galt")
            return
        }
        if (requestCode == randomNumberRequestCode) {
            var randomNumber = data.getIntExtra("randomNumber", 20)
            val textView = findViewById<View>(R.id.textView) as TextView
            textView.text = "Random number: $randomNumber"
        }
    }


}