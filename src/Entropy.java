import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

/*
 * Implementiert die Schnittstelle Steppable und ben�tigt deshalb auch die Methode step(), die hier
 * daf�r sorgt, dass immer wieder die Methode calculateEntropy(state) aufgerufen wird.
 */
public class Entropy implements Steppable {

	private double value = 0.0;

	private static Entropy unique;
	private static double startvalue;

	private Entropy() {
	}

	public static Entropy getInstance() {
		if (unique == null)
			unique = new Entropy();
		return unique;
	}

	@Override
	public void step(SimState state) {
		if(state.schedule.getSteps()==0){
			calculateEntropy(state);
			startvalue = this.getValue();
		}
		calculateEntropy(state);
		System.out.println("step: "+state.schedule.getSteps()+"\t"+"Entropie: "+this.getValue() + " Emergenz: "+
				(startvalue - this.getValue()));
	}

	private void calculateEntropy(SimState state) {
		value = 0.0;
		Simulation sim = (Simulation) state;
		SparseGrid2D grid = sim.getArea();
		int count = 0;

		Bag bag = sim.getArea().getAllObjects();
		if (bag != null && !bag.isEmpty()) {
			// nach rechts laufende Menschen
			for (int y = 0; y < grid.getHeight(); y += 1) {
				for (Object object : bag) {
					if (object instanceof Human
							&& ((Human) object).getDirection()>0
							&& ((Human) object).getPosition().y - y < Config.height
							&& ((Human) object).getPosition().y - y >= 0) {
						count++;
					}
				}
				if (count != 0)
					value += (double) count
							/ (double) Config.numTotalRight
							* Math.log((double) count
									/ (double) Config.numTotalRight);
				count = 0;
			}
			// nach links laufende Menschen
			for (int y = 0; y < grid.getHeight(); y += 1) {
				for (Object object : bag) {
					if (object instanceof Human
							&& ((Human) object).getDirection()<0
							&& ((Human) object).getPosition().y - y < Config.height
							&& ((Human) object).getPosition().y - y >= 0) {
						count++;
					}
				}
				if (count != 0)
					value += (double) count
							/ (double) Config.numTotalLeft
							* Math.log((double) count
									/ (double) Config.numTotalLeft);
				count = 0;
			}
		}
	}

	public double getValue() {
		return value * -1;
	}
}
