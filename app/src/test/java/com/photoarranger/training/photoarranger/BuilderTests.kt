package com.photoarranger.training.photoarranger

import org.junit.Assert
import org.junit.Test

/**
 * Created by Training on 22/08/2017.
 */
class BuilderUnitTests{


    @Test
    fun GenericTests(){
        var photoArray: ArrayList<Photos> = arrayListOf( Photos(12000, 36000), Photos(30000, 15000), Photos(21000, 21000), Photos(9000, 21000),Photos(42000,6000))
        /*for(i in 0..1) {
            photoList[i].calculateOrientation()
            photoList[i].calculateAspect()
        }*/
        var answer = builder(42000, 42000, photoArray)
        Assert.assertEquals(answer, 1152000)
        // photoArray = arrayOf<Photos>(Photos(1000, 1000), Photos(1000, 1000), Photos(1000,1000))
        //  answer = stacker(1000, 600, photoArray, 0, 0)
        // Assert.assertEquals(answer, 1)
        //   photoArray = arrayOf<Photos>(Photos(1000, 1000), Photos(1000, 1000), Photos(1000,1000))
        //  answer = stacker(10000, 10000, photoArray, 0, 0)
    }
}