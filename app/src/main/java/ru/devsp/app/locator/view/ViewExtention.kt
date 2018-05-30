package ru.devsp.app.locator.view

import android.transition.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

fun View.fadeIn(viewGroup: ViewGroup) {
    TransitionManager.beginDelayedTransition(viewGroup, Fade())
    this.visibility = View.VISIBLE
}

fun View.fadeOut(viewGroup: ViewGroup) {
    TransitionManager.beginDelayedTransition(viewGroup, Fade())
    this.visibility = View.GONE
}

fun View.slideIn(viewGroup: ViewGroup) {
    TransitionManager.beginDelayedTransition(viewGroup, Slide(Gravity.BOTTOM))
    this.visibility = View.VISIBLE
}

fun View.toggle(viewGroup: ViewGroup) {
    val transition = TransitionSet()
    transition.addTransition(Fade())
    transition.ordering = TransitionSet.ORDERING_TOGETHER
    TransitionManager.beginDelayedTransition(viewGroup, transition)

    if (this.visibility == View.VISIBLE) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}


