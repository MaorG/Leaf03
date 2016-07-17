package leaf03;

import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayerDiffuser;

public abstract class CustumValueLayerDiffuser extends ValueLayerDiffuser {
	
	protected double dx;
	protected double dt;
	
	public CustumValueLayerDiffuser(IGridValueLayer valueLayer, double evaporationConst,
			double diffusionConst, boolean toroidal) {
		super(valueLayer, evaporationConst, diffusionConst, toroidal);
	}
	
	public CustumValueLayerDiffuser() {
		super();
	}
	
	public void setScale(double scale) {
		dx = scale;
	}
	
	abstract public double determineMaxStableDt();


}
