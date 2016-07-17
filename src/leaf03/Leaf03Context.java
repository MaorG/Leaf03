package leaf03;


import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jscience.physics.amount.Amount;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import java.io.*;

import leaf03.utils.ParserUtils;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.Dimensions;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.valueLayer.BufferedGridValueLayer;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.valueLayer.ValueLayerDiffuser;


public class Leaf03Context extends DefaultContext<PhysicalAgent> {


	private int width;
	private int height;
	public double scale;
	public double stepTime;
	public double endTime;

	public Zoo zoo;

	private Map<String, ValueLayerDiffuser> diffuserMap;
	private Map<String, Reaction> reactionMap;
	private Waterscape waterscape;

	Schedule schedule;
	
	int ticks;
	
	@SuppressWarnings("unchecked")
	public void init(Node simulationNode) throws ClassNotFoundException {
		
		diffuserMap = new HashMap<String, ValueLayerDiffuser>();
		reactionMap = new HashMap<String, Reaction>();
		
		schedule = (Schedule) RunEnvironment.getInstance().getCurrentSchedule();
		//schedule.setTimeUnits;
		
		// init domain
		Node domainNode = ParserUtils.getNodesByTagName(simulationNode, "domain").get(0);
		initDomain(domainNode);

		// init waterscape
		Node waterscapeNode = ParserUtils.getNodesByTagName(simulationNode, "waterscape").get(0);
		initWaterscape(waterscapeNode);
		
		// init solutes
		Node solutesNode = ParserUtils.getNodesByTagName(simulationNode, "solutes").get(0);
		initSolutesAndDiffusers(solutesNode);

		// init reactions
		Node reactionsNode = ParserUtils.getNodesByTagName(simulationNode, "reactions").get(0);
		initReactions(reactionsNode);

		// init Zoo (move to a func of context and init node)
		this.zoo = new Zoo(this, (ContinuousSpace<PhysicalAgent>) this.getProjection("space"), (Grid<PhysicalAgent>) this.getProjection("grid"));


		// init bacteria
		// get g, kappa from file
		double g = 0.10;
		double kappa = 1.0;
		@SuppressWarnings("unchecked")
		ContinuousSpace <PhysicalAgent> space = (ContinuousSpace<PhysicalAgent>) this.getProjection("space");
		@SuppressWarnings("unchecked")
		Grid <PhysicalAgent> grid = (Grid<PhysicalAgent>) this.getProjection("grid");


		Zoo zoo = new Zoo(this, space, grid);
		Node agentsNode = ParserUtils.getNodesByTagName(simulationNode, "agents").get(0);
		zoo.init(agentsNode);
		this.zoo = zoo;


		/// move all this into zoo

		int bactCount = 00;

		for (int i = 0; i < bactCount; i++) {
			this.add(zoo.createAgent("Bacteria"));
		}

		for (int i = 0; i < 0; i++) {
			this.add(zoo.createAgent("EmittingBacteria"));
		}

		for (Object obj : this) {
			((PhysicalAgent)obj).setMass(1.0);
			NdPoint pt = space.getLocation(obj);
			grid.moveTo((PhysicalAgent)obj, (int)(pt.getX()/scale), (int)(pt.getY()/scale));
		}


	}

	private void initWaterscape(Node waterscapeNode) {
		waterscape = new Waterscape(this, waterscapeNode);
	}

	private void initReactions(Node reactionsNode) {
		List<Node> reactionNodeList = ParserUtils.getNodesByTagName(reactionsNode, "reaction");

		// create solutes
		for (int i = 0; i < reactionNodeList.size(); i++) {

			Element e = (Element)reactionNodeList.get(i);
			String reactionType = e.getAttribute("type");

			if (reactionType.equalsIgnoreCase("monod")) { 
				Reaction reaction = new ReactionMonod(this, reactionNodeList.get(i));
				reactionMap.put(reaction.name, reaction);
			}
			if (reactionType.equalsIgnoreCase("const")) { 
				Reaction reaction = new ReactionConst(this, reactionNodeList.get(i));
				reactionMap.put(reaction.name, reaction);
			}
		}
	}


	private void initSolutesAndDiffusers(Node solutesNode) {
		List<Node> soluteNodeList = ParserUtils.getNodesByTagName(solutesNode, "solute");

		// create solutes
		for (int i = 0; i < soluteNodeList.size(); i++) {
			createSolute(soluteNodeList.get(i));
		}

		// attach diffusers
		for (int i = 0; i < soluteNodeList.size(); i++) {
			try {
				attachDiffuser(soluteNodeList.get(i));
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		// TODO: delete 
		// temp: init some pattern for solutes

		GridValueLayer COD = (GridValueLayer)getValueLayer("COD");
		GridValueLayer surf = (GridValueLayer)getValueLayer("surfactant");

		int width = (int) COD.getDimensions().getWidth();
		int height = (int) COD.getDimensions().getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double r = Math.sqrt((x - width/2)*(x - width/2) + (y - height/2)*(y - height/2));

				if (r > width / 3) {
//					double v = Math.cos(Math.PI * r * 0.5 / (double)(width/4));
//					v = 1.0;
//				if (x < (width * 0.25) || x > (width * 0.75)) {
					COD.set(0.0 , x, y);
				}


				if (true) {
					surf.set(Math.max((double)y/ (double)height, 0.0), x, y);
				}
			}
		}


	}

	private void initDomain(Node domainNode) {
		width = ParserUtils.getIntByName(domainNode, "width");
		height = ParserUtils.getIntByName(domainNode, "height");
		scale = ParserUtils.getDoubleByName(domainNode, "scale");
		stepTime = ParserUtils.getDoubleByName(domainNode, "stepTime");
		endTime = ParserUtils.getDoubleByName(domainNode, "endTime");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<PhysicalAgent> space = spaceFactory.createContinuousSpace("space", this,
				new RandomCartesianAdder<PhysicalAgent>(),
				new repast.simphony.space.continuous.WrapAroundBorders(),
				width*scale,height*scale);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<PhysicalAgent> grid = gridFactory.createGrid("grid", this,
				new GridBuilderParameters<PhysicalAgent>(new WrapAroundBorders(),
						new SimpleGridAdder<PhysicalAgent>(),
						true, (int)width, (int)height));
	}

	private void attachDiffuser(Node node) throws RuntimeException {

		// TODO - evap
		// TODO - move to some other class... CustumValueLayerDiffuser?

		Element e = (Element)node;
		String name = e.getAttribute("name");
		String diffusionType = ParserUtils.getStringByName(node, "diffusionType");

		if (diffusionType.equals("Homogeneous")) {
			// add homogeneous diffuser
			GridValueLayer valueLayer = (GridValueLayer)getValueLayer(name);
			double diffusionCoeff = ParserUtils.getDoubleByName(node, "diffusionCoeff");

			ValueLayerDiffuser diffuser = new ValueLayerDiffuser4((IGridValueLayer)valueLayer, 1.0, diffusionCoeff, true);
			((ValueLayerDiffuser4) diffuser).setScale(scale);



			diffuserMap.put(valueLayer.getName(), diffuser);


		} else if (diffusionType.equals("Heterogeneous")) {
			// add heterogeneous diffuser

			GridValueLayer valueLayer = (GridValueLayer)getValueLayer(name);
			String diffusionLayerName = ParserUtils.getStringByName(node, "diffusionLayer");
			String diffusionExpression = ParserUtils.getStringByName(node, "diffusionCoeff");

			String waterLevelLayerName = ParserUtils.getStringByName(node, "waterLevelLayer");

			ValueLayerDiffuser diffuser;
			if (waterLevelLayerName == null) {
				diffuser = new ValueLayerDiffuserHeterogeneous((IGridValueLayer)valueLayer, 1.0, 0.0, true);

			}
			else {
				diffuser = new ValueLayerDiffuserHeterogeneousWaterLevel((IGridValueLayer)valueLayer, 1.0, 0.0, true);
				GridValueLayer waterLevelLayer = (GridValueLayer)getValueLayer("waterLevel");
				((ValueLayerDiffuserHeterogeneousWaterLevel)diffuser).setWaterLevelLayer(waterLevelLayer);
			}
			GridValueLayer diffusionLayer = (GridValueLayer)getValueLayer(diffusionLayerName);
			((ValueLayerDiffuserHeterogeneous)diffuser).setDiffusionLayer(diffusionLayer);
			((ValueLayerDiffuserHeterogeneous)diffuser).setDiffusionExpression(diffusionExpression);		
			((ValueLayerDiffuserHeterogeneous)diffuser).setScale(scale);

			diffuserMap.put(valueLayer.getName(), diffuser);
		} else { 
			throw new RuntimeException("unexpected diffusion type");
		}





	}

	private void createSolute(Node node) {

		Element e = (Element)node;
		String name = e.getAttribute("name");
		Node concentrationNode = ParserUtils.getNodesByName(node, "concentration").get(0);
		double conc = Double.parseDouble(concentrationNode.getTextContent());
		// TODO replace above 2 lines?

		GridValueLayer gridValueLayer = new GridValueLayer(name, conc, true, new WrapAroundBorders(), new int[]{(int)width, (int)height}, new int[]{0,0});
		addValueLayer(gridValueLayer);
	}

	//@ScheduledMethod(start = 1, interval = 1, priority = -2)
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


	@ScheduledMethod(start = 1, interval = 1, priority = -3)
	public void diffuse() {
		for (Entry<String, ValueLayerDiffuser> entry : diffuserMap.entrySet()) {

			long startTime = System.currentTimeMillis();

			String soluteName = entry.getKey();
			ValueLayerDiffuser diffuser = entry.getValue();


			double dt = stepTime;
			if (CustumValueLayerDiffuser.class.isAssignableFrom(diffuser.getClass())){
				dt = ((CustumValueLayerDiffuser) diffuser).determineMaxStableDt();
			}			//double dt = getMaxStableDt(diffuser);

			int repeats = (int) Math.ceil(stepTime / dt);
			GridValueLayer grid = (GridValueLayer)getValueLayer(soluteName);	
			for (int j = 0; j < repeats; j++) {
				diffuser.diffuse();
			}
			long endTime = System.currentTimeMillis();

		
			GridValueLayer solute = (GridValueLayer)getValueLayer(soluteName);

			double sum = 0;
			int width = (int) solute.getDimensions().getWidth();
			int height = (int) solute.getDimensions().getHeight();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					sum += solute.get(x,y);
				}
			}

			System.out.println("diffusion, " + soluteName + ":" + "    total amount = " + sum);
			System.out.println(endTime - startTime);
			if (repeats > 0) {
				System.out.println("diffusion, " + soluteName + "perStep:");
				System.out.println((endTime - startTime)/repeats);
			}

			
			
		}
	}



	// priority = -1 so that the heatbugs action occurs first
	@ScheduledMethod(start = 1, interval = 1, priority = -5)
	public void react() {
		for (int i = 0; i < 1; i++) { 

			for (Entry<String, Reaction> entry : reactionMap.entrySet()) {

				long startTime = System.currentTimeMillis();

				String key = entry.getKey();
				Reaction value = entry.getValue();
				value.react(1.0);
				long endTime = System.currentTimeMillis();

				System.out.println("reaction, " + key + ":");
				System.out.println(endTime - startTime);

			}
		}
	}

	@ScheduledMethod ( start = 1, interval = 1, priority = 1)
	public void Shove() {
		long startTime = System.currentTimeMillis();
		IndexedIterable<PhysicalAgent> allAgents = getObjects(Object.class);
		for (int i = 0; i < 50; i++) {
			for (PhysicalAgent b : allAgents) {
				b.calculateForces();
			}
			for (PhysicalAgent b : allAgents) {
				b.followForces(1);			
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("shoving:");
		System.out.println(endTime - startTime);
	}
	
	@ScheduledMethod ( start = 1, interval = 1, priority = 4)
	public void updtaeWaterLevel() {
		double t = schedule.getTickCount();
		waterscape.updateWaterLevel(this, t);
	}
	
	@ScheduledMethod ( start = 1, interval = 1, priority = -1)
	public void checkEnd() {
		double t = schedule.getTickCount();
		double time = t * stepTime;
		if (time > endTime) {
			RunEnvironment.getInstance().endRun();
		}
	}

	public Zoo getZoo() {
		return zoo;
	}
}
