package leaf03;

import leaf03.Leaf03ContextBuilder;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.valueLayer.BufferedGridValueLayer;

public class Leaf03ContextBuilder implements ContextBuilder<PhysicalAgent> {

	@Override
	public Context<PhysicalAgent> build(Context<PhysicalAgent> context) {

		context.setId("Leaf03");
		
		// get g, kappa from file
		double g = 2.0;
		double kappa = 100.0;
		
		//
		double h = 500;
		double w = 500;
			
		
		ContinuousSpaceFactory spaceFactory = 
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<PhysicalAgent> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<PhysicalAgent>(),
				new repast.simphony.space.continuous.WrapAroundBorders(),
				h,w);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<PhysicalAgent> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<PhysicalAgent>(new WrapAroundBorders(),
				new SimpleGridAdder<PhysicalAgent>(),
				true, (int)h, (int)w));
		
		
	    BufferedGridValueLayer COD = new BufferedGridValueLayer("COD", 1.0, true, new WrapAroundBorders(), new int[]{(int)h, (int)w}, new int[]{0,0});
	    
		int width = (int) COD.getDimensions().getWidth();
		int height = (int) COD.getDimensions().getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > width/2){
					COD.set(0.0, x, y);
				}
			}
		}
		
		//((Leaf03Context)context).addValueLayerAndDiffuser(COD, 1.0, 1.0);	

		context.addValueLayer(COD);
		
	    BufferedGridValueLayer surf = new BufferedGridValueLayer("surf", 0.0, true, new WrapAroundBorders(), new int[]{(int)h, (int)w}, new int[]{0,0});
		((Leaf03Context)context).addValueLayerAndDiffuser(surf, 0.95, 1.0);	

		
		int bactCount = 5;
		
		for (int i = 0; i < bactCount; i++) {
			context.add(new Bacteria(context, 1.0, g, kappa));
		}

		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo((Bacteria)obj, (int)pt.getX(), (int)pt.getY());
		}
		

		
		return context;
	}


//	@ScheduledMethod ( start = 1, interval = 1, priority = 1)
//	public void step() {
//		
//		IndexedIterable<Bacteria> allAgents = myContext.getObjects(Bacteria.class);
//		for (int i = 0; i < 50; i++) {
//			for (Bacteria b : allAgents) {
//				//Bacteria b = (Bacteria)o;
//				b.calculateForces();
//				b.followForces(1);			
//			}
//		}
//	}

}
