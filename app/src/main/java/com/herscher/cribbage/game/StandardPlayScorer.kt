package com.herscher.cribbage.game

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

open class StandardPlayScorer {
    fun calculateScores(game: Game, scoreForGo: Boolean): PlayerScoring {
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

    protected fun playScoreFifteens(game: Game): ScoreUnit? {
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

    protected fun playScoreRuns(game: Game): ScoreUnit? {
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

    protected fun playScorePairs(game: Game): ScoreUnit? {
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

    // tODO move
    private fun getCardsInRun(cardList: List<Card>): List<Card> {
        val sortedCards = ArrayList(cardList).sortedWith(compareBy { it.order })
        var runLength = 1
        var runStartIndex = 0
        var lastOrder = -1

        for (i in sortedCards.indices) {
            val currentOrder = sortedCards[i].order
            val isCardPartOfRun = currentOrder + 1 == lastOrder

            if (isCardPartOfRun) {
                // The run continues
                runLength++
            } else {
                if (runLength == 1) {
                    runStartIndex = i
                } else if (runLength > 1) {
                    // We had a run, but found the end of it
                    break
                }
            }

            lastOrder = currentOrder
        }

        return ArrayList<Card>(sortedCards.subList(runStartIndex, runStartIndex + runLength))
    }
}