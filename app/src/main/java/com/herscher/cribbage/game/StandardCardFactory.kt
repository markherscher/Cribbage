package com.herscher.cribbage.game

import com.herscher.cribbage.model.Card
import com.herscher.cribbage.model.Face
import com.herscher.cribbage.model.Suit

class StandardCardFactory: CardFactory {
    override fun createStandardDeck(): ArrayList<Card> {
        val cards = ArrayList<Card>()

        cards.add(createCard(Face.KING, Suit.CLUBS))
        cards.add(createCard(Face.QUEEN, Suit.CLUBS))
        cards.add(createCard(Face.JACK, Suit.CLUBS))
        cards.add(createCard(Face.TEN, Suit.CLUBS))
        cards.add(createCard(Face.NINE, Suit.CLUBS))
        cards.add(createCard(Face.EIGHT, Suit.CLUBS))
        cards.add(createCard(Face.SEVEN, Suit.CLUBS))
        cards.add(createCard(Face.SIX, Suit.CLUBS))
        cards.add(createCard(Face.FIVE, Suit.CLUBS))
        cards.add(createCard(Face.FOUR, Suit.CLUBS))
        cards.add(createCard(Face.THREE, Suit.CLUBS))
        cards.add(createCard(Face.TWO, Suit.CLUBS))
        cards.add(createCard(Face.ACE, Suit.CLUBS))

        cards.add(createCard(Face.KING, Suit.DIAMONDS))
        cards.add(createCard(Face.QUEEN, Suit.DIAMONDS))
        cards.add(createCard(Face.JACK, Suit.DIAMONDS))
        cards.add(createCard(Face.TEN, Suit.DIAMONDS))
        cards.add(createCard(Face.NINE, Suit.DIAMONDS))
        cards.add(createCard(Face.EIGHT, Suit.DIAMONDS))
        cards.add(createCard(Face.SEVEN, Suit.DIAMONDS))
        cards.add(createCard(Face.SIX, Suit.DIAMONDS))
        cards.add(createCard(Face.FIVE, Suit.DIAMONDS))
        cards.add(createCard(Face.FOUR, Suit.DIAMONDS))
        cards.add(createCard(Face.THREE, Suit.DIAMONDS))
        cards.add(createCard(Face.TWO, Suit.DIAMONDS))
        cards.add(createCard(Face.ACE, Suit.DIAMONDS))

        cards.add(createCard(Face.KING, Suit.HEARTS))
        cards.add(createCard(Face.QUEEN, Suit.HEARTS))
        cards.add(createCard(Face.JACK, Suit.HEARTS))
        cards.add(createCard(Face.TEN, Suit.HEARTS))
        cards.add(createCard(Face.NINE, Suit.HEARTS))
        cards.add(createCard(Face.EIGHT, Suit.HEARTS))
        cards.add(createCard(Face.SEVEN, Suit.HEARTS))
        cards.add(createCard(Face.SIX, Suit.HEARTS))
        cards.add(createCard(Face.FIVE, Suit.HEARTS))
        cards.add(createCard(Face.FOUR, Suit.HEARTS))
        cards.add(createCard(Face.THREE, Suit.HEARTS))
        cards.add(createCard(Face.TWO, Suit.HEARTS))
        cards.add(createCard(Face.ACE, Suit.HEARTS))

        cards.add(createCard(Face.KING, Suit.SPADES))
        cards.add(createCard(Face.QUEEN, Suit.SPADES))
        cards.add(createCard(Face.JACK, Suit.SPADES))
        cards.add(createCard(Face.TEN, Suit.SPADES))
        cards.add(createCard(Face.NINE, Suit.SPADES))
        cards.add(createCard(Face.EIGHT, Suit.SPADES))
        cards.add(createCard(Face.SEVEN, Suit.SPADES))
        cards.add(createCard(Face.SIX, Suit.SPADES))
        cards.add(createCard(Face.FIVE, Suit.SPADES))
        cards.add(createCard(Face.FOUR, Suit.SPADES))
        cards.add(createCard(Face.THREE, Suit.SPADES))
        cards.add(createCard(Face.TWO, Suit.SPADES))
        cards.add(createCard(Face.ACE, Suit.SPADES))

        return cards
    }

    override fun createCard(face: Face, suit: Suit): Card {
        return Card(face, suit, getOrder(face), getValue(face))
    }

    private fun getValue(face: Face): Int {
        return when (face) {
            Face.NINE -> 9
            Face.EIGHT -> 8
            Face.SEVEN -> 7
            Face.SIX -> 6
            Face.FIVE -> 5
            Face.FOUR -> 4
            Face.THREE -> 3
            Face.TWO -> 2
            Face.ACE -> 1
            else -> 10
        }
    }

    private fun getOrder(face: Face): Int {
        return when (face) {
            Face.KING -> 13
            Face.QUEEN -> 12
            Face.JACK -> 11
            Face.TEN -> 10
            Face.NINE -> 9
            Face.EIGHT -> 8
            Face.SEVEN -> 7
            Face.SIX -> 6
            Face.FIVE -> 5
            Face.FOUR -> 4
            Face.THREE -> 3
            Face.TWO -> 2
            Face.ACE -> 1
        }
    }
}