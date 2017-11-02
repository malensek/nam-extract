/*
Copyright (c) 2017, University of San Francisco
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package edu.usfca.cs.nam.extract;

public class SpatialRange {
    private float upperLat;
    private float lowerLat;
    private float upperLon;
    private float lowerLon;

    public SpatialRange(float lowerLat, float upperLat,
            float lowerLon, float upperLon) {
        this.lowerLat = lowerLat;
        this.upperLat = upperLat;
        this.lowerLon = lowerLon;
        this.upperLon = upperLon;
    }

    public SpatialRange(SpatialRange copyFrom) {
        this.lowerLat = copyFrom.lowerLat;
        this.upperLat = copyFrom.upperLat;
        this.lowerLon = copyFrom.lowerLon;
        this.upperLon = copyFrom.upperLon;
    }

    /*
     * Retrieves the smallest latitude value of this spatial range going east.
     */
    public float getLowerBoundForLatitude() {
        return lowerLat;
    }

    /*
     * Retrieves the largest latitude value of this spatial range going east.
     */
    public float getUpperBoundForLatitude() {
        return upperLat;
    }

    /*
     * Retrieves the smallest longitude value of this spatial range going south.
     */
    public float getLowerBoundForLongitude() {
        return lowerLon;
    }

    /*
     * Retrieves the largest longitude value of this spatial range going south.
     */
    public float getUpperBoundForLongitude() {
        return upperLon;
    }

    public Coordinates getCenterPoint() {
        float latDifference = upperLat - lowerLat;
        float latDistance = latDifference / 2;

        float lonDifference = upperLon - lowerLon;
        float lonDistance = lonDifference / 2;

        return new Coordinates(lowerLat + latDistance,
                               lowerLon + lonDistance);
    }

}
