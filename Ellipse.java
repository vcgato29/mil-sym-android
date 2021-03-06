/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.geo.shape;

import sec.geo.GeoEllipse;
import armyc2.c2sd.JavaTacticalRenderer.mdlGeodesic;
import armyc2.c2sd.JavaLineArray.POINT2;
import java.util.ArrayList;
import armyc2.c2sd.JavaLineArray.ref;
import sec.geo.GeoPoint;
import sec.geo.ShapeObject;
import sec.geo.kml.KmlOptions;
import armyc2.c2sd.graphics2d.*;

/**
 *
 * @author Michael Deutch
 */
public class Ellipse {
    protected GeoPoint pivot;
    protected double radiusMeters;
    private ShapeObject shape;
    protected double maxDistanceMeters;
    protected double flatnessDistanceMeters;
    protected KmlOptions.AltitudeMode altitudeMode;
    private double minAltitudeMeters;
    private double maxAltitudeMeters;
    protected int limit;
        private double _semiMajor=0;
        private double _semiMinor=0;
        private double _rotation=0;
        public Ellipse(double semiMajor,double semiMinor,double rotation)
        {
            _semiMajor=semiMajor;
            _semiMinor=semiMinor;
            _rotation=rotation;
            limit=4;
            flatnessDistanceMeters=2;
            maxDistanceMeters=200000;
        }
	//@Override
	public ShapeObject createShape() {
		//GeoEllipse e = new GeoEllipse(pivot, radiusMeters * 2, radiusMeters * 2, maxDistanceMeters,
		//		flatnessDistanceMeters, limit);
		GeoEllipse e = new GeoEllipse(pivot, _semiMajor * 2, _semiMinor * 2, maxDistanceMeters,
				flatnessDistanceMeters, limit);
                
                float[] coords = new float[2];
                int type=0;
                POINT2 pt0=new POINT2(pivot.x,pivot.y),pt=null;
                POINT2 pt1=null;
                double R=0;
                ref<double[]> a12 = new ref(), a21 = new ref();
                double x=0,y=0,x1=0,y1=0;
                //test arbitray rotation angle                
                double rotation=_rotation;
                //navigation is clockwise from 0. 0 is true north
                rotation=90-rotation;
                if(rotation == 0 || _semiMajor==_semiMinor)
                    return new ShapeObject(e);                
                ArrayList<POINT2>pts=new ArrayList();
                for (PathIterator i = e.getPathIterator(null); !i.isDone(); i.next()) {
                    type = i.currentSegment(coords);
                    pt1=new POINT2(coords[0],coords[1]);
                    R=mdlGeodesic.geodesic_distance(pt0, pt1, a12, a21);
                    //x=R*Math.cos(a12.value[0]*Math.PI/180d);
                    //y=R*Math.sin(a12.value[0]*Math.PI/180d);                  
                    //rotate the points
                    //x1=x*Math.cos(rotation*Math.PI/180d)-y*Math.sin(rotation*Math.PI/180d);
                    //y1=x*Math.sin(rotation*Math.PI/180d)+y*Math.cos(rotation*Math.PI/180d);
                    pt=mdlGeodesic.geodesic_coordinate(pt0, R, a12.value[0]-rotation);
                    pts.add(pt);                    
                }
                //clear the path
                GeneralPath path=e.getPath();
                //path.reset();
                path.getPath().reset();
                //rebuild the path with the rotated points
                for(int j=0;j<pts.size();j++)
                {
                    x=pts.get(j).x;
                    y=pts.get(j).y;
                    if(j==0)
                        path.moveTo(x, y);
                    else
                        path.lineTo(x, y);
                }
		return new ShapeObject(e);
	} 
        public ArrayList<GeoPoint>getEllipsePoints()
        {
		GeoEllipse e = new GeoEllipse(pivot, _semiMajor * 2, _semiMinor * 2, maxDistanceMeters,
				flatnessDistanceMeters, limit);
                
                float[] coords = new float[2];
                int type=0;
                POINT2 pt0=new POINT2(pivot.x,pivot.y),pt=null;
                POINT2 pt1=null;
                double R=0;
                ref<double[]> a12 = new ref(), a21 = new ref();
                double x=0,y=0,x1=0,y1=0;
                double rotation=_rotation;
                //navigation is clockwise from 0. 0 is true north
                rotation=90-rotation;
                ArrayList<GeoPoint>pts=new ArrayList();
                for (PathIterator i = e.getPathIterator(null); !i.isDone(); i.next()) {
                    type = i.currentSegment(coords);
                    pt1=new POINT2(coords[0],coords[1]);
                    R=mdlGeodesic.geodesic_distance(pt0, pt1, a12, a21);
                    if(!(_semiMajor == _semiMinor))
                        pt=mdlGeodesic.geodesic_coordinate(pt0, R, a12.value[0]-rotation);
                    else
                        pt=pt1;
                    pts.add(new GeoPoint(pt.x,pt.y));                    
                }
                //clear the path
                GeneralPath path=e.getPath();
                path.getPath().reset();
                //rebuild the path with the rotated points
                for(int j=0;j<pts.size();j++)
                {
                    x=pts.get(j).x;
                    y=pts.get(j).y;
                    if(j==0)
                        path.moveTo(x, y);
                    else
                        path.lineTo(x, y);
                }
		return pts;            
        }
    protected void shapeChanged() {
        shape = null;
    }

    public double getMinAltitude() {
        return minAltitudeMeters;
    }

    public void setMinAltitude(double minAltitudeMeters) {
        this.minAltitudeMeters = minAltitudeMeters;
        shapeChanged();
    }

    public double getMaxAltitude() {
        return maxAltitudeMeters;
    }

    public void setMaxAltitude(double maxAltitudeMeters) {
        this.maxAltitudeMeters = maxAltitudeMeters;
        shapeChanged();
    }

    public void setMaxDistance(double maxDistanceMeters) {
        this.maxDistanceMeters = maxDistanceMeters;
        shapeChanged();
    }

    public void setFlatness(double flatnessDistanceMeters) {
        this.flatnessDistanceMeters = flatnessDistanceMeters;
        shapeChanged();
    }

    public void setLimit(int limit) {
        this.limit = limit;
        shapeChanged();
    }
    public void setPivot(GeoPoint pvt) {
        this.pivot = pvt;
        shapeChanged();
    }

    public KmlOptions.AltitudeMode getAltitudeMode() {
        return altitudeMode;
    }

    public void setAltitudeMode(KmlOptions.AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }
        
}
