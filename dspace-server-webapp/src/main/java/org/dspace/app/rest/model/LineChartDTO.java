/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import java.util.List;

/**
 *
 * @author root
 */
public class LineChartDTO {
    String type;    
    String name;
    String markerType="circle";
    boolean showInLegend =true;
    int markerSize=4;
    String color;
    int lineThickness=4;
    String yValueFormatString;
    List<LineChartdataPointsDTO> dataPoints;
    String legendMarkerColor;

    public LineChartDTO(String type, String name, String yValueFormatString, List<LineChartdataPointsDTO> dataPoints,String lineColor) {
        this.type = type;        
        this.name = name;
        this.yValueFormatString = yValueFormatString;
        this.dataPoints = dataPoints;
        this.color=lineColor;
        this.legendMarkerColor=lineColor;
    }
    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowInLegend() {
        return showInLegend;
    }

    public void setShowInLegend(boolean showInLegend) {
        this.showInLegend = showInLegend;
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(int markerSize) {
        this.markerSize = markerSize;
    }

    public String getyValueFormatString() {
        return yValueFormatString;
    }

    public void setyValueFormatString(String yValueFormatString) {
        this.yValueFormatString = yValueFormatString;
    }

    public List<LineChartdataPointsDTO> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<LineChartdataPointsDTO> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLegendMarkerColor() {
        return legendMarkerColor;
    }

    public void setLegendMarkerColor(String legendMarkerColor) {
        this.legendMarkerColor = legendMarkerColor;
    }
    
    
            
}
