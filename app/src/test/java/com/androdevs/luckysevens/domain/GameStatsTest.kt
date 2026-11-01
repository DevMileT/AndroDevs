package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */

import org.junit.Assert.assertEquals
import org.junit.Test

class GameStatsTest {

    @Test
    fun total_isSumOfWinsDrawsAndLosses() {
        val stats = GameStats(wins = 15, draws = 5, losses = 10, netBalance = 250, record = 1250)
        assertEquals(30, stats.total)
    }

    @Test
    fun total_withNoGames_isZero() {
        val stats = GameStats(wins = 0, draws = 0, losses = 0, netBalance = 0, record = 1000)
        assertEquals(0, stats.total)
    }
}