package com.herscher.cribbage.game

import com.herscher.cribbage.model.*
import java.util.*

const val MAX_PLAY_COUNT = 31

class StandardRulesController : RulesController {
    private val scorer: StandardScorer
    private val cardFactory: CardFactory

    constructor(scorer: StandardScorer, cardFactory: CardFactory) {
        this.scorer = scorer
        this.cardFactory = cardFactory
    }

    override fun startNewGameIfNeeded(game: Game) {
        if (game.state == GameState.NEW) {
            game.allCards = cardFactory.createStandardDeck()
            startRound(game)
        }
    }

    override fun isCardValidToPlay(game: Game, player: Player, card: Card): Boolean {
        return player == game.activePlayer && game.playTotal + card.value <= MAX_PLAY_COUNT
    }

    override fun getRemainingDiscardCount(game: Game, player: Player): Int {
        return Math.max(0, game.options.discardCount - player.discards.size)
    }

    override fun discardCards(game: Game, player: Player, cards: Array<Card>) {
        checkState(game, GameState.ROUND_START)

        // Ensure the amount of cards to discard is legal
        if (getRemainingDiscardCount(game, player) < cards.size) {
            throw RulesViolationException(RulesViolationException.RuleViolation.DISCARD_COUNT_WRONG)
        }

        // Check for duplicate cards
        if (cards.distinct().size != cards.size) {
            throw RulesViolationException(RulesViolationException.RuleViolation.INVALID_CARD)
        }

        checkCardsOwned(player, cards)

        // Discard the cards
        player.hand.removeAll(cards)
        player.discards.addAll(cards)

        // Check to see if all cards are discarded
        for (p in game.players) {
            if (getRemainingDiscardCount(game, p) > 0) {
                // Player still needs to discard
                return
            }
        }

        // If we get this far then everyone has discarded
        game.state = GameState.LEAD
    }

    override fun leadCard(game: Game, player: Player, card: Card) {
        checkState(game, GameState.LEAD)

        if (game.players[game.activePlayerIndex] != player) {
            throw RulesViolationException(RulesViolationException.RuleViolation.WRONG_PLAYER)
        }

        checkCardsOwned(player, Array(1) { card })

        game.playedCards.add(card)
        player.hand.remove(card)
        player.played.add(card)
        game.state = GameState.PLAY
        game.activePlayerIndex = incrementPlayerIndex(game, game.activePlayerIndex)
    }

    private fun getNextPlayerWithValidCard(game: Game): Int {
        val startingPlayerIndex: Int = game.activePlayerIndex
        var currentIndex = startingPlayerIndex
        do {
            currentIndex = incrementPlayerIndex(game, currentIndex)

            for (c: Card in game.players[currentIndex].hand) {
                if (isCardValidToPlay(game, game.players[currentIndex], c)) {
                    return currentIndex
                }
            }
        } while (startingPlayerIndex != currentIndex)

        return -1
    }

    private fun getNextPlayerWithAnyCards(game: Game): Int {
        val startingPlayerIndex: Int = game.activePlayerIndex
        var currentIndex = startingPlayerIndex
        do {
            currentIndex = incrementPlayerIndex(game, currentIndex)

            if (game.players[currentIndex].hand.size > 0) {
                return currentIndex
            }
        } while (startingPlayerIndex != currentIndex)

        return -1
    }

    override fun playCard(game: Game, player: Player, card: Card, allowChanges: Boolean): PlayerScoring {
        checkState(game, GameState.PLAY)

        if (game.players[game.activePlayerIndex] != player) {
            throw RulesViolationException(RulesViolationException.RuleViolation.WRONG_PLAYER)
        }

        checkCardsOwned(player, Array(1) { card })

        if (!isCardValidToPlay(game, player, card)) {
            throw RulesViolationException(RulesViolationException.RuleViolation.INVALID_CARD)
        }

        game.playedCards.add(card)
        player.hand.remove(card)
        player.played.add(card)

        // Find the next player that can actually play a card. Check each player's hand for a card
        // that is legal to play. This is necessary so we know if a Go is scored.
        var nextPlayerIndex = getNextPlayerWithValidCard(game)

        // Score what was just played, including a score for GO if no one else can play
        val playerScoring = scorer.calculatePlayScores(game, nextPlayerIndex == game.activePlayerIndex)

        // Apply the scores
        for (s in playerScoring.scores) {
            game.players[playerScoring.playerIndex].score += s.points
        }

        // See if they just won by pegging
        if (checkForEndOfGame(game, game.players[playerScoring.playerIndex])) {
            // They did! Abort early
            return playerScoring
        }

        // Check if we have a valid player. If not, we'll have to reset the cards played and let
        // the next player with any cards lead.
        if (nextPlayerIndex >= 0) {
            // Great, we have our player
            game.activePlayerIndex = nextPlayerIndex
        } else {
            // No valid cards left for anyone; check if there's anyone left to lead
            game.playTotal = 0
            game.state = GameState.LEAD
            nextPlayerIndex = getNextPlayerWithAnyCards(game)

            if (nextPlayerIndex >= 0) {
                game.activePlayerIndex = nextPlayerIndex
            } else {
                // No one has any cards left, so the round is over
                handleEndOfRound(game)
            }
        }

        return playerScoring
    }

    override fun getTeamNumber(game: Game, player: Player): Int {
        when (game.players.size) {
            2 -> return game.players.indexOf(player)
            3 -> return game.players.indexOf(player)
            4 -> return game.players.indexOf(player) % 2
            else -> return -1 // unsupported
        }
    }

    private fun checkForEndOfGame(game: Game, player: Player): Boolean {
        if (player.score >= game.options.pointsToWin) {
            // They win! Don't add the rest of the scores.
            game.winningTeamNumber = getTeamNumber(game, player)
            game.state = GameState.COMPLETE
            return true
        }

        return false
    }

    private fun handleEndOfRound(game: Game) {
        // Score everything in order, and only add a score after processed in case previous scores
        // win the game. In that case we don't bother scoring the remaining ones.
        val allScores = scorer.calculateRoundScores(game)
        game.lastEndOfRoundScores.clear()

        // Apply each in order
        for (scoring in allScores) {
            val player = game.players[scoring.playerIndex]
            for (s in scoring.scores) {
                player.score += s.points
            }

            game.lastEndOfRoundScores.add(scoring)

            if (checkForEndOfGame(game, player)) {
                break
            }
        }

        if (game.state != GameState.COMPLETE) {
            startRound(game)
        }
    }

    private fun startRound(game: Game) {
        Collections.shuffle(game.allCards)
        game.playTotal = 0
        game.playedCards.clear()
        game.dealerPlayerIndex = incrementPlayerIndex(game, game.dealerPlayerIndex)
        game.activePlayerIndex = incrementPlayerIndex(game, game.dealerPlayerIndex)
        game.state = GameState.ROUND_START


        // Deal the cards
        val handSize = game.options.discardCount + game.options.playCount
        var cardIndex = 0
        for (player in game.players) {

            for (i in 0 until handSize) {
                player.hand.add(game.allCards[cardIndex])
                cardIndex++
            }
        }

        game.cutCard = game.allCards[cardIndex]
    }

    private fun checkState(game: Game, expectedState: GameState) {
        if (game.state != expectedState) {
            throw RulesViolationException(RulesViolationException.RuleViolation.STATE_MISMATCH)
        }
    }

    private fun checkCardsOwned(player: Player, cards: Array<Card>) {
        for (c: Card in cards) {
            if (!player.hand.contains(c)) {
                throw RulesViolationException(RulesViolationException.RuleViolation.INVALID_CARD)
            }
        }
    }

    private fun incrementPlayerIndex(game: Game, playerIndex: Int): Int {
        var newIndex = playerIndex + 1
        if (newIndex >= game.players.size) {
            newIndex = 0
        }
        return newIndex
    }
}