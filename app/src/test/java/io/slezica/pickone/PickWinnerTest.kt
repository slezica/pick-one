package io.slezica.pickone

import io.slezica.pickone.component.pickWinner
import io.slezica.pickone.model.Pointer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Guards the finger-selection fix. The old code did `pointers[nextInt(size)]`,
 * treating a random int as a map key. With non-contiguous pointer ids (which
 * Android produces after a lift + re-add) that biased low ids and returned null
 * on gaps -> NPE. These tests pin the corrected behaviour.
 */
class PickWinnerTest {

    private fun pointer(id: Int) = Pointer(id = id, x = 0f, y = 0f)

    @Test
    fun everyFingerIsReachable_evenWithNonContiguousIds() {
        // Ids {0, 2, 5}: the old lookup could never return id 5 and crashed on 1/3/4.
        val pointers = listOf(pointer(0), pointer(2), pointer(5))
        val random = Random(42)

        val seen = mutableSetOf<Int>()
        repeat(10_000) {
            val winner = pickWinner(pointers, random)
            assertTrue("winner must be an actual pointer", pointers.contains(winner))
            seen.add(winner.id)
        }

        assertEquals("all fingers must be reachable", setOf(0, 2, 5), seen)
    }

    @Test
    fun selectionIsUniform() {
        val ids = listOf(0, 1, 2, 3, 4, 5, 6, 9, 11)
        val pointers = ids.map(::pointer)
        val random = Random(7)
        val runs = 900_000

        val counts = ids.associateWith { 0 }.toMutableMap()
        repeat(runs) {
            val winner = pickWinner(pointers, random)
            counts[winner.id] = counts.getValue(winner.id) + 1
        }

        val expected = runs.toDouble() / ids.size
        for (id in ids) {
            val deviation = Math.abs(counts.getValue(id) - expected) / expected
            assertTrue(
                "id $id got ${counts.getValue(id)}, expected ~$expected (dev=$deviation)",
                deviation < 0.05
            )
        }
    }

    @Test
    fun singleFingerAlwaysWins() {
        val only = pointer(3)
        assertEquals(only, pickWinner(listOf(only), Random(0)))
    }
}
