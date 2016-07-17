package leaf03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leaf03.utils.ParserUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Zoo {
	
	private Context<PhysicalAgent> context;
	private Map<String, Map<String, Boolean>> speciesReactionNamesMap = new HashMap<String, Map<String, Boolean>>();
	private Map<String, Map<String, String>> speciesDefaultProperties = new HashMap<String, Map<String, String>>();
	private ContinuousSpace<PhysicalAgent> space;
	private Grid<PhysicalAgent> grid;

	public Zoo(Leaf03Context context, ContinuousSpace<PhysicalAgent> space, Grid<PhysicalAgent> grid) {
		this.context = context;
		this.space = space;
		this.grid = grid;
		
	}

	public void init(Node agentsNode) {
		List <Node> agentNodes = ParserUtils.getNodesByTagName(agentsNode, "agent");
		for (Node agentNode : agentNodes) {
			initAgentSpecies(agentNode);
		}
	}
	
	public void initAgentSpecies(Node agentNode) {

		String agentClassName = ParserUtils.getStringByName(agentNode, "class");
		speciesReactionNamesMap.put(agentClassName, new HashMap<String, Boolean>());
		speciesDefaultProperties.put(agentClassName, new HashMap<String, String>());


		Node reactionNamesNode = ParserUtils.getNodeByTagName(agentNode, "reactionNames");

		if (reactionNamesNode != null) {
			NodeList nodeList = reactionNamesNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				switch (nodeList.item(i).getNodeType()) {
				case Node.ELEMENT_NODE:
					Element element = (Element) nodeList.item(i);
					String reactionName = element.getAttribute("name");
					Boolean active = Integer.parseInt(nodeList.item(i).getTextContent()) != 0;
					speciesReactionNamesMap.get(agentClassName).put(reactionName, active);
				}
			}
		} 

		Node defaultPropertiesNode = ParserUtils.getNodeByTagName(agentNode, "properties");

		if (defaultPropertiesNode != null) {
			NodeList nodeList = defaultPropertiesNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				switch (nodeList.item(i).getNodeType()) {
				case Node.ELEMENT_NODE:
					Element element = (Element) nodeList.item(i);
					String propertyName = element.getAttribute("name");
					String propertyValue = nodeList.item(i).getTextContent();
					speciesDefaultProperties.get(agentClassName).put(propertyName, propertyValue);
				}
			}
		}
	}
	
	public PhysicalAgent createAgent(String speciesClassName) {
		PhysicalAgent agent = null;

		if (speciesClassName.equalsIgnoreCase("PhysicalAgent")) {
			agent = new PhysicalAgent();
		}
		if (speciesClassName.equalsIgnoreCase("Bacteria")) {
			agent = new Bacteria();
		}
		if (speciesClassName.equalsIgnoreCase("EmittingBacteria")) {
			agent = new EmittingBacteria();
		}
		
		agent.grid = this.grid;
		agent.space = this.space;

		agent.setReactionNamesMap(speciesReactionNamesMap.get(speciesClassName));

		agent.init();
		return agent;
		
	}



}
