package org.bandev.buddhaquotes.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import org.bandev.buddhaquotes.R
import org.bandev.buddhaquotes.core.Colours
import org.bandev.buddhaquotes.core.Lists
import org.bandev.buddhaquotes.core.Quotes
import org.bandev.buddhaquotes.custom.onDoubleClickListener
import org.bandev.buddhaquotes.databinding.FragmentQuoteBinding

/**
 * Quote shows quotes to the user with refresh, like & share buttons.
 * Is the first item in the [FragmentAdapter] on MainActivity
 * @author jack.txt
 * @since 1.7.0
 * @updated 09/12/2020
 */

class Quote : Fragment() {

    private var _binding: FragmentQuoteBinding? = null
    private val binding get() = _binding!!
    private var quotes = Quotes()
    private var liked = false

    /**
     * Sets the correct view of the Fragment
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when view is full made
     * @param view [View]
     * @param savedInstanceState [Bundle]
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        newQuote()
        with(binding.swipeRefresh) {
            setColorSchemeColors(Colours().getAccentColorAsInt(context))
            setOnRefreshListener { newQuote(); binding.swipeRefresh.isRefreshing = false }
        }
        binding.like.setOnClickListener {
            favouriteQuote()
        }
        binding.share.setOnClickListener {
            shareQuote()
        }
        binding.content.setOnClickListener(object : onDoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                favouriteQuote()
            }
        })
    }

    /**
     * Refreshes the quote on screen
     */

    private fun newQuote() {
        val quote = quotes.random(0, requireContext())
        binding.quote.text = quote
        binding.number.text = getString(R.string.quote_number, quotes.quotenumberglobal)
        val icon = if (Lists().queryInList(quote, "Favourites", context)) {
            liked = true
            R.drawable.heart_full_red
        } else {
            liked = false
            R.drawable.like
        }
        binding.like.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                icon
            )
        )
    }

    /**
     * Opens sharesheet for the quote on screen
     */

    private fun shareQuote() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            val text = binding.quote.text.toString() + "\n\n~Gautama Buddha"

            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /**
     * Adds the quote to "Favourites" list using [Lists.toggleToList]
     */

    internal fun favouriteQuote() {
        val quote = binding.quote.text.toString()
        if (Lists().toggleToList(quote, "Favourites", requireContext())) {
            binding.like.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.heart_full_red
                )
            )
            binding.likeAnimator.likeAnimation()
        } else {
            binding.like.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.like
                )
            )
        }
    }

    companion object {

        /**
         * Called on new instance request
         * @param position [Int]
         * @return [Quote]
         */

        fun newInstance(position: Int): Quote {
            val instance = Quote()
            val args = Bundle()
            args.putInt("position", position)
            instance.arguments = args
            return instance
        }
    }
}