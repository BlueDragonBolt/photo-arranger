package com.photoarranger.training.photoarranger

import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Training on 17/08/2017.
 */

//square stacker needed
//also: orientation presence
//also also: "magic number" calculation (step?)
//you gotta stitch, too
//box problem is pending as well
private var frameWidth = 0
private var frameHeight = 0
private var constFrameH = 0
private var constFrameW = 0
private var rectangleArray: ArrayList<Photos> = arrayListOf()
private var originalRectangles: ArrayList<Pair<Int, Int>> = arrayListOf()
private var lateRectangleIndexes: MutableList<Int> = mutableListOf()
private var orderOfPlacementIndexes: MutableList<Int> = mutableListOf()
private var frameState: ArrayList<Pair<Int, Int>> = arrayListOf()
private var reBuilding = false
fun builder(frameW: Int, frameH: Int, recArray: ArrayList<Photos>): ArrayList<Photos> {
    if (frameH < 600 || frameW < 600)
        throw IllegalStateException("Frame must be at le ast 600x600!")
    if (frameH * frameW < recArray.size * 360000)
        throw Exception("Frame too small to be filled!")
    frameWidth = frameW
    frameHeight = frameH
    constFrameH = frameH
    constFrameW = frameW
    var frameSizeChecker = 0

    for (i in 0 until recArray.size) {
        rectangleArray.add(Photos(0, 0))
        rectangleArray[i].aspectRatio = recArray[i].aspectRatio
        rectangleArray[i].height = recArray[i].height
        rectangleArray[i].width = recArray[i].width
        rectangleArray[i].orientation = recArray[i].orientation
        rectangleArray[i].originalOrder = i+1
        frameSizeChecker += rectangleArray[i].height * rectangleArray[i].width
    }
    if (frameSizeChecker < frameH * frameW/9)
        throw Exception("Frame too large to be filled!")

     Collections.sort(rectangleArray)
    for(i in 0 until rectangleArray.size)
        originalRectangles.add(Pair(rectangleArray[i].width, rectangleArray[i].height))

    for (i in 0 until rectangleArray.size) {
        build(i, false)
    }
    return rectangleArray
}

private fun squareStacker(index: Int) {
    if (index == rectangleArray.size - 1) {
        if (lateRectangleIndexes.isNotEmpty()) {
            throw Exception("A photo is too small to be fit in the frame!")
        }
    }
}

var reBuildSizeV: Double = 1.0
var reBuildSizeH: Double = 1.0

private fun reBuild(issuer: Int) {

    frameHeight = constFrameH
    frameWidth = constFrameW
    var index = 0
    reBuilding = true
    while (index< orderOfPlacementIndexes.size) {

        if (orderOfPlacementIndexes[index] == issuer) break

        build(orderOfPlacementIndexes[index],false)
        index++
    }
    reBuilding = false
}

private fun build(index: Int, late: Boolean) {

    if (rectangleArray[index].orientation == Orientation.HORIZONTAL) {
        rectangleArray[index].width = frameWidth
        rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
        if (originalRectangles[index].first * 3 < rectangleArray[index].width && (index != rectangleArray.size - 1)) {
            if(reBuilding) {

                rectangleArray[index].width = originalRectangles[index].first * 3
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio * reBuildSizeH).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = (constFrameW - rectangleArray[index].width).toDouble() / (constFrameW - frameWidth)
                reBuild(index)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH

            }
            else {
                rectangleArray[index].width = originalRectangles[index].first
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
                lateRectangleIndexes.add(index)
            }
        }
        else {
            if (originalRectangles[index].first * 3 < rectangleArray[index].width && (index == rectangleArray.size - 1)) {
                rectangleArray[index].width = originalRectangles[index].first * 3
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
                frameHeight -= rectangleArray[index].height
               // rearrange()
            }


            if (rectangleArray[index].width < 600) {

                rectangleArray[index].width = 600
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = (constFrameW - 600).toDouble() / (constFrameW - frameWidth)
                reBuild(index)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH

                //reBuild
            }

            if (rectangleArray[index].height < 600) {
                rectangleArray[index].height = 600
             

            }

            if (rectangleArray[index].height > frameHeight) {
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = minOf(constFrameH - 600, constFrameH - frameHeight - rectangleArray[index].height / 2 + frameHeight / 2).toDouble() / (constFrameH - frameHeight)
                reBuildSizeV = 1.0
                reBuild(index)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH
                rectangleArray[index].width = frameWidth
                rectangleArray[index].height = frameHeight
             
                //reBuild
                //rearrange()
            }

            orderOfPlacementIndexes.add(index)
            rectangleArray[index].upLeftPointY = frameHeight - rectangleArray[index].height
            rectangleArray[index].downRightPointX = frameWidth
            rectangleArray[index].downRightPointY = frameHeight
            frameHeight -= rectangleArray[index].height
            frameState.add(Pair(frameWidth, frameHeight))
        }

    }
    else {

        rectangleArray[index].height = frameHeight
        rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio).toInt()
        if (originalRectangles[index].second * 3 < rectangleArray[index].height && (index != rectangleArray.size - 1)) {

            rectangleArray[index].height = originalRectangles[index].second
            rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio).toInt()
            lateRectangleIndexes.add(index)
        }
        else {

            if (originalRectangles[index].second * 3 < rectangleArray[index].height && (index == rectangleArray.size - 1)) {
                rectangleArray[index].height = originalRectangles[index].second * 3
                rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio).toInt()
                frameHeight -= rectangleArray[index].width
                //rearrange()
            }


            if (rectangleArray[index].height < 600) {

                rectangleArray[index].height = 600
                rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = (constFrameH - 600).toDouble() / (constFrameH - frameHeight)
                reBuildSizeV = 1.0
                reBuild(index)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH

             
                //reBuild
                //rearrange()
            }

            if (rectangleArray[index].width < 600) {

                rectangleArray[index].width = 600

            }

            if (rectangleArray[index].width > frameWidth) {

                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = minOf(constFrameW- 600, constFrameW - frameWidth - rectangleArray[index].width / 2 + frameWidth/ 2).toDouble() / (constFrameW - frameWidth)
                reBuild(index)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH
                rectangleArray[index].height = frameHeight
                rectangleArray[index].width = frameWidth
             
                //reBuild
            }

            orderOfPlacementIndexes.add(index)
            rectangleArray[index].upLeftPointX = frameWidth - rectangleArray[index].width
            rectangleArray[index].downRightPointX = frameWidth
            rectangleArray[index].downRightPointY = frameHeight
            frameWidth -= rectangleArray[index].width
            frameState.add(Pair(frameWidth, frameHeight))
        }
    }
    if (!late) {
        val lateIndexer = lateRectangleIndexes.size
        for (i in 0 until lateIndexer) {
            build(lateRectangleIndexes[i], true)
            lateRectangleIndexes.removeAt(i)
        }
    }

    if (lateRectangleIndexes.size == rectangleArray.size - orderOfPlacementIndexes.size) {
        //stitch
    }

}