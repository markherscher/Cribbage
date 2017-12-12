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

    override val message: String?
        get() {
            return when (violation) {
                RuleViolation.INVALID_CARD -> "invalid card"
                RuleViolation.WRONG_PLAYER -> "wrong player"
                RuleViolation.STATE_MISMATCH -> "state mismatch"
                RuleViolation.CARD_CANNOT_BE_PLAYED -> "card cannot be played"
                RuleViolation.SCORE_DISAGREEMENT -> "score disagreement"
                RuleViolation.DISCARD_COUNT_WRONG -> "discard count wrong"
            }
        }
}