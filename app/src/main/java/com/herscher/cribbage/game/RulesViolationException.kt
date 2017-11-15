package com.herscher.cribbage.game

class RulesViolationException : Exception {
    constructor(v: RuleViolation) {
        violation = v
    }

    enum class RuleViolation {
        STATE_MISMATCH,
        WRONG_PLAYER,
        INVALID_CARD,
        CARD_CANNOT_BE_PLAYED,
        SCORE_DISAGREEMENT,
        DISCARD_COUNT_WRONG
    }

    val violation: RuleViolation
}