package leaf03;


import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import repast.simphony.context.DefaultContext;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.Dimensions;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.valueLayer.BufferedGridValueLayer;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.valueLayer.ValueLayerDiffuser;

public class Leaf03Context extends DefaultContext<PhysicalAgent> {
	private ValueLayerDiffuser diffuserCod;
	private ValueLayerDiffuser diffuserSurf;

	private Map<String, ValueLayerDiffuser> diffuserMap = new HashMap<String, ValueLayerDiffuser>();

	  public void addValueLayer(ValueLayer valueLayer) {
		    // TODO Auto-generated method stub
		    super.addValueLayer(valueLayer);
		    diffuserCod = new ValueLayerDiffuser((IGridValueLayer)valueLayer, 1.0, 1.0, true);
	  }
	
	
	/* (non-Javadoc)
	 * @see repast.simphony.context.AbstractContext#addValueLayer(repast.simphony.valueLayer.ValueLayer)
	 */
	public void addValueLayerAndDiffuser(ValueLayer valueLayer, double evaporationConst, double diffusionConst) {
		// TODO Auto-generated method stub
		super.addValueLayer(valueLayer);


		ValueLayerDiffuser diffuser = new ValueLayerDiffuser((IGridValueLayer)valueLayer, evaporationConst, diffusionConst, true);
		diffuserMap.put(valueLayer.getName(), diffuser);

	}

	public void addValueLayerAndDiffuser(ValueLayer valueLayer, double evaporationConst, String diffusionLayerName) {
		// TODO Auto-generated method stub
		super.addValueLayer(valueLayer);

		GridValueLayer diffusionLayer = (GridValueLayer)getValueLayer(diffusionLayerName);
		ValueLayerDiffuser diffuser = new ValueLayerDiffuserHeterogeneous((IGridValueLayer)valueLayer, evaporationConst, 0.0, true);
		((ValueLayerDiffuserHeterogeneous)diffuser).setDiffusionLayer(diffusionLayer);
		diffuser.setMinValue(0.0);
		diffuserMap.put(valueLayer.getName(), diffuser);

	}


	@ScheduledMethod(start = 1, interval = 1, priority = -2)
	public void permeate() {
		GridValueLayer gridCOD = (GridValueLayer)getValueLayer("COD");
		GridValueLayer gridSurf = (GridValueLayer)getValueLayer("surf");
		Dimensions dims = gridCOD.getDimensions();
		double w = dims.getWidth(); 
		double h = dims.getHeight();

		for (int i = 0; i < (int)w; i++) {
			for (int j = 0; j < (int)h; j++) {
				double val = gridCOD.get(i,j);
				//double perm = gridSurf.get(i,j);
				
				double delta = 1.0 - val;
				//val = val + 0.00001 * perm * delta;
				
				val = val + 0.01 * delta;
				gridCOD.set(val, i,j);
			}
		}
	}


	// priority = -1 so that the heatbugs action occurs first
	@ScheduledMethod(start = 1, interval = 1, priority = -3)
	public void swap() {
//	    diffuserCod.diffuse();
		for (int i = 1; i < 50; i++) { 
		
		for (Entry<String, ValueLayerDiffuser> entry : diffuserMap.entrySet()) {
		    String key = entry.getKey();
		    ValueLayerDiffuser value = entry.getValue();
		    
		    GridValueLayer grid = (GridValueLayer)getValueLayer(key);		
		    value.diffuse();
		}
	}
	}

	@ScheduledMethod ( start = 1, interval = 1, priority = 1)
	public void step() {
		IndexedIterable<PhysicalAgent> allAgents = getObjects(Object.class);
		for (int i = 0; i < 50; i++) {
			for (PhysicalAgent b : allAgents) {
				b.calculateForces();
				b.followForces(1);			
			}
		}
	}
}
