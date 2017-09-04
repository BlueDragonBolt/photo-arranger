package com.photoarranger.training.photoarranger

import android.content.ClipData.Item


/**
 * Created by Training on 14/08/2017.
 */

class Photos  (W : Int, H: Int) : Comparable<Photos> {
    var height = H
    var width = W
    var orientation = getOrientation("SQUARE")
    var aspectRatio: Double = 1.0
    var upLeftPointX: Int = 0
    var upLeftPointY: Int = 0
    var downRightPointX: Int = 0
    var downRightPointY: Int = 0
    var originalOrder = 0
    init{
        calculateAspect()
        calculateOrientation()
    }
    constructor( W: Int, H:Int, left: Int, top: Int, right: Int, bottom: Int): this(W, H){
         height = H
         width = W
         upLeftPointX = left
         upLeftPointY = top
         downRightPointX = right
         downRightPointY = bottom
    }


    override operator fun compareTo(other: Photos): Int {
         if (this.aspectRatio < other.aspectRatio) {
            return -1
        } else {
             if(this.aspectRatio > other.aspectRatio)
                 return 1
             return if (this.height * this.width < other.height * other.width)
                 -1
             else
                 1
        }
    }

    fun calculateAspect() {
            aspectRatio = minOf(width.toDouble()/height, height.toDouble()/width)

    }

    fun calculateOrientation(){
        if(height>width)
           orientation = Orientation.VERTICAL

        else if(height<width)
            orientation =  Orientation.HORIZONTAL

        else orientation = Orientation.SQUARE
    }
}