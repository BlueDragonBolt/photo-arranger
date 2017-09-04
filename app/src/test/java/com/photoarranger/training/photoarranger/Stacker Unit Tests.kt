package com.photoarranger.training.photoarranger

import org.junit.Assert
import org.junit.Test


/**
 * Created by Training on 14/08/2017.
 */
class StackerUnitTests{


    @Test
    fun GenericTests(){
        var photoArray = arrayOf<Photos>(Photos(1440, 400), Photos(1440, 400))
        /*for(i in 0..1) {
            photoList[i].calculateOrientation()
            photoList[i].calculateAspect()
        }*/
       var answer = stacker(2400, 600, photoArray, 0, 0)
        Assert.assertEquals(answer, 1152000)
       // photoArray = arrayOf<Photos>(Photos(1000, 1000), Photos(1000, 1000), Photos(1000,1000))
      //  answer = stacker(1000, 600, photoArray, 0, 0)
       // Assert.assertEquals(answer, 1)
     //   photoArray = arrayOf<Photos>(Photos(1000, 1000), Photos(1000, 1000), Photos(1000,1000))
      //  answer = stacker(10000, 10000, photoArray, 0, 0)
    }
}