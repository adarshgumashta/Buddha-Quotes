package org.bandev.buddhaquotes

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class InfoPanel : AppCompatActivity() {

    private lateinit var favs: Array<String?>
    private var settings: SharedPreferences? = null
    private var fontsize: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_panel)

        when (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor =
                ResourcesCompat.getColor(resources, R.color.transparent, null)
        } else {
            window.navigationBarColor =
                ResourcesCompat.getColor(resources, R.color.black, null)
        }

        val myToolbar = findViewById<View>(R.id.my_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        (supportActionBar ?: return).setDisplayShowTitleEnabled(false)
        window.statusBarColor = ContextCompat.getColor(this@InfoPanel, R.color.colorTop)

        val intent = intent
        val quote = intent.getStringExtra("quote")
        val textview = findViewById<TextView>(R.id.text)
        textview.text = quote

        settings = getSharedPreferences("Settings", 0)
        val textsize: String? = settings?.getString("text_size", "md")
        fontsize = when (textsize) {
            "sm" -> "25"
            "lg" -> "35"
            else -> "30"
        }

        (textview ?: return).textSize = (fontsize ?: return).toFloat()

        val favourites = getSharedPreferences("Favs", 0)
        val editor = favourites.edit()

        val contextView = findViewById<View>(R.id.view)
        favs = arrayOf(favourites.getString("fav", ""))
        val array = (favs[0] ?: return).split("//VADER//".toRegex()).toTypedArray()
        val list: MutableList<String> = ArrayList(listOf(*array))

        val fab = findViewById<FloatingActionButton>(R.id.delete)

        fab.setOnClickListener {
            favs = arrayOf(favourites.getString("fav", ""))
            var array =
                (favs[0] ?: return@setOnClickListener).split("//VADER//".toRegex()).toTypedArray()
            val list: MutableList<String> = ArrayList(listOf(*array))
            val text = textview.text as String
            list.remove(text)
            Log.d("Array", list.toString())
            array = list.toTypedArray()
            var sb = ""
            for (i in array.indices) {
                if (array[i] != "") {
                    sb = sb + array[i] + "//VADER//"
                }
            }
            Log.d("Array", sb)
            editor.putString("fav", sb)
            editor.apply()

            val myIntent = Intent(this@InfoPanel, Favourites::class.java)
            this@InfoPanel.startActivity(myIntent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.fav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                val myIntent = Intent(this@InfoPanel, Favourites::class.java)
                this@InfoPanel.startActivity(myIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val myIntent = Intent(this@InfoPanel, Favourites::class.java)
        this@InfoPanel.startActivity(myIntent)
        finish()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Add your animation
        val myIntent = Intent(this@InfoPanel, Favourites::class.java)
        this@InfoPanel.startActivity(myIntent)
        finish()
    }
}
