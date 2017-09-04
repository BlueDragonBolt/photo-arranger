package com.photoarranger.training.photoarranger

/**
 * Created by Training on 14/08/2017.
 */
enum class Orientation{
    HORIZONTAL(), VERTICAL(), SQUARE()
}
fun getOrientation(oriName: String): Orientation?{
    Orientation.values()
            .filter { oriName == it.name }
            .forEach { return it }

    return null
}