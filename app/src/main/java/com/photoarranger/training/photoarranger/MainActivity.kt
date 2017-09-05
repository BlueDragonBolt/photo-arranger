package com.photoarranger.training.photoarranger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var mImageView: ImageView = findViewById(R.id.mImageView) as ImageView
        var photoArray: ArrayList<Photos> = arrayListOf(Photos(1200, 3600), Photos(3000, 1500), Photos(2100, 2100), Photos(900, 2100), Photos(4200, 600))
        val frameHeight = 4200
        val frameWidth = 4200
        val myColor = resources.getColor(R.color.Black)
        var built = builder(frameWidth, frameHeight, photoArray)
        var building = arrayListOf<Photos>()
        var step: Button = findViewById(R.id.stepButton) as Button
        var i = 0
        step.setOnClickListener {
            if (i < built.size) {
                building.add(built[i])
                mImageView = draw(frameWidth + 10, frameHeight + 10, building, mImageView)
                i++
            }
        }
    }
}
