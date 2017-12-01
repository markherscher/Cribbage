package com.herscher.cribbage.model

data class Game(
        var state: GameState = GameState.NEW,
        val options: Options = Options(),
        val players: List<Player> = emptyList(),
        var playTotal: Int = 0,
        var cutCard: Card? = null,
        val crib: MutableList<Card> = ArrayList(),
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