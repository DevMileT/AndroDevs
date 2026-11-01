package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HistoryFilterTest {

    @Test
    fun all_hasNoOutcome() {
        assertNull(HistoryFilter.ALL.outcome)
    }

    @Test
    fun eachFilter_mapsToItsOutcome() {
        assertEquals(SpinOutcome.WIN, HistoryFilter.WINS.outcome)
        assertEquals(SpinOutcome.DRAW, HistoryFilter.DRAWS.outcome)
        assertEquals(SpinOutcome.LOSS, HistoryFilter.LOSSES.outcome)
    }
}