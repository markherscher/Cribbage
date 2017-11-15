package com.herscher.cribbage.game

import com.herscher.cribbage.model.*

class GameCenter {
    val game: Game
    val rulesController: RulesController
    val listeners: MutableList<Listener> = ArrayList()

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

        when (game.state) {
            GameState.ROUND_START -> {
                for (l in listeners) {
                    l.onRoundStart()
                }
            }

            GameState.LEAD -> {
                for (l in listeners) {
                    l.onLeadRequired(game.activePlayer)
                }
            }

            GameState.PLAY -> {
                for (l in listeners) {
                    l.onPlayRequired(game.activePlayer)
                }
            }

            GameState.COMPLETE -> {
                for (l in listeners) {
                    l.onGameComplete()
                }
            }

            else -> {
                throw IllegalStateException("unhandled game state")
            }
        }
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
        rulesController.leadCard(game, player, card)

        for (l in listeners) {
            l.onCardLead(player, card)
        }

        if (game.state == GameState.PLAY) {
            // State just switched
            for (l in listeners) {
                l.onPlayRequired(game.players[game.activePlayerIndex])
            }
        }
    }

    fun playCard(player: Player, card: Card, allowChanges: Boolean) {
        val playerScoring = rulesController.playCard(game, player, card, allowChanges)

        for (l in listeners) {
            l.onCardPlayed(player, card, playerScoring)
        }

        when (game.state) {
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

    fun isCardValidToPlay(player: Player, card: Card): Boolean {
        return rulesController.isCardValidToPlay(game, player, card)
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

    val cutCard: Card
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

    interface Listener {
        fun onCardsDiscarded(player: Player, cards: Array<Card>)

        fun onCardLead(player: Player, card: Card)

        fun onCardPlayed(player: Player, card: Card, scores: PlayerScoring)

        fun onRoundStart()

        fun onLeadRequired(player: Player)

        fun onPlayRequired(player: Player)

        fun onGameComplete()
    }
}