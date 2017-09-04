package com.photoarranger.training.photoarranger

import java.util.*


/**
 * Created by Training on 17/08/2017.
 */

//square stacker needed
//have a look at the warnings...again
//Important: combine check should be implemented...sigh...yeah
//also: orientation presence
//also also: "magic number" calculation
private var frameWidth = 0
private var frameHeight = 0
private var constFrameH = 0
private var constFrameW = 0
private var rectangleArray: ArrayList<Photos> = arrayListOf()
private var originalRectangles: ArrayList<Pair<Int, Int>> = arrayListOf()
private var lateRectangleIndexes: MutableList<Int> = mutableListOf()
private var orderOfPlacementIndexes: MutableList<Int> = mutableListOf()
fun biter(frameW: Int, frameH: Int, recArray: ArrayList<Photos>): ArrayList<Photos> {
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
        bite(i, false)
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

var warningSizeV: Double = 1.0
var warningSizeH: Double = 1.0

private fun warningBite(issuer: Int) {
    frameHeight = constFrameH
    frameWidth = constFrameW
    var index = 0
    while (index< orderOfPlacementIndexes.size) {

        if (orderOfPlacementIndexes[index] == issuer) break

        if (rectangleArray[orderOfPlacementIndexes[index]].orientation == Orientation.HORIZONTAL) {
            rectangleArray[orderOfPlacementIndexes[index]].width = frameWidth
            rectangleArray[orderOfPlacementIndexes[index]].height = Math.round(rectangleArray[orderOfPlacementIndexes[index]].width * rectangleArray[orderOfPlacementIndexes[index]].aspectRatio * warningSizeH).toInt()
            if (originalRectangles[orderOfPlacementIndexes[index]].first * 3 < rectangleArray[orderOfPlacementIndexes[index]].width) {

                rectangleArray[orderOfPlacementIndexes[index]].width = originalRectangles[index].first * 3
                rectangleArray[orderOfPlacementIndexes[index]].height = Math.round(rectangleArray[orderOfPlacementIndexes[index]].width * rectangleArray[orderOfPlacementIndexes[index]].aspectRatio * warningSizeH).toInt()
                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = 1.0
                warningSizeV = (constFrameW - rectangleArray[orderOfPlacementIndexes[index]].width).toDouble() / (constFrameW - frameWidth)
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH

            }

            if (rectangleArray[orderOfPlacementIndexes[index]].width < 600) {
                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = 1.0
                warningSizeV = (constFrameW - 600).toDouble() / (constFrameW - frameWidth)
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH
                rectangleArray[orderOfPlacementIndexes[index]].width = frameWidth
                rectangleArray[orderOfPlacementIndexes[index]].height = Math.round(rectangleArray[orderOfPlacementIndexes[index]].width * rectangleArray[orderOfPlacementIndexes[index]].aspectRatio * warningSizeH).toInt()

            }
            if (originalRectangles[orderOfPlacementIndexes[index]].second * 3 < rectangleArray[orderOfPlacementIndexes[index]].height) {
                rectangleArray[orderOfPlacementIndexes[index]].height = originalRectangles[orderOfPlacementIndexes[index]].second * 3
             
            }
            if (rectangleArray[orderOfPlacementIndexes[index]].height < 600) {
                rectangleArray[orderOfPlacementIndexes[index]].height = 600
             
            }
            rectangleArray[orderOfPlacementIndexes[index]].upLeftPointY = frameHeight - rectangleArray[orderOfPlacementIndexes[index]].height
            rectangleArray[orderOfPlacementIndexes[index]].downRightPointX = frameWidth
            rectangleArray[orderOfPlacementIndexes[index]].downRightPointY = frameHeight
            frameHeight -= rectangleArray[orderOfPlacementIndexes[index]].height
        } else {
            rectangleArray[orderOfPlacementIndexes[index]].height = frameHeight
            rectangleArray[orderOfPlacementIndexes[index]].width = Math.round(rectangleArray[orderOfPlacementIndexes[index]].height * rectangleArray[orderOfPlacementIndexes[index]].aspectRatio * warningSizeV).toInt()
            if (originalRectangles[index].second * 3 < rectangleArray[index].height) {

                rectangleArray[orderOfPlacementIndexes[index]].height = originalRectangles[index].second * 3
                rectangleArray[orderOfPlacementIndexes[index]].width = Math.round(rectangleArray[orderOfPlacementIndexes[index]].height * rectangleArray[orderOfPlacementIndexes[index]].aspectRatio * warningSizeV).toInt()
                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = (constFrameH - rectangleArray[orderOfPlacementIndexes[index]].height).toDouble() / (constFrameH - frameHeight)
                warningSizeV = 1.0
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH

            }


            if (rectangleArray[orderOfPlacementIndexes[index]].height < 600) {
                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = (constFrameH - 600).toDouble() / (constFrameH - frameHeight)
                warningSizeV = 1.0
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH
                rectangleArray[orderOfPlacementIndexes[index]].height = frameHeight
                rectangleArray[orderOfPlacementIndexes[index]].width = Math.round(rectangleArray[orderOfPlacementIndexes[index]].height * rectangleArray[orderOfPlacementIndexes[index]].aspectRatio * warningSizeV).toInt()
            }
                if (originalRectangles[orderOfPlacementIndexes[index]].first * 3 < rectangleArray[orderOfPlacementIndexes[index]].width) {
                    rectangleArray[orderOfPlacementIndexes[index]].width = originalRectangles[orderOfPlacementIndexes[index]].first * 3

                }

            if (rectangleArray[orderOfPlacementIndexes[index]].width < 600) {
                rectangleArray[orderOfPlacementIndexes[index]].width = 600
             
            }
            rectangleArray[orderOfPlacementIndexes[index]].upLeftPointX = frameWidth - rectangleArray[orderOfPlacementIndexes[index]].width
            rectangleArray[orderOfPlacementIndexes[index]].downRightPointX = frameWidth
            rectangleArray[orderOfPlacementIndexes[index]].downRightPointY = frameHeight
            frameWidth -= rectangleArray[orderOfPlacementIndexes[index]].width
        }
        //square arranger

        index++
    }
}

private fun bite(index: Int, late: Boolean) {
    if (!late) {
        val lateIndexer = lateRectangleIndexes.size
        for (i in 0 until lateIndexer) {
            bite(lateRectangleIndexes[i], true)
            lateRectangleIndexes.removeAt(i)
        }
    }
    if (rectangleArray[index].orientation == Orientation.HORIZONTAL) {
        rectangleArray[index].width = frameWidth
        rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
        if (originalRectangles[index].first * 3 < rectangleArray[index].width && (index != rectangleArray.size - 1)) {
            rectangleArray[index].width = originalRectangles[index].first
            rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
            lateRectangleIndexes.add(index)
        } else {
            if (originalRectangles[index].first * 3 < rectangleArray[index].width && (index == rectangleArray.size - 1)) {
                rectangleArray[index].width = originalRectangles[index].first * 3
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
                frameHeight -= rectangleArray[index].height

            }


            if (rectangleArray[index].width < 600) {
                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = 1.0
                warningSizeV = (constFrameW - 600).toDouble() / (constFrameW - frameWidth)
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH
                rectangleArray[index].width = frameWidth
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio).toInt()
             
                //warning
            }

            if (rectangleArray[index].height < 600) {
                rectangleArray[index].height = 600
             

            }

            if (rectangleArray[index].height > frameHeight) {
                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = minOf(constFrameH - 600, constFrameH - frameHeight - rectangleArray[index].height / 2 + frameHeight / 2).toDouble() / (constFrameH - frameHeight)
                warningSizeV = 1.0
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH
                rectangleArray[index].width = frameWidth
                rectangleArray[index].height = frameHeight
             
                //warning
            }

            orderOfPlacementIndexes.add(index)
            rectangleArray[index].upLeftPointY = frameHeight - rectangleArray[index].height
            rectangleArray[index].downRightPointX = frameWidth
            rectangleArray[index].downRightPointY = frameHeight
            frameHeight -= rectangleArray[index].height
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

            }


            if (rectangleArray[index].height < 600) {

                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = (constFrameH - 600).toDouble() / (constFrameH - frameHeight)
                warningSizeV = 1.0
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH
                rectangleArray[index].width = frameWidth
                rectangleArray[index].height = frameHeight
             
                //warning
            }

            if (rectangleArray[index].width < 600) {

                rectangleArray[index].width = 600

            }

            if (rectangleArray[index].width > frameWidth) {

                val warningV = warningSizeV
                val warningH = warningSizeH
                warningSizeH = 1.0
                warningSizeV = minOf(constFrameW- 600, constFrameW - frameWidth - rectangleArray[index].width / 2 + frameWidth/ 2).toDouble() / (constFrameW - frameWidth)
                warningBite(index)
                warningSizeV = warningV
                warningSizeH = warningH
                rectangleArray[index].width = frameWidth
                rectangleArray[index].height = frameHeight
             
                //warning
            }

            orderOfPlacementIndexes.add(index)
            rectangleArray[index].upLeftPointX = frameWidth - rectangleArray[index].width
            rectangleArray[index].downRightPointX = frameWidth
            rectangleArray[index].downRightPointY = frameHeight
            frameWidth -= rectangleArray[index].width
        }
    }

    if (lateRectangleIndexes.size == rectangleArray.size - orderOfPlacementIndexes.size) {
        //stitch
    }

}