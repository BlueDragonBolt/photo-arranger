package com.photoarranger.training.photoarranger

import java.util.*

/**
 * Created by Training on 14/08/2017.
 */
private var spaceHeight: Int = 0
private var spaceWidth: Int = 0
private var rectangleArrayHeight: Array<Photos> = arrayOf(Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0))
private var rectangleArrayWidth: Array<Photos> = arrayOf(Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0),Photos(0,0))
private var arraySize = 0
fun stacker( spaceW: Int, spaceH: Int, recArray: Array<Photos>, startX: Int, startY: Int): Int {

     spaceHeight = spaceH
     spaceWidth = spaceW
    for (index in 0..recArray.size - 2) {
        if (recArray[index].orientation != recArray[index + 1].orientation)
            throw Exception("Rectangle orientations must match!")
    }
    if (spaceHeight < 600 || spaceWidth < 600)
        throw IllegalStateException("space must be at least 600x600!")

    for(i in 0..recArray.size-1){
        rectangleArrayHeight[i].height = recArray[i].height
        rectangleArrayHeight[i].width = recArray[i].width
        rectangleArrayHeight[i].orientation = recArray[i].orientation
        rectangleArrayHeight[i].aspectRatio = recArray[i].aspectRatio
        rectangleArrayWidth[i].height = recArray[i].height
        rectangleArrayWidth[i].width = recArray[i].width
        rectangleArrayWidth[i].orientation = recArray[i].orientation
        rectangleArrayWidth[i].aspectRatio = recArray[i].aspectRatio
    }
    arraySize = recArray.size
    val pixelLossHeight = stackHeight()
   val pixelLossWidth = stackWidth()
    if(pixelLossHeight == null && pixelLossWidth == null)
        throw Exception("space is impossible to be filled!")
    else if(pixelLossHeight == null || pixelLossWidth!=null && Math.abs(pixelLossWidth)<Math.abs(pixelLossHeight)) {
        
        val aspectModPerPixel: Double = (pixelLossWidth!!.toDouble()/spaceHeight + spaceWidth)/ spaceWidth
        
        var prevEnd=startX
        for(index in 0..arraySize){
            rectangleArrayWidth[index].upLeftPointX = prevEnd
            rectangleArrayWidth[index].upLeftPointY = startY
            rectangleArrayWidth[index].downRightPointY = spaceHeight
            rectangleArrayWidth[index].downRightPointX = rectangleArrayWidth[index].upLeftPointX + Math.round(rectangleArrayWidth[index].width/ aspectModPerPixel).toInt()
            prevEnd = rectangleArrayWidth[index].downRightPointX
        }
        rectangleArrayWidth[arraySize-1].downRightPointX = spaceWidth + startX
        //return rectangleArrayWidth
        return pixelLossWidth
    }
    
    else{
        val aspectModPerPixel: Double = (pixelLossHeight.toDouble()/spaceWidth + spaceHeight)/ spaceHeight

        var prevEnd=startY
        for(index in 0..arraySize-1){
            rectangleArrayHeight[index].upLeftPointY = prevEnd
            rectangleArrayWidth[index].upLeftPointX = startX
            rectangleArrayHeight[index].downRightPointX = spaceHeight
            rectangleArrayHeight[index].downRightPointY = rectangleArrayHeight[index].upLeftPointY + Math.round(rectangleArrayHeight[index].width*aspectModPerPixel).toInt()
            prevEnd = rectangleArrayHeight[index].downRightPointY
        }
        rectangleArrayHeight[arraySize-1].downRightPointY = spaceHeight + startY
       // return rectangleArrayHeight
        return pixelLossHeight
    }
}

private fun stackHeight(): Int?{

    if(spaceHeight< arraySize*600)
        return null

    var sizeChecker = 0
    for(i in 0..arraySize-1){
        sizeChecker+= rectangleArrayHeight[i].height*3
    }
        if(sizeChecker< spaceHeight)
            return null

    var pixelLossHeight : Int = 0



    for (index in 0..arraySize - 1) {
        rectangleArrayHeight[index].width = spaceWidth

        val tmpHeight = rectangleArrayHeight[index].height
        rectangleArrayHeight[index].height = Math.round(((rectangleArrayHeight[index].width).toDouble() / rectangleArrayHeight[index].aspectRatio)).toInt()
        pixelLossHeight += rectangleArrayHeight[index].height

        if (rectangleArrayHeight[index].height < 600) {
            rectangleArrayHeight[index].height = 600
        }

       else if(tmpHeight*3<rectangleArrayHeight[index].height){
            rectangleArrayHeight[index].height = tmpHeight*3
        }

    }

    return (pixelLossHeight - spaceHeight) * spaceWidth
}

private fun stackWidth(): Int? {

    if(spaceWidth< arraySize*600)
        return null

    var sizeChecker = 0
    for(i in 0..arraySize-1){
        sizeChecker+= rectangleArrayWidth[i].width*3
    }

    if(sizeChecker< spaceWidth)
        return null

    var pixelLossWidth : Int = 0

    for (index in 0..arraySize - 1) {
        rectangleArrayWidth[index].height = spaceHeight
        val tmpWidth = rectangleArrayWidth[index].width

        rectangleArrayWidth[index].width = Math.round(((rectangleArrayWidth[index].height).toDouble() * rectangleArrayWidth[index].aspectRatio)).toInt()
        pixelLossWidth += rectangleArrayWidth[index].width

        if (rectangleArrayWidth[index].width < 600) {
            rectangleArrayWidth[index].width = 600
            rectangleArrayWidth[index].calculateAspect()
        }

        else if(tmpWidth*3<rectangleArrayWidth[index].width){
            rectangleArrayWidth[index].height = tmpWidth*3

        }
    }

        return (pixelLossWidth - spaceWidth) * spaceHeight
}