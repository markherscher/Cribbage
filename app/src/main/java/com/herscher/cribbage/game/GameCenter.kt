package com.herscher.cribbage.game

import com.herscher.cribbage.model.*

class GameCenter {
    private val game: Game
    private val rulesController: RulesController
    private val listeners: MutableList<Listener> = ArrayList()
    private val playerInfoMap: MutableMap<Player, PlayerInfo> = HashMap()

    constructor(game: Game, rulesController: RulesController) {
        this.game = game
        this.rulesController = rulesController
    }

    fun addListener(l: Listener) {
        if (!listeners.contains(l)) {
            listeners.add(l)
        }
    }

    fun removeListener(l: Listener) {
        listeners.remove(l)
    }

    fun resumeGame() {
        rulesController.startNewGameIfNeeded(game)
        fireStateListeners()
    }

    fun getRemainingDiscardCount(player: Player): Int {
        return rulesController.getRemainingDiscardCount(game, player)
    }

    fun discardCards(player: Player, cards: Array<Card>) {
        rulesController.discardCards(game, player, cards)

        for (l in listeners) {
            l.onCardsDiscarded(player, cards)
        }

        if (game.state == GameState.LEAD) {
            // State just switched
            for (l in listeners) {
                l.onLeadRequired(game.players[game.activePlayerIndex])
            }
        }
    }

    fun leadCard(player: Player, card: Card) {
        val playerScore = rulesController.leadCard(game, player, card)

        for (l in listeners) {
            l.onCardLead(player, card, playerScore)
        }

        when (game.state) {
            GameState.NEW -> {
                // Shouldn't happen?
            }
            GameState.PLAY -> {
                for (l in listeners) {
                    l.onPlayRequired(game.players[game.activePlayerIndex])
                }
            }
            GameState.LEAD -> {
                for (l in listeners) {
                    l.onLeadRequired(game.players[game.activePlayerIndex])
                }
            }
            GameState.ROUND_START -> {
                for (l in listeners) {
                    l.onRoundStart()
                }
            }
            GameState.COMPLETE -> {
                for (l in listeners) {
                    l.onGameComplete()
                }
            }
        }
    }

    fun playCard(player: Player, card: Card, allowChanges: Boolean) {
        val playerScoring = rulesController.playCard(game, player, card, allowChanges)

        for (l in listeners) {
            l.onCardPlayed(player, card, playerScoring)
        }

        fireStateListeners()
    }

    fun isCardValidToPlay(player: Player, card: Card): Boolean {
        return rulesController.isCardValidToPlay(game, player, card)
    }

    fun setPlayerInfo(player: Player, playerInfo: PlayerInfo?) {
        if (!game.players.contains(player)) {
            throw IllegalArgumentException("no PlayerInfo for specified player")
        }

        if (playerInfo != null) {
            playerInfoMap.put(player, playerInfo)
        } else {
            playerInfoMap.remove(player)
        }
    }

    fun getPlayerInfo(player: Player): PlayerInfo {
        val playerInfo = playerInfoMap[player]
        if (playerInfo == null) {
            throw IllegalArgumentException("player not found")
        }

        return playerInfo
    }

    val arePlayersReady: Boolean
        get() {
            return playerInfoMap.size == game.players.size
        }

    val gameState: GameState
        get() {
            return game.state
        }

    val allPlayers: List<Player>
        get() {
            return game.players
        }

    val playTotal: Int
        get() {
            return game.playTotal
        }

    val crib: List<Card>
        get() {
            return game.crib
        }

    val cutCard: Card?
        get() {
            return game.cutCard
        }

    val allCards: List<Card>
        get() {
            return game.allCards
        }

    val dealerPlayer: Player
        get() {
            return game.players[game.dealerPlayerIndex]
        }

    val activePlayer: Player
        get() {
            return game.players[game.activePlayerIndex]
        }

    val winningTeamNumber: Int?
        get() {
            return game.winningTeamNumber
        }

    val lastRoundScores: List<PlayerScoring>?
        get() = game.lastEndOfRoundScores

    private fun fireStateListeners() {
        when (game.state) {
            GameState.NEW -> {
                // Shouldn't happen?
            }
            GameState.PLAY -> {
                for (l in listeners) {
                    l.onPlayRequired(game.players[game.activePlayerIndex])
                }
            }
            GameState.LEAD -> {
                for (l in listeners) {
                    l.onLeadRequired(game.players[game.activePlayerIndex])
                }
            }
            GameState.ROUND_START -> {
                for (l in listeners) {
                    l.onRoundStart()
                }
            }
            GameState.COMPLETE -> {
                for (l in listeners) {
                    l.onGameComplete()
                }
            }
        }
    }

    interface Listener {
        fun onCardsDiscarded(player: Player, cards: Array<Card>)

        fun onCardLead(player: Player, card: Card, scores: PlayerScoring)

        fun onCardPlayed(player: Player, card: Card, scores: PlayerScoring)

        fun onRoundStart()

        fun onLeadRequired(player: Player)

        fun onPlayRequired(player: Player)

        fun onGameComplete()
    }

    class PlayerInfo(val name: String, val isLocal: Boolean)
}