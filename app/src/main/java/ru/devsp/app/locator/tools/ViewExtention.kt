package ru.devsp.app.locator.tools

import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

fun View.fadeIn(viewGroup : ViewGroup){
    TransitionManager.beginDelayedTransition(viewGroup, Fade())
    this.visibility = View.VISIBLE
}

fun View.fadeOut(viewGroup : ViewGroup){
    TransitionManager.beginDelayedTransition(viewGroup, Fade())
    this.visibility = View.GONE
}

fun View.slideIn(viewGroup: ViewGroup){
    TransitionManager.beginDelayedTransition(viewGroup, Slide(Gravity.BOTTOM))
    this.visibility = View.VISIBLE
}

