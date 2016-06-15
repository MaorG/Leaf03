package leaf03;

import repast.simphony.space.continuous.WrapAroundBorders;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayerDiffuser;

public class ValueLayerDiffuserHeterogeneous extends ValueLayerDiffuser {
	private IGridValueLayer diffusionLayer;

	private transient WrapAroundBorders borders;

	public ValueLayerDiffuserHeterogeneous(IGridValueLayer valueLayer, double evaporationConst,
			IGridValueLayer diffusionLayer, boolean toroidal) {
		//super(valueLayer);
		this.evaporationConst = evaporationConst;
		this.diffusionConst = 0;
		this.toroidal = toroidal;

		setValueLayer(valueLayer);
		setDiffusionLayer(diffusionLayer);
	}

	public ValueLayerDiffuserHeterogeneous(IGridValueLayer valueLayer, double evaporationConst,
			double diffusionConst, boolean toroidal) {
		super(valueLayer, evaporationConst, diffusionConst, toroidal);
	}

	public void setDiffusionLayer(IGridValueLayer diffusionLayer) {
		// exceptions?

		this.diffusionLayer = diffusionLayer;

	}

	@Override
	protected void computeVals() {
		// this is being based on
		// http://www.mathcs.sjsu.edu/faculty/rucker/capow/santafe.html
		int size = valueLayer.getDimensions().size();

		if (size == 2) {
			int width = (int) valueLayer.getDimensions().getWidth();
			int height = (int) valueLayer.getDimensions().getHeight();
			double[][] newVals = new double[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// these are the neighbors that are directly north/south/east/west to
					// the given cell 4 times those that are diagonal to the cell

					double oldVal = getValue(x, y);


					double duE = getValue(x + 1, y) - oldVal;
					double duN = getValue(x, y + 1) - oldVal;
					double duW = getValue(x - 1, y) - oldVal;
					double duS = getValue(x, y - 1) - oldVal;

					// these are the neighbors that are diagonal to the given cell
					// they are only weighted 1/4 of the ones that are
					// north/south/east/west
					// of the cell
					double duNE = getValue(x + 1, y + 1) - oldVal;
					double duNW = getValue(x - 1, y + 1) - oldVal;
					double duSW = getValue(x - 1, y - 1) - oldVal;
					double duSE = getValue(x + 1, y - 1) - oldVal;

					double localDiff = getDiffValue(x, y);
					double diffE = 0.5 * (getDiffValue(x + 1, y) + localDiff);
					double diffN = 0.5 * (getDiffValue(x, y + 1) + localDiff);
					double diffW = 0.5 * (getDiffValue(x - 1, y) + localDiff);
					double diffS = 0.5 * (getDiffValue(x, y - 1) + localDiff);

					// these are the neighbors that are diagonal to the given cell
					// they are only weighted 1/4 of the ones that are
					// north/south/east/west
					// of the cell
					double diffNE = 0.5 * (getDiffValue(x + 1, y + 1) + localDiff);
					double diffNW = 0.5 * (getDiffValue(x - 1, y + 1) + localDiff);
					double diffSW = 0.5 * (getDiffValue(x - 1, y - 1) + localDiff);
					double diffSE = 0.5 * (getDiffValue(x + 1, y - 1) + localDiff);

					// compute the weighted avg, those directly north/south/east/west
					// are given 4 times the weight of those on a diagonal
					double weightedAvgDeltaDiff = ((
							duE * diffE + 
							duW * diffW + 
							duN * diffN +
							duS * diffS
							) * 4 
							+ (
									duNE * diffNE + 
									duNW * diffNW + 
									duSW * diffSW + 
									duSE * diffSE
									)) / 20.0;

					// apply the diffusion and evaporation constants
					double newVal = (oldVal + weightedAvgDeltaDiff) * evaporationConst;

					// bring the value into [min, max]
					newVals[x][y] = constrainByMinMax(newVal);

					// System.out.println("x: " + x + " y: " + y + "val: " + oldVal +
					// " delta: "
					// + delta + " d: " + newVals[x][y]);
				}
			}
			computedVals = newVals;
		}
	}


	@Override
	public void diffuse() {
		computeVals();
		int size = valueLayer.getDimensions().size();

		if (size == 1) {
			double[] newVals = (double[]) computedVals;
			for (int x = 0; x < newVals.length; x++) {
				valueLayer.set(newVals[x], x);
			}
		} else if (size == 2) {
			double[][] newVals = (double[][]) computedVals;
			for (int x = 0; x < newVals.length; x++) {
				for (int y = 0; y < newVals[0].length; y++) {
					valueLayer.set(newVals[x][y], x, y);
				}
			}
		} else {
			double[][][] newVals = (double[][][]) computedVals;
			for (int x = 0; x < newVals[0].length; x++) {
				for (int y = 0; y < newVals[0][0].length; y++) {
					for (int z = 0; z < newVals.length; z++) {
						valueLayer.set(newVals[x][y][z], x, y, z);
					}
				}
			}
		}
	}


	protected double getDiffValue(double... coords) {
		if (toroidal) {
			if (borders == null) {
				borders = new WrapAroundBorders();
				borders.init(valueLayer.getDimensions());
			}
			// use the wrap around borders class to set this up
			borders.transform(coords, coords);
		} else if (inBounds(coords) == 0.0) {
			return 0.0;
		}
		return diffusionLayer.get(coords);
	}

}
