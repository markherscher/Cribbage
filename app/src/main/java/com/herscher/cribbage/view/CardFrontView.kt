package com.herscher.cribbage.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.herscher.cribbage.R
import com.herscher.cribbage.model.Card
import com.herscher.cribbage.model.Face
import com.herscher.cribbage.model.Suit
import kotlinx.android.synthetic.main.view_card_front.view.*

class CardFrontView : LinearLayout {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.view_card_front, this)
    }

    var card: Card? = null
        set(value) {
            field = value
            suit_image.setImageResource(when (card?.suit) {
                Suit.CLUBS -> R.drawable.card_club
                Suit.DIAMONDS -> R.drawable.card_diamond
                Suit.HEARTS -> R.drawable.card_heart
                Suit.SPADES -> R.drawable.card_spade
                else -> 0
            })

            face_text.text = when (card?.face) {
                Face.ACE -> "A"
                Face.TWO -> "2"
                Face.THREE -> "3"
                Face.FOUR -> "4"
                Face.FIVE -> "5"
                Face.SIX -> "6"
                Face.SEVEN -> "7"
                Face.EIGHT -> "8"
                Face.NINE -> "9"
                Face.TEN -> "10"
                Face.JACK -> "J"
                Face.QUEEN -> "Q"
                Face.KING -> "K"
                else -> ""
            }
        }
}