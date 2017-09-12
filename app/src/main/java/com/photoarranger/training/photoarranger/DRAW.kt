package com.photoarranger.training.photoarranger

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.photoarranger.training.photoarranger.Photos
import com.photoarranger.training.photoarranger.R


fun draw( frameWidth: Int, frameHeight: Int,array: ArrayList<Photos>, imView: ImageView): ImageView {

    var bitmap: Bitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888)
    var canvas: Canvas = Canvas(bitmap)
    var mImageView = imView
    var counter = 1
    for (i in array) {


        var drawRect: Rect = Rect(i.upLeftPointX, i.upLeftPointY, i.downRightPointX, i.downRightPointY)
        var strokePaint: Paint = Paint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 50f
        strokePaint.color = Color.BLACK
        canvas.drawRect(drawRect, strokePaint)
        strokePaint.strokeWidth = 20f
        strokePaint.textSize = 200f
        canvas.drawText(i.width.toString()+"x"+i.height.toString(), i.upLeftPointX+20f, i.upLeftPointY+250f, strokePaint)
        canvas.drawText("No."+i.originalOrder.toString(), i.upLeftPointX+20f, i.upLeftPointY+500f, strokePaint)

        mImageView.setImageBitmap(bitmap)
    }
    return mImageView
}
/**
 * Created by Training on 22/08/2017.
 */
