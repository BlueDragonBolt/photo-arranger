package com.photoarranger.training.photoarranger

import java.util.*
import kotlin.collections.ArrayList
val MAX_NUM_PHOTOS = 1000

/**
 * Created by Training on 17/08/2017.
 */

//square stacker needed
//also: orientation presence
//also also: "magic number" calculation (step?)
//you gotta stitch, too
//box problem is pending as well
//scaling in case of a HUGE bitmap
//reArrange could be used in a non-last photo
//you can't tell + and - apart
private var frameWidth = 0
private var frameHeight = 0
private var constFrameH = 0
private var constFrameW = 0
private var rectangleArray: ArrayList<Photos> = arrayListOf()
private var originalRectangles: ArrayList<Pair<Int, Int>> = arrayListOf()
private var lateRectangleIndexes: MutableList<Int> = mutableListOf()
private var orderOfPlacementIndexes: MutableList<Int> = mutableListOf()
private var frameState= Array<Pair<Int, Int>>(MAX_NUM_PHOTOS,  { i -> Pair(0,0)})

private var reBuilding = false
private var noTrimPolicy = false
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

private fun reArranger(issuer: Int): MutableList<Int>
{
    //horizontals deserve horizontal trim
    //verticals - vertical trim
    val targetRatio: Double = (originalRectangles[issuer].first / originalRectangles[issuer].second).toDouble()
    var index =  issuer-1
    var currRatio: Double = (frameWidth).toDouble() / frameHeight
    var innerFirst = 0
    var innerLast = 0
    var outerLast = 0
    var bestRatio: Double = currRatio
    var bestOrder = mutableListOf<Int>()
    var permute = Stack<Int>()
    var swap = 0
    val reBuildH = reBuildSizeH
    val reBuildV = reBuildSizeV
    reBuildSizeH = 1.0
    reBuildSizeV = 1.0

    for(i in 0 until orderOfPlacementIndexes.size)
        bestOrder.add(orderOfPlacementIndexes[i])

    noTrimPolicy = true
    MainCycle@ while(index>=0) {
        if (bestRatio == targetRatio) return bestOrder

        if (rectangleArray[orderOfPlacementIndexes[index]].orientation == Orientation.HORIZONTAL && targetRatio < currRatio) {
            index--
            continue
        }
        if (rectangleArray[orderOfPlacementIndexes[index]].orientation == Orientation.VERTICAL && targetRatio > currRatio) {
            index--
            continue
        }
        innerFirst = index
        index--
        if(index<0) break@MainCycle
        while (rectangleArray[orderOfPlacementIndexes[index]].orientation == rectangleArray[orderOfPlacementIndexes[innerFirst]].orientation && index >= 0) {
            index--
            if(index<0) break@MainCycle
        }
        innerLast = index + 1
        index--
        if(index<0) break@MainCycle
        while (rectangleArray[orderOfPlacementIndexes[index]].orientation == rectangleArray[orderOfPlacementIndexes[index+1]].orientation && index >= 0) {
            index--
            if(index<0) break@MainCycle
        }
        outerLast = index + 1
        val stateLimit = outerLast - 1
        val tmpList = MutableList(orderOfPlacementIndexes.size, { i -> 0})

        for (i in innerFirst downTo innerLast)
            tmpList[i - innerFirst] = orderOfPlacementIndexes[i]

        for (i in innerFirst downTo  innerFirst - Math.abs(outerLast-innerLast)+1)
            orderOfPlacementIndexes[i] = orderOfPlacementIndexes[i - Math.abs(innerLast - innerFirst + 1)]
        var counter = 0
        for (i in outerLast + Math.abs(innerLast - innerFirst) downTo outerLast) {
            orderOfPlacementIndexes[i] = tmpList[counter]
            counter++
        }
        innerLast = outerLast + Math.abs(innerLast - innerFirst)+1
        var failed = true
        while (failed) {
            swap = orderOfPlacementIndexes[innerLast]
            orderOfPlacementIndexes[innerLast] = orderOfPlacementIndexes[innerFirst]
            orderOfPlacementIndexes[innerFirst] = swap
            
            failed = false
            try {
                reBuild(rectangleArray.size - 1, stateLimit)

            } catch (e: Exception) {
                index = -1 // indicates that the function returns after this iteration
                swap = orderOfPlacementIndexes[innerLast]
                for (i in innerLast - 1 downTo stateLimit+1)
                    orderOfPlacementIndexes[i + 1] = orderOfPlacementIndexes[i]
                orderOfPlacementIndexes[stateLimit+1] = swap
                outerLast++
                innerLast++
                failed = true
            }
        }
        currRatio = (frameWidth).toDouble() / frameHeight

        if (Math.abs(targetRatio - currRatio) < Math.abs(targetRatio - bestRatio)) {
            try{
                reBuild(rectangleArray.size, 0)
                bestRatio = currRatio
                        for (i in 0 until orderOfPlacementIndexes.size)
                            bestOrder[i] = orderOfPlacementIndexes[i]
            }
            catch(e:Exception){ }
        }

        if ((currRatio < targetRatio && rectangleArray[orderOfPlacementIndexes[outerLast]].orientation == Orientation.HORIZONTAL) || //change to smth constant
                (currRatio > targetRatio && rectangleArray[orderOfPlacementIndexes[outerLast]].orientation == Orientation.VERTICAL)) {

            index -= (Math.abs(innerLast - innerFirst )+ 1)
            continue
        }

        if (currRatio == targetRatio)
            return bestOrder

            while (innerLast< orderOfPlacementIndexes.size) {

                failed = true
                while (failed) {
                    if (innerLast > innerFirst)
                        innerLast = outerLast+1

                    swap = orderOfPlacementIndexes[innerLast]
                    orderOfPlacementIndexes[innerLast] = orderOfPlacementIndexes[innerFirst]
                    orderOfPlacementIndexes[innerFirst] = swap

                    swap = orderOfPlacementIndexes[innerLast]
                    orderOfPlacementIndexes[innerLast] = orderOfPlacementIndexes[innerLast - 1]
                    orderOfPlacementIndexes[innerLast - 1] = swap

                    failed = false
                    try {
                        reBuild(rectangleArray.size - 1, stateLimit)

                    } catch (e: Exception) {
                        index = -1 // indicates that the function returns after this iteration
                        swap = orderOfPlacementIndexes[innerLast + 1]
                        for (i in innerLast + 2 downTo stateLimit+1)
                            orderOfPlacementIndexes[i - 1] = orderOfPlacementIndexes[i]
                        orderOfPlacementIndexes[stateLimit+1] = swap
                        outerLast++
                        innerLast++
                        failed = true
                    }
                }
                currRatio = (frameWidth).toDouble() / frameHeight

                if (Math.abs(targetRatio - currRatio) < Math.abs(targetRatio - bestRatio)) {
                    try{
                        reBuild(rectangleArray.size, 0)
                        bestRatio = currRatio
                        for (i in 0 until orderOfPlacementIndexes.size)
                            bestOrder[i] = orderOfPlacementIndexes[i]
                    }
                    catch(e:Exception){ }
                }

                if ((currRatio < targetRatio && rectangleArray[orderOfPlacementIndexes[outerLast]].orientation == Orientation.HORIZONTAL) ||
                        (currRatio > targetRatio && rectangleArray[orderOfPlacementIndexes[outerLast]].orientation == Orientation.VERTICAL)) {

                    swap = orderOfPlacementIndexes[innerLast + 1]

                    for (i in innerLast + 2 downTo  outerLast)
                        orderOfPlacementIndexes[i - 1] = orderOfPlacementIndexes[i]

                    orderOfPlacementIndexes[outerLast] = swap
                    outerLast++
                    innerLast++
                    continue
                }

                if (currRatio == targetRatio)
                    return bestOrder

                if(innerLast > innerFirst)
                    permute.push(innerFirst)

                else if(innerLast == innerFirst) {
                    if(innerLast+1 == outerLast){
                        index = -1 // indicates that the function returns after this iteration
                        break@MainCycle
                    }
                    permute.push(innerLast - 2)
                }
                else
                    permute.push(innerFirst)
                while(permute.isNotEmpty()) {
                    val top = permute.peek()
                    permute.pop()
                    if(top>outerLast || top<innerFirst)continue
                    if(top>innerLast) {
                        permute.push(innerLast - 1)

                        swap = orderOfPlacementIndexes[innerLast - 2]
                        orderOfPlacementIndexes[innerLast - 1] = orderOfPlacementIndexes[top]
                        orderOfPlacementIndexes[top] = swap
                    }
                    else{
                        swap = orderOfPlacementIndexes[innerLast]
                        orderOfPlacementIndexes[innerLast] = orderOfPlacementIndexes[top]
                        orderOfPlacementIndexes[top] = swap
                    }
                    if(top > innerLast + 1 || (top < innerLast - 1 && top > outerLast))
                        permute.push(top - 1)

                    try {
                        reBuild(rectangleArray.size-1, stateLimit)

                        currRatio = (frameWidth).toDouble() / frameHeight

                        if (Math.abs(targetRatio - currRatio) < Math.abs(targetRatio - bestRatio)) {
                            try{
                                reBuild(rectangleArray.size, 0)
                                bestRatio = currRatio
                                for (i in 0 until orderOfPlacementIndexes.size)
                                    bestOrder[i] = orderOfPlacementIndexes[i]
                            }
                            catch(e:Exception){ }
                        }
                    }
                    catch (e:Exception){}
                }
                break
            }
        }

    noTrimPolicy=false
    reBuildSizeH = reBuildH
    reBuildSizeV = reBuildV
    return bestOrder
}



var reBuildSizeV: Double = 1.0
var reBuildSizeH: Double = 1.0

private fun reBuild(issuer: Int, stateIndex: Int) {

    if(stateIndex==-1) {
        frameHeight = constFrameH
        frameWidth = constFrameW
    }
    else{
        frameWidth = frameState[stateIndex].first
        frameHeight = frameState[stateIndex].second
    }
    var index = stateIndex+1
    reBuilding = true
    while (index< orderOfPlacementIndexes.size) {

        if (orderOfPlacementIndexes[index] == issuer) break

        build(orderOfPlacementIndexes[index],false)
        index++
    }
    reBuilding = false
}

private fun build(index: Int, late: Boolean) {

    rectangleArray[index].width = originalRectangles[index].first
    rectangleArray[index].height = originalRectangles[index].second
    if(index == rectangleArray.size - 1){
        orderOfPlacementIndexes = reArranger(index)
    }
    if (rectangleArray[index].orientation == Orientation.HORIZONTAL) {
        rectangleArray[index].width = frameWidth
        rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio* reBuildSizeH).toInt()
        if (originalRectangles[index].first * 3 < rectangleArray[index].width && (index != rectangleArray.size - 1)) {
            if(reBuilding) {
                if(noTrimPolicy)
                    throw Exception("Unable to rearrange")
                rectangleArray[index].width = originalRectangles[index].first * 3
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio * reBuildSizeH).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = (constFrameW - rectangleArray[index].width).toDouble() / (constFrameW - frameWidth)
                reBuild(index,-1)
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

            }


            if (rectangleArray[index].width < 600) {
                if(noTrimPolicy)
                    throw IllegalStateException("Unable to rearrange")
                rectangleArray[index].width = 600
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio* reBuildSizeH).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = (constFrameW - 600).toDouble() / (constFrameW - frameWidth)
                reBuild(index,-1)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH

                //reBuild
            }

            if (rectangleArray[index].height < 600) {
                rectangleArray[index].height = 600
             

            }

            if (rectangleArray[index].height > frameHeight) {

                if(noTrimPolicy)
                    throw IllegalStateException("Unable to rearrange")
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = minOf(constFrameH - 600, constFrameH - frameHeight - rectangleArray[index].height / 2 + frameHeight / 2).toDouble() / (constFrameH - frameHeight)
                reBuildSizeV = 1.0
                reBuild(index,-1)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH
                rectangleArray[index].width = frameWidth
                rectangleArray[index].height = frameHeight
             
                //reBuild
            }

            if(!reBuilding){
                orderOfPlacementIndexes.add(index)

            }

            rectangleArray[index].upLeftPointY = frameHeight - rectangleArray[index].height
            rectangleArray[index].downRightPointX = frameWidth
            rectangleArray[index].downRightPointY = frameHeight
            frameHeight -= rectangleArray[index].height
            frameState[index]=Pair(frameWidth, frameHeight)
        }

    }
    else {

        rectangleArray[index].height = frameHeight
        rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio * reBuildSizeV).toInt()
        if (originalRectangles[index].second * 3 < rectangleArray[index].height && (index != rectangleArray.size - 1)) {
            if(reBuilding) {

                if(noTrimPolicy)
                    throw Exception("Unable to rearrange")
                rectangleArray[index].width = originalRectangles[index].first * 3
                rectangleArray[index].height = Math.round(rectangleArray[index].width * rectangleArray[index].aspectRatio * reBuildSizeH).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = (constFrameW - rectangleArray[index].width).toDouble() / (constFrameW - frameWidth)
                reBuild(index,-1)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH

            }
            else {
                rectangleArray[index].height = originalRectangles[index].second
                rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio).toInt()
                lateRectangleIndexes.add(index)
            }
        }
        else {

            if (originalRectangles[index].second * 3 < rectangleArray[index].height && (index == rectangleArray.size - 1)) {
                rectangleArray[index].height = originalRectangles[index].second * 3
                rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio * reBuildSizeV).toInt()
                frameHeight -= rectangleArray[index].width
            }


            if (rectangleArray[index].height < 600) {

                if(noTrimPolicy)
                    throw IllegalStateException("Unable to rearrange")

                rectangleArray[index].height = 600
                rectangleArray[index].width = Math.round(rectangleArray[index].height * rectangleArray[index].aspectRatio * reBuildSizeV).toInt()
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = (constFrameH - 600).toDouble() / (constFrameH - frameHeight)
                reBuildSizeV = 1.0
                reBuild(index,-1)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH

             
                //reBuild
            }

            if (rectangleArray[index].width < 600) {

                rectangleArray[index].width = 600

            }

            if (rectangleArray[index].width > frameWidth) {

                if(noTrimPolicy)
                    throw IllegalStateException("Unable to rearrange")
                val reBuildV = reBuildSizeV
                val reBuildH = reBuildSizeH
                reBuildSizeH = 1.0
                reBuildSizeV = minOf(constFrameW- 600, constFrameW - frameWidth - rectangleArray[index].width / 2 + frameWidth/ 2).toDouble() / (constFrameW - frameWidth)
                reBuild(index,-1)
                reBuildSizeV = reBuildV
                reBuildSizeH = reBuildH
                rectangleArray[index].height = frameHeight
                rectangleArray[index].width = frameWidth
             
                //reBuild
            }

            if(!reBuilding){
                orderOfPlacementIndexes.add(index)

            }

            rectangleArray[index].upLeftPointX = frameWidth - rectangleArray[index].width
            rectangleArray[index].downRightPointX = frameWidth
            rectangleArray[index].downRightPointY = frameHeight
            frameWidth -= rectangleArray[index].width
            frameState[index]=Pair(frameWidth, frameHeight)
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