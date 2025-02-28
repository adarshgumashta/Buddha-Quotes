/**

Buddha Quotes
Copyright (C) 2021  BanDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

package org.bandev.buddhaquotes.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.material.snackbar.Snackbar
import org.bandev.buddhaquotes.R
import org.bandev.buddhaquotes.adapters.AddQuoteRecycler
import org.bandev.buddhaquotes.core.*
import org.bandev.buddhaquotes.databinding.AddlistContentBinding
import org.bandev.buddhaquotes.items.AddQuoteItem
import java.util.*

/**
 * The activity where the user selects a quote to add to their list
 * If calling, make sure to send the name of the list they want with
 * the key "list" in the intent
 **/
class AddToListActivity : LocalizationActivity(), AddQuoteRecycler.ClickListener {

    private lateinit var binding: AddlistContentBinding
    private lateinit var recyclerAdapter: AddQuoteRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set theme, navigation bar and language
        setAccentColour(this)
        window.setStatusBarAsAccentColour(this)
        window.setNavigationBarColourDefault(this)

        // Setup view binding
        binding = AddlistContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        with(binding.toolbar) {
            navigationIcon = context.backIcon()
            setBackgroundColor(toolbarColour(context))
            setNavigationOnClickListener { onBackPressed() }
        }

        recyclerAdapter = AddQuoteRecycler(genList(), this@AddToListActivity)

        with(binding.allQuotesRecycler) {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    override fun onClick(quote: String) {
        val list = (intent.extras ?: return).getString("list").toString()
        val lists2 = ListsV2(this)
        val quoteID = Quotes().getFromString(quote, this)
        if (!lists2.queryInList(quoteID, list)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) binding.root.performHapticFeedback(
                HapticFeedbackConstants.CONFIRM
            )
            else binding.root.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            lists2.addToList(quoteID, list)
            val intent2 = Intent(this, ScrollingActivity::class.java)
            intent2.putExtra("list", list)
            startActivity(intent2)
            finish()
            overridePendingTransition(
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right
            )

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) binding.root.performHapticFeedback(
                HapticFeedbackConstants.REJECT
            )
            else binding.root.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val outName = if (list == "Favourites") getString(R.string.favourites)
            else list
            Snackbar.make(
                binding.root,
                getString(R.string.duplicate) + " $outName",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun genList(): ArrayList<AddQuoteItem> {
        val list = ArrayList<AddQuoteItem>()
        val max = 237
        var i = 1
        while (i != max) {
            val quote = Quotes().getQuote(i, this)
            list.add(AddQuoteItem(quote))
            i++
        }

        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        val searchItem = menu!!.findItem(R.id.appSearchBar)
        val searchView = searchItem.actionView as SearchView
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val searchCloseBtn =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val searchEditText =
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        searchCloseBtn.setColorFilter(Color.WHITE)
        searchIcon.setColorFilter(Color.WHITE)

        with(searchEditText) {
            setTextColor(Color.WHITE)
            setHintTextColor(Color.WHITE)
            hint = getString(R.string.searchHint)
        }

        with(searchView) {
            imeOptions = EditorInfo.IME_ACTION_DONE
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    recyclerAdapter.filter.filter(newText)
                    return false
                }
            })
        }
        return true
    }

    override fun onBackPressed() {
        val list = (intent.getStringExtra("list") ?: return).toString()
        startActivity(Intent(this, ScrollingActivity::class.java).putExtra("list", list))
        finish()
        overridePendingTransition(
            R.anim.anim_slide_in_right,
            R.anim.anim_slide_out_right
        )
    }
}
