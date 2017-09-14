package com.photoarranger.training.photoarranger

import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus

val MAX_NUM_PHOTOS = 1000

/**
 * Created by Training on 17/08/2017.
 */
//-box-problem-is-pending-as-well- the "stitching" actually solves -most- all instances of the box problem (some may be cut a bit, however)
//-square-stacker-needed- square stacker no longer needed, all square-stacking needs shall be fulfilled in the "stitching" process
//stitching in progress...

//-also:- -orientation presence- done
//also also: "magic number" calculation (step?)
//scaling in case of a HUGE bitmap
//reArrange could be used in a non-last photo
//you can't tell + and - apart (still applicable?)
//check for the gap
private var frameWidth = 0
private var frameHeight = 0
private var constFrameH = 0
private var constFrameW = 0
private var rectangleArray: ArrayList<Photos> = arrayListOf()
private var originalRectangles: ArrayList<Pair<Int, Int>> = arrayListOf()
private var lateRectangleIndexes: MutableList<Int> = mutableListOf()
private var orderOfPlacementIndexes: MutableList<Int> = mutableListOf()
private var frameState= Array<Pair<Int, Int>>(MAX_NUM_PHOTOS,  { i -> Pair(0,0)})
private var stitchedLists: ArrayList<ArrayList<Int>> = arrayListOf()
fun builder(frameW: Int, frameH: Int, recArray: ArrayList<Photos>): ArrayList<Photos> {
    if (frameH < 0 || frameW < 0)
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
        frameSizeChecker += rectangleArray[i].height * rectangleArray[i].width
    }
    if (frameSizeChecker < frameH * frameW/9)
        throw Exception("Frame too large to be filled!")

     Collections.sort(rectangleArray)
    for(i in 0 until rectangleArray.size)
        originalRectangles.add(Pair(rectangleArray[i].width, rectangleArray[i].height))
    var recSize = rectangleArray.size
    var i = 0
    while(i<recSize){
        build(i, false)
        if(recSize!= rectangleArray.size)
            recSize = rectangleArray.size
        i++
    }
    deStitch(recArray.size)
    for(i in 0 until orderOfPlacementIndexes.size) {
       /* if(i >= orderOfPlacementIndexes.size)
            throw Exception("Order list is not full!")*/
        rectangleArray[orderOfPlacementIndexes[i]].orderOfPlacement = i+1
    }

    return rectangleArray
}

private fun deStitch(realSize: Int)
{
    for(i in realSize until rectangleArray.size){
        val ori = rectangleArray[i].orientation == Orientation.HORIZONTAL
        val equalDim = if(ori) rectangleArray[i].height else rectangleArray[i].width
        var sum = 0
        for(j in stitchedLists[i - realSize]){
            var majorRectangleD = if(ori) rectangleArray[j].width else rectangleArray[j].height
            var secondaryRectangleD = if(ori) rectangleArray[j].height else rectangleArray[j].width
            val ratio = majorRectangleD.toDouble()/secondaryRectangleD
            secondaryRectangleD = equalDim
            majorRectangleD = Math.round(secondaryRectangleD * ratio).toInt()
            sum+=majorRectangleD
        }
        var upLeftX = rectangleArray[i].upLeftPointX
        var upLeftY = rectangleArray[i].upLeftPointY
        for(j in stitchedLists[i - realSize]){
            var majorRectangleD = if(ori) rectangleArray[j].width else rectangleArray[j].height
            var secondaryRectangleD = if(ori) rectangleArray[j].height else rectangleArray[j].width
            val ratio = majorRectangleD.toDouble()/secondaryRectangleD
            secondaryRectangleD = equalDim
            majorRectangleD = (secondaryRectangleD * ratio).toInt()
            if(ori){
                rectangleArray[j].width = Math.round(majorRectangleD.toDouble() / sum).toInt() * rectangleArray[i].width
                rectangleArray[j].height = Math.round(rectangleArray[j].width * rectangleArray[j].aspectRatio).toInt()
                rectangleArray[j].upLeftPointY = upLeftY
                rectangleArray[j].upLeftPointX = upLeftX
                rectangleArray[j].downRightPointY = upLeftY + rectangleArray[j].height
                rectangleArray[j].downRightPointX = upLeftX + rectangleArray[j].height
                upLeftX-=rectangleArray[j].width
            }
            else {
                rectangleArray[j].width = Math.round(rectangleArray[j].height * rectangleArray[j].aspectRatio).toInt()
                rectangleArray[j].height = Math.round(majorRectangleD.toDouble() / sum).toInt() * rectangleArray[i].height
                rectangleArray[j].upLeftPointY = upLeftY
                rectangleArray[j].upLeftPointX = upLeftX
                rectangleArray[j].downRightPointY = upLeftY + rectangleArray[j].height
                rectangleArray[j].downRightPointX = upLeftX + rectangleArray[j].height
                upLeftY-=rectangleArray[j].height
            }
            orderOfPlacementIndexes.add(j)
        }
        orderOfPlacementIndexes.remove(i)
    }

}

private fun squareStacker(index: Int) {
    if (index == rectangleArray.size - 1) {
        if (lateRectangleIndexes.isNotEmpty()) {
            throw Exception("A photo is too small to be fit in the frame!")
        }
    }
}
private var direStitch = false
private fun stitchASAP(ori: Orientation): Boolean
{
    var i = 0
        while (i < lateRectangleIndexes.size)
    {
            if (rectangleArray[lateRectangleIndexes[i]].orientation == ori || rectangleArray[lateRectangleIndexes[i]].orientation == Orientation.SQUARE)
                break
            i++
        }
        if (i == lateRectangleIndexes.size) {
            return false
        }

    rectangleArray.add(rectangleArray[lateRectangleIndexes[i]])
    stitchedLists.add(ArrayList())
    stitchedLists[stitchedLists.size-1].add(lateRectangleIndexes[i])
    for(check in 0..2) {
        if(!direStitch && check == 2)
            break
        for (j in lateRectangleIndexes.size-1 downTo i) {
            if (rectangleArray[lateRectangleIndexes[j]].orientation != ori && check == 0)
                continue
            if (rectangleArray[lateRectangleIndexes[j]].orientation != Orientation.SQUARE && check == 1)
                continue

            if (ori == Orientation.HORIZONTAL) {
                rectangleArray[lateRectangleIndexes[j]].aspectRatio = 1.0 / rectangleArray[lateRectangleIndexes[j]].aspectRatio
                rectangleArray[rectangleArray.size - 1].aspectRatio = 1.0 / rectangleArray[rectangleArray.size - 1].aspectRatio
                rectangleArray[rectangleArray.size - 1].height = minOf(rectangleArray[rectangleArray.size - 1].height, rectangleArray[lateRectangleIndexes[j]].height)
                rectangleArray[rectangleArray.size - 1].width = (rectangleArray[rectangleArray.size - 1].height * rectangleArray[rectangleArray.size - 1].aspectRatio).toInt()
                rectangleArray[rectangleArray.size - 1].width += (rectangleArray[rectangleArray.size - 1].height * rectangleArray[lateRectangleIndexes[j]].aspectRatio).toInt()
                rectangleArray[rectangleArray.size - 1].calculateAspect()
                rectangleArray[lateRectangleIndexes[j]].calculateAspect()
                stitchedLists[stitchedLists.size - 1].add(lateRectangleIndexes[j])
                if (rectangleArray[rectangleArray.size - 1].width * 3 >= frameWidth) {
                    for(k in stitchedLists[stitchedLists.size-1])
                        lateRectangleIndexes.remove(k)
                    originalRectangles.add(Pair(rectangleArray[rectangleArray.size-1].width, rectangleArray[rectangleArray.size-1].height))
                    rectangleArray[rectangleArray.size-1].calculateOrientation()
                    return true
                }
            } else {
                rectangleArray[lateRectangleIndexes[j]].aspectRatio = 1.0 / rectangleArray[lateRectangleIndexes[j]].aspectRatio
                rectangleArray[rectangleArray.size - 1].aspectRatio = 1.0 / rectangleArray[rectangleArray.size - 1].aspectRatio
                rectangleArray[rectangleArray.size - 1].width = minOf(rectangleArray[rectangleArray.size - 1].width, rectangleArray[lateRectangleIndexes[j]].width)
                rectangleArray[rectangleArray.size - 1].height = (rectangleArray[rectangleArray.size - 1].width * rectangleArray[rectangleArray.size - 1].aspectRatio).toInt()
                rectangleArray[rectangleArray.size - 1].height += (rectangleArray[rectangleArray.size - 1].width * rectangleArray[lateRectangleIndexes[j]].aspectRatio).toInt()
                rectangleArray[rectangleArray.size - 1].calculateAspect()
                rectangleArray[lateRectangleIndexes[j]].calculateAspect()
                stitchedLists[stitchedLists.size - 1].add(lateRectangleIndexes[j])
                if (rectangleArray[rectangleArray.size - 1].height * 3 >= frameHeight){
                    for(k in stitchedLists[stitchedLists.size-1])
                        lateRectangleIndexes.remove(k)
                    originalRectangles.add(Pair(rectangleArray[rectangleArray.size-1].width, rectangleArray[rectangleArray.size-1].height))
                    rectangleArray[rectangleArray.size-1].calculateOrientation()
                    return true
                }
            }
        }
    }
    if(direStitch){
        for(k in stitchedLists[stitchedLists.size-1])
            lateRectangleIndexes.remove(k)
        originalRectangles.add(Pair(rectangleArray[rectangleArray.size-1].width, rectangleArray[rectangleArray.size-1].height))
        rectangleArray[rectangleArray.size-1].calculateOrientation()
        return true
    }
    rectangleArray.removeAt(rectangleArray.size-1)
    stitchedLists.removeAt(stitchedLists.size-1)
    return false
}
private var noTrimPolicy = false
private fun reArranger(issuer: Int): MutableList<Int>
{
    //horizontals deserve horizontal trim
    //verticals - vertical trim
    val targetRatio: Double = (originalRectangles[issuer].first / originalRectangles[issuer].second).toDouble()
    var index =  orderOfPlacementIndexes.size-1
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
            tmpList[innerFirst - i] = orderOfPlacementIndexes[i]

        for (i in innerFirst downTo  innerFirst - Math.abs(outerLast-innerLast)+1)
            orderOfPlacementIndexes[i] = orderOfPlacementIndexes[i - Math.abs(innerLast - innerFirst) - 1]
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
                reBuilding = false
                index = -1 // indicates that the function returns after this iteration
                swap = orderOfPlacementIndexes[innerLast]
                for (i in innerLast - 1 downTo stateLimit+1)
                    orderOfPlacementIndexes[i + 1] = orderOfPlacementIndexes[i]
                orderOfPlacementIndexes[stateLimit+1] = swap
                outerLast++
                innerLast++
                if(innerLast>innerFirst)
                    break@MainCycle
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
            catch(e:Exception){
                reBuilding = false
            }
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
                        reBuilding = false
                        index = -1 // indicates that the function returns after this iteration
                        swap = orderOfPlacementIndexes[innerLast + 1]
                        for (i in innerLast + 2 downTo stateLimit+1)
                            orderOfPlacementIndexes[i - 1] = orderOfPlacementIndexes[i]
                        orderOfPlacementIndexes[stateLimit+1] = swap
                        outerLast++
                        innerLast++
                        if(innerLast>innerFirst)
                            break@MainCycle
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
                    catch(e:Exception){
                        reBuilding = false }
                }

                if ((currRatio < targetRatio && rectangleArray[orderOfPlacementIndexes[outerLast]].orientation == Orientation.HORIZONTAL) ||
                        (currRatio > targetRatio && rectangleArray[orderOfPlacementIndexes[outerLast]].orientation == Orientation.VERTICAL)) {

                    if(innerLast >= innerFirst){
                        swap = orderOfPlacementIndexes[innerLast - 1]
                        for (i in innerLast downTo  outerLast)
                            orderOfPlacementIndexes[i - 1] = orderOfPlacementIndexes[i]
                        continue@MainCycle
                    }
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
                            catch(e:Exception){
                                reBuilding = false
                            }
                        }
                    }
                    catch (e:Exception){
                        reBuilding = false
                    }
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

private var reBuilding = false
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
    if(index == rectangleArray.size - 1 && lateRectangleIndexes.isEmpty()){
        orderOfPlacementIndexes = reArranger(index)
    }
    val ori =  rectangleArray[index].orientation == Orientation.HORIZONTAL
    var majorRectangleD = if( ori ) rectangleArray[index].width else rectangleArray[index].height
    var secondaryRectangleD = if( ori ) rectangleArray[index].height else rectangleArray[index].width
    var majorFrameD = if( ori ) frameWidth else frameHeight
    var secondaryFrameD = if( ori ) frameHeight else frameWidth
    var majorReBuildSize = if( ori ) reBuildSizeH else reBuildSizeV
    var secondaryReBuildSize = if( ori ) reBuildSizeV else reBuildSizeH
    val majorOriginal = if( ori ) originalRectangles[index].first else originalRectangles[index].second
    val majorConstFrame = if( ori ) constFrameW else constFrameH
    val secondaryConstFrame = if( ori ) constFrameH else constFrameW
    //var secondaryReBuildSize = if( ori ) rectangleArray[index].height else rectangleArray[index].width

        majorRectangleD = majorFrameD
        secondaryRectangleD = Math.round(majorRectangleD * rectangleArray[index].aspectRatio * majorReBuildSize ).toInt()

        if (majorOriginal * 3 < majorRectangleD && !reBuilding && (index!= rectangleArray.size-1 || lateRectangleIndexes.isNotEmpty())) {

            rectangleArray[index].width = originalRectangles[index].first
            rectangleArray[index].height = originalRectangles[index].second
            lateRectangleIndexes.add(index)

        }

        else {
            if (majorOriginal * 3 < majorRectangleD && (reBuilding || index == rectangleArray.size - 1)) {
                if (noTrimPolicy)
                    throw Exception("Unable to rearrange")

                majorRectangleD = majorOriginal * 3
                secondaryRectangleD = Math.round(majorRectangleD * rectangleArray[index].aspectRatio * majorReBuildSize).toInt()
                val reBuildM = majorReBuildSize
                val reBuildS = secondaryReBuildSize
                majorReBuildSize = 1.0
                secondaryReBuildSize = (majorConstFrame - majorRectangleD).toDouble() / (majorConstFrame - majorFrameD)
                if (ori) {
                    reBuildSizeH = majorReBuildSize
                    reBuildSizeV = secondaryReBuildSize
                } else {
                    reBuildSizeH = secondaryReBuildSize
                    reBuildSizeV = majorReBuildSize

                }
                reBuild(index, -1)
                majorReBuildSize = reBuildM
                secondaryReBuildSize = reBuildS
                 majorFrameD = if( ori ) frameWidth else frameHeight
                 secondaryFrameD = if( ori ) frameHeight else frameWidth
                majorRectangleD = majorFrameD
                secondaryRectangleD = Math.round(majorRectangleD * rectangleArray[index].aspectRatio * majorReBuildSize).toInt()
            }

            if (majorRectangleD < 0) {
                if(noTrimPolicy)
                    throw IllegalStateException("Unable to rearrange")
                rectangleArray[index].aspectRatio = 1/rectangleArray[index].aspectRatio
                secondaryRectangleD = 0
                majorRectangleD = Math.round(secondaryRectangleD * rectangleArray[index].aspectRatio).toInt()
                val reBuildM = majorReBuildSize
                val reBuildS = secondaryReBuildSize
                majorReBuildSize = 1.0
                secondaryReBuildSize = (majorConstFrame - majorRectangleD).toDouble() / (majorConstFrame - majorFrameD)
                if (ori) {
                    reBuildSizeH = majorReBuildSize
                    reBuildSizeV = secondaryReBuildSize
                } else {
                    reBuildSizeH = secondaryReBuildSize
                    reBuildSizeV = majorReBuildSize

                }
                reBuild(index, -1)
                rectangleArray[index].aspectRatio = 1/rectangleArray[index].aspectRatio
                majorReBuildSize = reBuildM
                secondaryReBuildSize = reBuildS
                majorFrameD = if( ori ) frameWidth else frameHeight
                secondaryFrameD = if( ori ) frameHeight else frameWidth
                majorRectangleD = majorFrameD
                secondaryRectangleD = Math.round(majorRectangleD * rectangleArray[index].aspectRatio * majorReBuildSize).toInt()
                //reBuild
            }

            if (secondaryRectangleD < 0) {
                secondaryRectangleD = 0
            }

            if (secondaryRectangleD > secondaryFrameD) {

                if(noTrimPolicy)
                    throw IllegalStateException("Unable to rearrange")
                val reBuildM = majorReBuildSize
                val reBuildS = secondaryReBuildSize
                majorReBuildSize = minOf(secondaryConstFrame - 0, secondaryConstFrame - secondaryFrameD - secondaryRectangleD / 2 + secondaryFrameD / 2).toDouble() / (secondaryConstFrame - secondaryFrameD)
                secondaryReBuildSize = 1.0
                reBuild(index,-1)
                if (ori) {
                    reBuildSizeH = majorReBuildSize
                    reBuildSizeV = secondaryReBuildSize
                } else {
                    reBuildSizeH = secondaryReBuildSize
                    reBuildSizeV = majorReBuildSize

                }
                majorRectangleD = majorFrameD
                secondaryRectangleD = secondaryFrameD

                //reBuild
            }

            if(!reBuilding){
                orderOfPlacementIndexes.add(index)

            }

            if (ori) {
                reBuildSizeH = majorReBuildSize
                reBuildSizeV = secondaryReBuildSize
                rectangleArray[index].width = majorRectangleD
                rectangleArray[index].height = secondaryRectangleD
                frameWidth = majorFrameD
                frameHeight = secondaryFrameD
                rectangleArray[index].upLeftPointY = frameHeight - rectangleArray[index].height
                rectangleArray[index].downRightPointX = frameWidth
                rectangleArray[index].downRightPointY = frameHeight
                frameHeight-= secondaryRectangleD
            }
            else {
                reBuildSizeV = majorReBuildSize
                reBuildSizeH = secondaryReBuildSize
                rectangleArray[index].width = secondaryRectangleD
                rectangleArray[index].height = majorRectangleD
                frameWidth = secondaryFrameD
                frameHeight = majorFrameD
                rectangleArray[index].upLeftPointX = frameWidth - rectangleArray[index].width
                rectangleArray[index].downRightPointX = frameWidth
                rectangleArray[index].downRightPointY = frameHeight
                frameWidth -= rectangleArray[index].width
            }

            frameState[index]=Pair(frameWidth, frameHeight)
        }

    if (lateRectangleIndexes.size>0 && index == rectangleArray.size-1) {
        if(frameWidth> frameHeight){
            if(!stitchASAP(Orientation.HORIZONTAL)) {
                if(! stitchASAP(Orientation.VERTICAL)){
                    direStitch = true
                    if(!stitchASAP(Orientation.HORIZONTAL))
                        throw Exception("Frame too large to be filled!(@stitchingASAP)")
                    direStitch = false
                }

            }
        }
        else{
            if(!stitchASAP(Orientation.HORIZONTAL))
                if(!stitchASAP(Orientation.VERTICAL)){
                    direStitch = true
                    if(!stitchASAP(Orientation.VERTICAL))
                        throw Exception("Frame too large to be filled!(@stitchingASAP)")
                    direStitch = false
                }
        }
        //stitchASAP
    }
    if (!late && !reBuilding) {
        val lateIndexer = lateRectangleIndexes.size
        for (i in 0 until lateIndexer) {
            if(i>= lateRectangleIndexes.size)break
            build(lateRectangleIndexes[i], true)
            lateRectangleIndexes.removeAt(i)
        }
    }


}
