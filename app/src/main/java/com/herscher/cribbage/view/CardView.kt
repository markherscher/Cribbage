package com.herscher.cribbage.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.herscher.cribbage.R
import com.herscher.cribbage.model.Card
import kotlinx.android.synthetic.main.view_card.view.*

class CardView : FrameLayout {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.view_card, this)
        updateBackground()
    }

    var isHighlighted: Boolean = false
        set(value) {
            field = value
            updateBackground()
        }

    var isFacedown: Boolean = true
        set(value) {
            field = value
            updateBackground()
        }

    var card: Card?
        set(value) {
            card_front_view.card = value
        }
        get() {
            return card_front_view.card
        }

    private fun updateBackground() {
        if (isFacedown) {
            card_front_view.visibility = View.INVISIBLE
            setBackgroundColor(Color.TRANSPARENT)
            setBackgroundResource(R.drawable.card_back)
        } else if (isHighlighted) {
            card_front_view.visibility = View.VISIBLE
            setBackgroundColor(Color.YELLOW)
        } else {
            card_front_view.visibility = View.VISIBLE
            setBackgroundColor(Color.WHITE)
        }
    }
}