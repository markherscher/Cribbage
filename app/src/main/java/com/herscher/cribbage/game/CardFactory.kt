package com.herscher.cribbage.game

import com.herscher.cribbage.model.Card
import com.herscher.cribbage.model.Face
import com.herscher.cribbage.model.Suit

interface CardFactory {
    fun createCard(face: Face, suit: Suit): Card

    fun createStandardDeck(): ArrayList<Card>
}