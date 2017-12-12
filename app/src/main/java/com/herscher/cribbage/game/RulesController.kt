package com.herscher.cribbage.game

import com.herscher.cribbage.model.*

interface RulesController {
    fun isCardValidToPlay(game: Game, player: Player, card: Card): Boolean

    fun getRemainingDiscardCount(game: Game, player: Player): Int

    fun startNewGameIfNeeded(game: Game)

    fun discardCards(game: Game, player: Player, cards: Array<Card>)

    fun leadCard(game: Game, player: Player, card: Card, allowChanges: Boolean = true): PlayerScoring

    fun playCard(game: Game, player: Player, card: Card, allowChanges: Boolean = true): PlayerScoring

    fun getTeamNumber(game: Game, player: Player): Int
}