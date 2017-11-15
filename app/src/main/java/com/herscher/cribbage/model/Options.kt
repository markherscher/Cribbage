package com.herscher.cribbage.model

data class Options(val discardCount: Int = 2,
                   val playCount: Int = 4,
                   val pointsToWin: Int = 121,
                   val maxPlayCount: Int = 31) {
}