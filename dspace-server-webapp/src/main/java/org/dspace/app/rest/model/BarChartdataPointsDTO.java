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
public class BarChartdataPointsDTO {    
    int y;
    String label;

    public BarChartdataPointsDTO(String label, int y) {
        this.label = label;
        this.y = y;
    }
    
   

    public BarChartdataPointsDTO() {
		super();
	}



	public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
}
