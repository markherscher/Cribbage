package com.herscher.cribbage.model

data class ScoreUnit(val cards: List<Card>,
                     val points: Int,
                     val type: Type) {

    enum class Type {
        FIFTEENS,
        RUN,
        SET,
        FLUSH,
        NOBS,
        GO
    }
}