package com.herscher.cribbage.model

data class Card(val face:
                Face = Face.ACE,
                val suit: Suit = Suit.CLUBS,
                val order: Int = 1,
                val value: Int = 10) {

}
