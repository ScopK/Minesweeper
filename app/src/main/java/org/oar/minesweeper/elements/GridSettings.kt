package org.oar.minesweeper.elements

import org.oar.minesweeper.generators.GridGenerator
import org.oar.minesweeper.generators.RandomCheckedGenerator
import org.oar.minesweeper.generators.RandomGenerator
import java.io.Serializable
import kotlin.reflect.KClass

data class GridSettings(
    val firstOpen: Boolean,
    val solvable: Boolean,
    var visualHelp: Boolean,
) : Serializable {
    val generatorClass: KClass<out GridGenerator>
        get() = if (solvable)
                RandomCheckedGenerator::class
            else
                RandomGenerator::class
}