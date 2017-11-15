package com.herscher.cribbage.model

data class Game(
        var state: GameState = GameState.ROUND_START,
        val options: Options = Options(),
        val players: List<Player> = emptyList(),
        var playTotal: Int = 0,
        val crib: MutableList<Card> = ArrayList(),
        var cutCard: Card = Card(),
        var allCards: MutableList<Card> = ArrayList(),
        val playedCards: MutableList<Card> = ArrayList(),
        var dealerPlayerIndex: Int = -1,
        var activePlayerIndex: Int = -1,
        var winningTeamNumber: Int? = null,
        val lastEndOfRoundScores: MutableList<PlayerScoring> = ArrayList()) {

    val activePlayer: Player
        get() {
            return players[activePlayerIndex]
        }
}