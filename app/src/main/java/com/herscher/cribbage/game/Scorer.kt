package com.herscher.cribbage.game

import com.herscher.cribbage.model.Game
import com.herscher.cribbage.model.PlayerScoring

interface Scorer {
    fun calculatePlayScores(game: Game, scoreForGo: Boolean): PlayerScoring

    fun calculateRoundScores(game: Game): List<PlayerScoring>
}