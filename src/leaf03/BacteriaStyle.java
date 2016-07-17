package leaf03;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;

public class BacteriaStyle implements StyleOGL2D<Bacteria> {

	private Font font1, font2;
	private ShapeFactory2D shapeFactory;

	public BacteriaStyle() {
		font1 = new JLabel().getFont();
		font2 = new Font("SansSerif", Font.BOLD, 20);
	}

	public void init(ShapeFactory2D shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getBorderColor(java.lang
	 * .Object)
	 */
	public Color getBorderColor(Bacteria object) {
//		return Color.black;
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getBorderSize(java.lang
	 * .Object)
	 */
	public int getBorderSize(Bacteria object) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getColor(java.lang.Object)
	 */
	public Color getColor(Bacteria object) {
		return Color.green;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getRotation(java.lang.Object
	 * )
	 */
	public float getRotation(Bacteria object) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getScale(java.lang.Object)
	 */
	public float getScale(Bacteria object) {
		return 2.0f * (float)((Bacteria)object).radius;
//		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getVSpatial(java.lang.Object
	 * , saf.v3d.scene.VSpatial)
	 */
	public VSpatial getVSpatial(Bacteria object, VSpatial spatial) {
		if (spatial == null) {
			VSpatial vs = shapeFactory.createCircle((float)((Bacteria)object).radius, 16); 
			return vs;
		}
		return spatial;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getLabel(java.lang.Object)
	 */
	public String getLabel(Bacteria object) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getLabelColor(java.lang
	 * .Object)
	 */
	public Color getLabelColor(Bacteria object) {
		return Color.WHITE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getLabelFont(java.lang.
	 * Object)
	 */
	public Font getLabelFont(Bacteria object) {
		if (Math.random() < .5) {
			return font1;
		} else {
			return font2;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getLabelPosition(java.lang
	 * .Object)
	 */
	public Position getLabelPosition(Bacteria object) {
		return Position.SOUTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getLabelXOffset(java.lang
	 * .Object)
	 */
	public float getLabelXOffset(Bacteria object) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.visualizationOGL2D.StyleOGL2D#getLabelYOffset(java.lang
	 * .Object)
	 */
	public float getLabelYOffset(Bacteria object) {
		// TODO Auto-generated method stub
		return 0;
	}
}


