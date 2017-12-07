package com.herscher.cribbage.model

import android.text.TextUtils
import java.util.ArrayList

data class Player(val id: String,
                  var score: Int = 0,
                  var hand: MutableList<Card> = ArrayList(),
                  var discards: MutableList<Card> = ArrayList(),
                  var played: MutableList<Card> = ArrayList()) {

    override fun equals(other: Any?): Boolean {
        return if (other is Player) {
            TextUtils.equals(other.id, id)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}