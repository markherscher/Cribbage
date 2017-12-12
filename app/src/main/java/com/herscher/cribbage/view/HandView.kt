package com.herscher.cribbage.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.herscher.cribbage.model.Card

class HandView : LinearLayout {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

    }

    var areClicksEnabled: Boolean = true
        set(value) {
            highlightedCardView?.isHighlighted = false
            highlightedCardView = null
        }

    var isFacedown: Boolean = true
        set(value) {
            field = value
            val allChildren = (0..getChildCount() - 1).map { getChildAt(it) }

            for (v in allChildren) {
                if (v is CardView) {
                    v.isFacedown = value
                }
            }
        }

    var listener: Listener? = null

    private var highlightedCardView: CardView? = null
    private val clickHandler = ClickHandler()

    fun addCard(card: Card) {
        val layoutParams: LinearLayout.LayoutParams
        if (orientation == LinearLayout.VERTICAL) {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
        } else {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
        }

        val cardView = CardView(context)
        cardView.card = card
        cardView.isFacedown = isFacedown
        cardView.setOnClickListener(clickHandler)
        addView(cardView, layoutParams)
    }

    fun removeCard(card: Card) {
        val allChildren = (0..getChildCount() - 1).map { getChildAt(it) }

        for (v in allChildren) {
            if (v is CardView) {
                if (v.card == card) {
                    if (v == highlightedCardView) {
                        highlightedCardView = null
                    }

                    v.card = null
                    v.setOnClickListener(null)
                    removeView(v)
                }
            }
        }
    }

    fun removeAllCards() {
        val allChildren = (0..getChildCount() - 1).map { getChildAt(it) }

        for (v in allChildren) {
            if (v is CardView) {
                v.card = null
                v.setOnClickListener(null)
                removeView(v)
            }
        }
    }

    private inner class ClickHandler : View.OnClickListener {
        override fun onClick(v: View?) {
            if (areClicksEnabled && v is CardView) {
                val card = v.card
                if (card != null) {
                    if (highlightedCardView == v) {
                        // They clicked the same card again, so fire listener for playing
                        v.isHighlighted = false
                        highlightedCardView = null
                        listener?.onCardPlayed(card)
                    } else if (highlightedCardView != null) {
                        // They selected a different card, so unhighlight it
                        highlightedCardView?.isHighlighted = false
                        highlightedCardView = null
                    } else {
                        // They selected a card while nothing was highlighted
                        val isLegal = listener?.onAttemptedCardHighlight(card) ?: false
                        if (isLegal) {
                            v.isHighlighted = true
                            highlightedCardView = v
                        }
                    }
                }
            }
        }
    }

    interface Listener {
        fun onAttemptedCardHighlight(card: Card): Boolean

        fun onCardPlayed(card: Card)
    }
}