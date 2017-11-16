package com.herscher.cribbage.game

import android.util.SparseIntArray
import com.herscher.cribbage.model.*

const val FIFTEENS = 15
const val MAX_PLAY = 31
const val MIN_RUN_LENGTH = 3
const val POINTS_PER_CARD_IN_RUN = 1
const val POINTS_PER_FIFTEENS = 2
const val POINTS_PER_2_SET = 2
const val POINTS_PER_3_SET = 6
const val POINTS_PER_4_SET = 12
const val POINTS_PER_GO = 1
const val POINTS_PER_31_GO = 2

open class StandardScorer : Scorer {
    override fun calculatePlayScores(game: Game, scoreForGo: Boolean): PlayerScoring {
        val allScores = ArrayList<ScoreUnit>()

        var scoreUnit: ScoreUnit? = playScoreFifteens(game)
        if (scoreUnit != null) {
            allScores.add(scoreUnit)
        }

        scoreUnit = playScorePairs(game)
        if (scoreUnit != null) {
            allScores.add(scoreUnit)
        }

        scoreUnit = playScoreRuns(game)
        if (scoreUnit != null) {
            allScores.add(scoreUnit)
        }

        if (scoreForGo) {
            // Count all cards to see if they hit 31 exactly
            var total = 0
            for (card in game.playedCards) {
                total += card.value
            }

            val points = if (total == MAX_PLAY) POINTS_PER_31_GO else POINTS_PER_GO
            allScores.add(ScoreUnit(emptyList(), points, ScoreUnit.Type.GO))
        }

        return PlayerScoring(game.activePlayerIndex, allScores)
    }

    override fun calculateRoundScores(game: Game): List<PlayerScoring> {
        val playerScoringList = ArrayList<PlayerScoring>()

        // Scoring starts with the player after the dealer
        val startingIndex = incrementPlayerIndex(game, game.dealerPlayerIndex)
        var playerIndex = startingIndex
        do {
            val scoreUnits = ArrayList<ScoreUnit>()

            // Sort the player's hands adding the cut card to it
            val allCards = ArrayList(game.players[playerIndex].hand)
            allCards.add(game.cutCard)
            val sortedCards = allCards.sortedWith(compareBy { it.order })

            scoreUnits.addAll(roundScoreFifteens(sortedCards))
            scoreUnits.addAll(roundScorePairs(sortedCards))
            scoreUnits.addAll(roundScoreRuns(sortedCards))
            scoreUnits.addAll(roundScoreFlush(game.players[playerIndex].hand, game.cutCard, false))
            playerScoringList.add(PlayerScoring(playerIndex, scoreUnits))

            playerIndex = incrementPlayerIndex(game, playerIndex)
        } while (playerIndex != startingIndex)

        // Now score the crib for the dealer
        val scoreUnits = ArrayList<ScoreUnit>()
        val allCards = ArrayList(game.crib)
        allCards.add(game.cutCard)
        val sortedCards = allCards.sortedWith(compareBy { it.order })

        scoreUnits.addAll(roundScoreFifteens(sortedCards))
        scoreUnits.addAll(roundScorePairs(sortedCards))
        scoreUnits.addAll(roundScoreRuns(sortedCards))
        scoreUnits.addAll(roundScoreFlush(game.crib, game.cutCard, true))
        playerScoringList.add(PlayerScoring(game.dealerPlayerIndex, scoreUnits))

        return playerScoringList
    }

    private fun playScoreFifteens(game: Game): ScoreUnit? {
        var index = game.playedCards.size - 1
        var total = 0

        while (index >= 0) {
            total += game.playedCards[index].value

            if (total == FIFTEENS) {
                val cardsInvolved: List<Card> = game.playedCards.subList(index, game.playedCards.size)
                return ScoreUnit(cardsInvolved, POINTS_PER_FIFTEENS, ScoreUnit.Type.FIFTEENS)
            } else if (total > FIFTEENS) {
                break
            }

            index++
        }

        return null
    }

    private fun playScoreRuns(game: Game): ScoreUnit? {
        // Complication: the run can be out of order
        // As long as the difference between largest and smallest card in the run equals the
        // run length - 1 the run is valid.
        var smallest = Int.MAX_VALUE
        var largest = Int.MIN_VALUE
        var runLength = 0

        for (card in game.playedCards) {
            runLength++

            if (card.order < smallest) {
                smallest = card.order
            }

            if (card.order > largest) {
                largest = card.order
            }

            if ((largest - smallest) + 1 == runLength) {
                // The run is valid so far
            } else {
                // Run is over
                break
            }
        }

        return if (runLength >= MIN_RUN_LENGTH) {
            val cardsInvolved: List<Card> = game.playedCards.subList(0, runLength)
            ScoreUnit(cardsInvolved, runLength * POINTS_PER_CARD_IN_RUN, ScoreUnit.Type.RUN)
        } else {
            null
        }
    }

    private fun playScorePairs(game: Game): ScoreUnit? {
        var index = game.playedCards.size - 1
        var repeatCount = 0
        val compareFace = game.playedCards[index].face

        while (index >= 0) {
            if (game.playedCards[index].face == compareFace) {
                repeatCount++
            } else {
                break
            }

            index--
        }

        val cardsInvolved: List<Card> = game.playedCards.subList(0, repeatCount)

        return when (repeatCount) {
            2 -> ScoreUnit(cardsInvolved, POINTS_PER_2_SET, ScoreUnit.Type.SET)
            3 -> ScoreUnit(cardsInvolved, POINTS_PER_3_SET, ScoreUnit.Type.SET)
            4 -> ScoreUnit(cardsInvolved, POINTS_PER_4_SET, ScoreUnit.Type.SET)
            else -> null
        }
    }

    private fun roundScoreFifteens(sortedCards: List<Card>): List<ScoreUnit> {
        val scoreUnitList = ArrayList<ScoreUnit>()

        for (i in sortedCards.indices) {
            var baseIndex = i + 1

            while (baseIndex < sortedCards.size) {
                var indexToAdd = baseIndex
                val includedCards = ArrayList<Card>()
                var total = sortedCards[i].value

                while (indexToAdd < sortedCards.size) {
                    total += sortedCards[indexToAdd].value
                    includedCards.add(sortedCards[indexToAdd])

                    if (total == FIFTEENS) {
                        scoreUnitList.add(ScoreUnit(ArrayList(includedCards),
                                POINTS_PER_FIFTEENS, ScoreUnit.Type.FIFTEENS))
                        break
                    } else if (total > FIFTEENS) {
                        break
                    }

                    indexToAdd++
                }

                baseIndex++
            }
        }

        return scoreUnitList
    }

    private fun roundScoreFlush(cardList: List<Card>, cutCard: Card, isCrib: Boolean): List<ScoreUnit> {
        val scoreUnitList = ArrayList<ScoreUnit>()
        for (suit in Suit.values()) {
            // All cards in the hand/crib must be of the same suit
            if (countSuit(cardList, suit) == cardList.size) {
                if (isCrib) {
                    // If it's the crib all five cards must be of the same suit
                    if (cutCard.suit == suit) {
                        val cardsInvolved = ArrayList(cardList)
                        cardsInvolved.add(cutCard)
                        scoreUnitList.add(ScoreUnit(cardsInvolved, cardsInvolved.size, ScoreUnit.Type.FLUSH))
                    }
                } else {
                    // If it's a hand then matching the cut card gives an extra point
                    val cardsInvolved = ArrayList(cardList)
                    if (cutCard.suit == suit) {
                        cardsInvolved.add(cutCard)
                    }

                    scoreUnitList.add(ScoreUnit(cardsInvolved, cardsInvolved.size, ScoreUnit.Type.FLUSH))
                }
            }
        }

        return scoreUnitList
    }

    private fun roundScoreRuns(sortedCards: List<Card>): List<ScoreUnit>  {
        // First separate into groupings without regard for repeats or minimum run length
        // (i.e. 2, 2, 3 || 4, 5, 6, 7)
        val scoreUnitList = ArrayList<ScoreUnit>()
        val allGroupings = ArrayList<ArrayList<Card>>()
        var grouping = ArrayList<Card>()

        for (card in sortedCards) {
            val lastOrder = if (grouping.size > 0) grouping[grouping.size - 1].order else Int.MIN_VALUE

            if (lastOrder == card.order || lastOrder + 1 == card.order) {
                grouping.add(card)
            } else {
                allGroupings.add(grouping)
                grouping = ArrayList()
                grouping.add(card)
            }
        }

        // Now determine how many points each are worth
        for (g in allGroupings) {
            val runScoreUnit = scoreRunGrouping(g)
            if (runScoreUnit != null) {
                scoreUnitList.add(runScoreUnit)
            }
        }

        return scoreUnitList
    }

    private fun scoreRunGrouping(group: List<Card>): ScoreUnit? {
        // Count unique faces and repeats. Assume all are either repeats or in order.
        val cardCountMap = SparseIntArray()
        for (card in group) {
            var count = cardCountMap.get(card.order, 0)
            count++
            cardCountMap.put(card.order, count)
        }

        val runLength = cardCountMap.size()

        if (runLength >= MIN_RUN_LENGTH) {
            var repeatMultiple = 1

            var index = 0
            while (index < cardCountMap.size()) {
                repeatMultiple *= cardCountMap.valueAt(index)
                index++
            }

            return ScoreUnit(group, repeatMultiple * runLength, ScoreUnit.Type.RUN)
        } else {
            return null
        }
    }

    private fun scoreNobs(cardList: List<Card>, cutCard: Card): ScoreUnit? {
        for (card in cardList) {
            if (card.suit == cutCard.suit && card.face == Face.JACK) {
                val cardsInvolved = ArrayList<Card>()
                cardsInvolved.add(card)
                return ScoreUnit(cardsInvolved, 1, ScoreUnit.Type.NOBS)
            }
        }

        // No nobs found
        return null
    }

    private fun roundScorePairs(sortedCards: List<Card>): List<ScoreUnit> {
        val scoreUnitList = ArrayList<ScoreUnit>()
        var repeatCount = 0
        var lastValue = -1

        // Sorted by value
        for (i in sortedCards.indices) {
            if (sortedCards[i].value != lastValue) {
                val cardsInvolved: List<Card> = sortedCards.subList(i - repeatCount, repeatCount)

                when (repeatCount) {
                    2 -> scoreUnitList.add(ScoreUnit(cardsInvolved, POINTS_PER_2_SET, ScoreUnit.Type.SET))
                    3 -> scoreUnitList.add(ScoreUnit(cardsInvolved, POINTS_PER_3_SET, ScoreUnit.Type.SET))
                    4 -> scoreUnitList.add(ScoreUnit(cardsInvolved, POINTS_PER_4_SET, ScoreUnit.Type.SET))
                }

                repeatCount = 1
                lastValue = sortedCards[i].value
            } else {
                repeatCount++
            }
        }

        return scoreUnitList
    }

    private fun countSuit(cardList: List<Card>, suit: Suit): Int {
        var total = 0

        for (card in cardList) {
            if (card.suit == suit) {
                total++
            }
        }

        return total
    }

    private fun incrementPlayerIndex(game: Game, playerIndex: Int): Int {
        var newIndex = playerIndex + 1
        if (newIndex >= game.players.size) {
            newIndex = 0
        }
        return newIndex
    }
}