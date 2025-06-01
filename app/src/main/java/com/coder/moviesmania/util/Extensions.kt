package com.coder.moviesmania.util

import android.view.View
import android.widget.TextView
import com.google.android.material.chip.ChipGroup

/**
 * Extension function for TextView to set text and handle visibility based on content
 * If text is null or empty, the TextView will be hidden (View.GONE)
 * Otherwise, it will be visible and the text will be set
 */
fun TextView.setTextOrGone(text: String?) {
    if (text.isNullOrEmpty()) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
        this.text = text
    }
}

/**
 * Extension function for TextView to set text with a default value
 * If text is null or empty, the TextView will show the default value
 * Otherwise, it will show the provided text
 */
fun TextView.setTextWithDefault(text: String?, defaultValue: String) {
    this.visibility = View.VISIBLE
    this.text = if (text.isNullOrEmpty()) defaultValue else text
}

/**
 * Extension function for ChipGroup to clear all chips and hide if empty
 * or add chips and show if not empty
 */
fun ChipGroup.setChipsOrGone(items: List<*>?) {
    this.removeAllViews()
    
    if (items.isNullOrEmpty()) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
} 