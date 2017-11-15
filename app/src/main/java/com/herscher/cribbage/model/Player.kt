package com.herscher.cribbage.model

import java.util.ArrayList

data class Player(val id: String,
                  var score: Int = 0,
                  var hand: MutableList<Card> = ArrayList(),
                  var discards: MutableList<Card> = ArrayList(),
                  var played: MutableList<Card> = ArrayList()) {
}