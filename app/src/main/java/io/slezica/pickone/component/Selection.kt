package io.slezica.pickone.component

import io.slezica.pickone.model.Pointer
import kotlin.random.Random

/**
 * Pick one finger uniformly at random.
 *
 * Chooses among the actual pointers, not map keys. Android pointer ids can be
 * non-contiguous after a lift + re-add, so the old `pointers[nextInt(size)]`
 * biased low ids and returned null (-> NPE) on gaps. Kept free of Android types
 * so it stays covered by plain JVM unit tests.
 */
fun pickWinner(pointers: Collection<Pointer>, random: Random = Random.Default): Pointer =
    pointers.random(random)
