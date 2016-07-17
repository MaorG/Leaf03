package leaf03;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.GridValueLayer;
import leaf03.PhysicalAgent;
import leaf03.utils.ParserUtils;

public class Bacteria extends PhysicalAgent{
	
	public double divMass;

	static public void initAgentProperties(Context<PhysicalAgent> context, Node agentNode) {
	}

	
	
	public Bacteria() {
		
	}

	
	@SuppressWarnings("unchecked")
	@ScheduledMethod ( start = 1, interval = 1, priority = 2)
	public void reproduce() {
		if (this.mass > 2.0) {
			Context<PhysicalAgent> context = ContextUtils.getContext((Object)this);
			
			Zoo zoo = ((Leaf03Context)context).zoo;

			Bacteria clone = (Bacteria) zoo.createAgent("Bacteria");
			clone.setMass(mass*0.5);
			context.add(clone);

			this.mass = this.mass * 0.5;
			this.radius = Math.sqrt(this.mass / Math.PI);

			NdPoint myPoint = space.getLocation(this);
			space.moveTo(clone, myPoint.getX(), myPoint.getY());
			double angle = (float) RandomHelper.nextDoubleFromTo(0.0, 2.0*Math.PI);
			this.moveByAngleAndDist(angle, this.radius * 0.3);
			clone.moveByAngleAndDist(angle + Math.PI, clone.radius * 0.3);
		}
	}

}
	

