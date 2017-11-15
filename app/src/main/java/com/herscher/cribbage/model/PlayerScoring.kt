package com.herscher.cribbage.model

data class PlayerScoring(val playerIndex: Int,
                         val scores: List<ScoreUnit>) {
}