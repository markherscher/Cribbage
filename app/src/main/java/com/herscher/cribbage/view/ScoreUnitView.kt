package com.herscher.cribbage.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.View
import com.herscher.cribbage.R
import com.herscher.cribbage.model.ScoreUnit
import kotlinx.android.synthetic.main.view_score_unit.view.*

open class ScoreUnitView : ConstraintLayout {
    constructor(context: Context) : super(context, null) {
        View.inflate(context, R.layout.view_score_unit, this)
    }

    var scoreUnit: ScoreUnit? = null
        set(value) {
            if (value == null ) {
                type_text.text = ""
                value_text.text = ""
                cards_view.removeAllCards()
            } else {
                when (scoreUnit?.type) {
                    ScoreUnit.Type.RUN -> {
                        type_text.text = String.format("Run of %d", value.cards.size)
                    }
                    ScoreUnit.Type.FLUSH -> {
                        type_text.text = "Flush"
                    }
                    ScoreUnit.Type.FIFTEENS -> {
                        type_text.text = "Fifteens"
                    }
                    ScoreUnit.Type.GO -> {
                        type_text.text = "Go"
                    }
                    ScoreUnit.Type.NOBS -> {
                        type_text.text = "Nobs"
                    }
                    ScoreUnit.Type.SET -> {
                        type_text.text = String.format("Set of %d", value.cards.size)
                    }
                }

                value_text.text = String.format("%d points", value.points)

                for (card in value.cards) {
                    cards_view.addCard(card)
                }
            }
        }
}