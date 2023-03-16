/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import java.util.Date;

/**
 *
 * @author root
 */
public class LineChartdataPointsDTO {
    String x;
    int y;
    String label;
    int donwloadcount;
    public LineChartdataPointsDTO(String x, int y) {
        this.x = x;
        this.y = y;
       
    }
    public LineChartdataPointsDTO(String x, int y,int donwloadcount) {
        this.x = x;
        this.y = y;
        this.donwloadcount=donwloadcount;
    }
    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDonwloadcount() {
        return donwloadcount;
    }

    public void setDonwloadcount(int donwloadcount) {
        this.donwloadcount = donwloadcount;
    }
    

    
}
