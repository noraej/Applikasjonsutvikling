package com.example.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    val firstname = "Nora"
    val surname = "Evensen Jansrud"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(meny: Menu): Boolean{
        super.onCreateOptionsMenu(meny)
        meny.add(firstname)
        meny.add(surname)
        Log.d("INFT2501","meny laget") //vises i LogCat
        return true // gjor at menyen vil vises
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        if (item.getTitle().equals(firstname)){
            Log.w("Menu", "$firstname clicked") }
        if(item.getTitle().equals(surname)){
            Log.e("Menu", "$surname clicked") }
        return true
    }

}