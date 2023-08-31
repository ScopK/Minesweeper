package org.oar.minesweeper.models

import org.oar.minesweeper.generators.GridGenerator
import org.oar.minesweeper.generators.RandomCheckedGenerator
import org.oar.minesweeper.generators.RandomGenerator
import java.io.Serializable
import kotlin.reflect.KClass

data class GridSettings(
    val solvable: Boolean,
    var visualHelp: Boolean,
) : Serializable {

    val firstTapGenerate = !solvable
    val firstOpen = solvable

    val generatorClass: KClass<out GridGenerator>
        get() = if (solvable)
                RandomCheckedGenerator::class
            else
                RandomGenerator::class
}