package leaf03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridWithin;
import repast.simphony.space.Dimensions;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;





/// TODO perhaps should be wrapped into parser class
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import java.io.*;

import leaf03.utils.ParserUtils;


public class PhysicalAgent {
	
	protected ContinuousSpace <PhysicalAgent> space ;
	protected Grid <PhysicalAgent> grid ;
	
	public double mass;
	public double radius;
	protected double fX;
	protected double fY;
	
	//TODO: perhaps use some properties class
	static private double gridScaleXInv; 
	static private double gridScaleYInv; 
	
	protected Map<String, Boolean> reactionNamesMap = new HashMap<String, Boolean>();
	protected Map<String, String> speciesProperties;
	
	public void setReactionNamesMap(Map<String, Boolean> map) {
		this.reactionNamesMap = map;
	}
	
	public Boolean hasReaction(String reactionName) {
		if(!reactionNamesMap.containsKey(reactionName))
			return false;
		Boolean temp = reactionNamesMap.get(reactionName); 
		return temp;
	}

	@SuppressWarnings("unchecked")
	public PhysicalAgent() {
		
		// TODO move all this to static init methods in the zoo
//		Context<Object> context = ContextUtils.getContext((Object)this);
//		space = (ContinuousSpace<PhysicalAgent>) context.getProjection("space");;
//		grid = (Grid<PhysicalAgent>) context.getProjection("grid");
	}
	
	public void init() {
		gridScaleXInv = grid.getDimensions().getWidth() / space.getDimensions().getWidth();
		gridScaleYInv = grid.getDimensions().getHeight() / space.getDimensions().getHeight();
	}
	
	public void setMass(double mass) {
		this.mass = mass;
		this.radius = Math.sqrt(this.mass / Math.PI);
	}

	
	@SuppressWarnings("unchecked")
	//@ScheduledMethod ( start = 1, interval = 1, priority = 3)
	public void calculateForces() {
		Context<Object> context = ContextUtils.getContext((Object)this);

		int initialNeighborDistance = 5;
		GridWithin<Object> query = new GridWithin<Object>(context, this, initialNeighborDistance*gridScaleYInv);

		ArrayList<Object> foundOverlapping = new ArrayList<Object>();
		
		// instead of computing distance twice, use a map with object as key?
		
		for (Object o : query.query()){
			Bacteria neigh = (Bacteria)o;
			
			NdPoint q = space.getLocation(this);
			NdPoint p = space.getLocation(neigh);

			Dimensions dims = this.space.getDimensions();
			double w = dims.getWidth(); 
			double h = dims.getHeight();
			
			double qpX = q.getX() - p.getX();
			if (2.0f * Math.abs(qpX) > w) {
				if (qpX > 0) qpX = qpX - w;
				else qpX = qpX + w;
			}
			double qpY = q.getY() - p.getY();
			
			if (2.0f * Math.abs(qpY) > h) {
				if (qpY > 0) qpY = qpY - h;
				else qpY = qpY + h;
			}			

			double d = Math.pow(qpX*qpX + qpY*qpY, 0.5);

			if (d < this.radius + neigh.radius) {
				foundOverlapping.add((Object)neigh);
			}
		}
		this.fX = 0;
		this.fY = 0;
		for (Object o : foundOverlapping){
			Bacteria b = (Bacteria)o;
			NdPoint q = space.getLocation(this);
			NdPoint p = space.getLocation(b);
			
			Dimensions dims = this.space.getDimensions();
			double w = dims.getWidth(); 
			double h = dims.getHeight();
			double qpX = q.getX() - p.getX();
			if (Math.abs(qpX) > 0.5f * w) {
				if (qpX > 0) qpX = qpX - w;
				else qpX = qpX + w;
			}
			double qpY = q.getY() - p.getY();
			
			if (Math.abs(qpY) > 0.5f * h) {
				if (qpY > 0) qpY = qpY - h;
				else qpY = qpY + h;
			}			

			double d = Math.pow(qpX*qpX + qpY*qpY, 0.5);
			double hh = (this.radius + b.radius) - d;
			
			double F = Math.pow(d, 0.5) * Math.pow (hh, 1.5);

		    double Fxa = (F * qpX / d);
		    double Fya = (F * qpY / d);

		    this.fX = this.fX + Fxa;
		    this.fY = this.fY + Fya;
		
		}
	}

	//@ScheduledMethod ( start = 1, interval = 1, priority = 2)
	protected void followForces(double coeff) {
		NdPoint myPoint = space.getLocation(this);
		double nextX = myPoint.getX() + (this.fX / this.mass) * coeff;
		double nextY = myPoint.getY() + (this.fY / this.mass) * coeff;
		space.moveTo(this, nextX, nextY);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int) (myPoint.getX()*gridScaleXInv), (int) (myPoint.getY()*gridScaleYInv));
	}

	protected void moveByAngleAndDist(double angle, double dist) {
		NdPoint myPoint = space.getLocation(this);
		space.moveByVector(this, dist, angle, 0);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int) (myPoint.getX()*gridScaleXInv), (int) (myPoint.getY()*gridScaleYInv));

	}
	
	public String getGeo() {
		NdPoint myPoint = space.getLocation(this);
		
		return String.valueOf(this.getClass().getSimpleName() + " " + myPoint.getX()) + " " + String.valueOf(myPoint.getY()) + " " + String.valueOf(this.radius);  
	}


}
