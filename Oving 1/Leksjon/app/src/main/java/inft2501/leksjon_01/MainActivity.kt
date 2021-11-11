package inft2501.leksjon_01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import inft2501.leksjon_01.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(meny: Menu): Boolean {
        super.onCreateOptionsMenu(meny)
        meny.add("Valg 1")
        meny.add("Valg 2")
        Log.d("Leksjon" , "meny laget") //vises i LogCat
        return true // gjor at menyen vil vises
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title.equals("Valg 1")) {
            Log.d("Leksjon", "Valg 1 er trykket av brukeren")
        }
        return true // hvorfor true her? Se API-dokumentasjonen!!
    }
}