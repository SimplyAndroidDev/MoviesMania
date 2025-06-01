package com.coder.moviesmania.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.coder.moviesmania.ui.fragments.NowPlayingMoviesFragment
import com.coder.moviesmania.ui.fragments.PopularMoviesFragment
import com.coder.moviesmania.ui.fragments.SavedMoviesFragment

class MoviesPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    private val fragments = listOf(
        PopularMoviesFragment.newInstance(),
        NowPlayingMoviesFragment.newInstance(),
        SavedMoviesFragment.newInstance()
    )
    
    override fun getItemCount(): Int = fragments.size
    
    override fun createFragment(position: Int): Fragment = fragments[position]
    
    fun getTabTitle(position: Int): String {
        return when (position) {
            0 -> "Popular"
            1 -> "Now Playing"
            2 -> "Saved"
            else -> ""
        }
    }
} 