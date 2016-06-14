package leaf03;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridWithin;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.continuous.ContinuousSpace ;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.BufferedGridValueLayer;
import leaf03.PhysicalAgent;

public class Bacteria extends PhysicalAgent{

    private BufferedGridValueLayer COD;
    private BufferedGridValueLayer surf;
	private double kappa;
	private double g;
	
	
	public Bacteria(Context<PhysicalAgent> context, double mass, double g, double kappa) {
		
		super(context, mass);
	    this.COD = (BufferedGridValueLayer) context.getValueLayer("COD");
	    this.surf = (BufferedGridValueLayer) context.getValueLayer("surf");
		this.g = g;
	    this.kappa = kappa;
	    
		this.radius = Math.sqrt(this.mass / Math.PI);
	}

	@ScheduledMethod ( start = 1, interval = 1, priority = 1)
	public void eat() {
	
	    //g * mass * ( subs-N / (kappa-N + subs-N))
	    GridPoint pt = grid.getLocation(this);
	    double codHere = COD.get(pt.getX(), pt.getY());

		double dm = this.mass * (codHere / (codHere + this.kappa));
		
		double nextCodHere = codHere - dm;
	    COD.set(nextCodHere, pt.getX(), pt.getY());

	    this.mass = this.mass + dm * this.g;
	    this.radius = Math.sqrt(this.mass / Math.PI);

	
	
	}
	
	@ScheduledMethod ( start = 1, interval = 1, priority = 1)
	public void emit() {
	
	    //g * mass * ( subs-N / (kappa-N + subs-N))
	    GridPoint pt = grid.getLocation(this);
	    double surfOutput = 0.05;
	    //double temp = 
	    surf.set(surfOutput + surf.get(pt.getX(), pt.getY()), pt.getX(), pt.getY());
	}
	
    

	
	@SuppressWarnings("unchecked")
	@ScheduledMethod ( start = 1, interval = 1, priority = 2)
	public void reproduce() {
		if (this.mass > 2.0) {
			Context<PhysicalAgent> context = ContextUtils.getContext((Object)this);

			Bacteria clone = new Bacteria(context, this.mass * 0.5, this.g, this.kappa);
			context.add(clone);

			this.mass = this.mass * 0.5;
			this.radius = Math.sqrt(this.mass / Math.PI);

			
			NdPoint myPoint = space.getLocation(this);
			space.moveTo(clone, myPoint.getX(), myPoint.getY());
			double angle = (float) RandomHelper.nextDoubleFromTo(0.0, 2.0*Math.PI);
			this.moveByAngleAndDist(angle, this.radius * 0.3f);
			clone.moveByAngleAndDist(angle + Math.PI, clone.radius * 0.3f);
		}
	}

}
	

