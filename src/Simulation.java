import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class Simulation extends SimState {

	// area ist das Feld, auf dem die Agenten agieren sollen.
	private final SparseGrid2D area = new SparseGrid2D(Config.width,
			Config.height);

	// seed ist eine Zufallszahl.
	public Simulation(long seed) {
		super(seed);
	}
	
	
	// Durch die Methode start() wird das Feld mit den Ameisen und Gegenständen initialisiert
	public void start() {
		super.start();
		// Das Feld, auf dem die Agenten initialisiert werden, wird geleert.
		area.clear();

		// Eine Instanz der Entropy-Klasse wird erzeugt und dem Schedule hinzugefügt.
		// Der Schedule sorgt dafür, dass die step()-Methode des Entropy-Objekts jede 1.0te Zeiteinheit auf-
		// gerufen wird (genau wie es später auch mit jeder Ameise erfolgt).
		Entropy entropy = Entropy.getInstance();
		schedule.scheduleRepeating(entropy, 101);

		// Es werden neue Ameisen erzeugt, denen eine Position übergeben wird. Diese Ameisen werden 
		// auf dem Feld verteilt und dem Schedule zugeteilt.
		Int2D position = new Int2D(area.getWidth() / 2, area.getHeight() / 2);
		for (int i = 0; i < Config.numAnts; i++) {
			Ant ant = new Ant(position);
			area.setObjectLocation(ant, position);
			// scheduleRepeating(ant) sorgt dafür, dass jeder Agent jede 1.0te Zeiteinheit (1.0, 2.0, 3.0 ...)
			// einen Schritt macht.
			// Alle Agenten machen dies gleichzeitig und in zufälliger Reihenfolge.
			// step() ist in Ants deklariert.
			schedule.scheduleRepeating(ant);
		}

		for (int i = 0; i < Config.numObstacles; i++) {
			position = null;
			do {
				// Zufällig Position wird generiert.
				position = new Int2D(
						(int) (this.area.getWidth() * random.nextDouble()),
						(int) (this.area.getHeight() * random.nextDouble()));
				Bag bag = this.area.getObjectsAtLocation(position.x, position.y);
				//Falls an dieser Position schon ein Objekt ist, wird Position wieder auf Null gesetzt und 
				// es wird eine neue Position berechnet, bis eine freie Position gefunden wurde.
				if (bag != null && !bag.isEmpty())
					position = null;
			} while (position == null);
			// Das Objekt wird an der generierten Position erzeugt.
			Obstacle obst = new Obstacle(position, area);
		}
		entropy.step(this);
	}

	public static void main(String[] args) {
		/*
		 * Bei Aufruf der main Methode wird die doLoop Methode aufgerufen. -> Die Simulation wird gestartet.
		 * Simulationsschritte:
		 * 1. Erzeugen einer Instanz der SimState Klasse, der eine Zufallszahl übergeben wird.
		 * 2. Die start Methode wird aufgerufen, in der die Simulation initialisiert wird.
		 * 3. Die step Methode wird immer wieder aufgerufen was dafür sorgt, dass die Agenten (wie im Schedule/Ablaufplan der Simulation vorgegeben) 
		 * 		einen Schritt/Step tätigen.
		 * 4. Wenn der Schedule keine Agenten mehr enthält / oder nach N Schritten wird die Simulation beendet.
		 */
		doLoop(Simulation.class, args);
		System.exit(0);
	}

	public SparseGrid2D getArea() {
		return area;
	}
}