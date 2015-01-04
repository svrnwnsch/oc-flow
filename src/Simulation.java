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

	// Durch die Methode start() wird das Feld mit den Ameisen und
	// Gegenst�nden initialisiert
	public void start() {
		super.start();
		// Das Feld, auf dem die Agenten initialisiert werden, wird geleert.
		area.clear();

		// Eine Instanz der Entropy-Klasse wird erzeugt und dem Schedule
		// hinzugef�gt.
		// Der Schedule sorgt daf�r, dass die step()-Methode des
		// Entropy-Objekts jede 1.0te Zeiteinheit auf-
		// gerufen wird (genau wie es sp�ter auch mit jeder Ameise erfolgt).
		Entropy entropy = Entropy.getInstance();
		schedule.scheduleRepeating(entropy, 101);

		// Es werden neue Ameisen erzeugt, denen eine Position �bergeben wird.
		// Diese Ameisen werden
		// auf dem Feld verteilt und dem Schedule zugeteilt.

		for (int i = 0; i < Config.numRight; i++) {
			Int2D position = null;
			Human hum;

			do {
				// Zufaellig Position wird generiert.
				position = new Int2D(
						(int) (this.area.getWidth() * random.nextDouble()),
						(int) (this.area.getHeight() * random.nextDouble()));
				Bag bag = this.area
						.getObjectsAtLocation(position.x, position.y);
				// Falls an dieser Position schon ein Objekt ist, wird Position
				// wieder auf Null gesetzt und
				// es wird eine neue Position berechnet, bis eine freie Position
				// gefunden wurde.
				if (bag != null && !bag.isEmpty())
					position = null;
			} while (position == null);
			// Das Objekt wird an der generierten Position erzeugt.

			hum = new Human(position, 1, 1, 0);

			area.setObjectLocation(hum, position);
			schedule.scheduleRepeating(hum);

		}
		for (int i = 0; i < Config.numLeft; i++) {
			Int2D position = null;
			Human hum;

			do {
				// Zufaellig Position wird generiert.
				position = new Int2D(
						(int) (this.area.getWidth() * random.nextDouble()),
						(int) (this.area.getHeight() * random.nextDouble()));
				Bag bag = this.area
						.getObjectsAtLocation(position.x, position.y);
				// Falls an dieser Position schon ein Objekt ist, wird Position
				// wieder auf Null gesetzt und
				// es wird eine neue Position berechnet, bis eine freie Position
				// gefunden wurde.
				if (bag != null && !bag.isEmpty())
					position = null;
			} while (position == null);
			// Das Objekt wird an der generierten Position erzeugt.

			hum = new Human(position, -1, 1, 0);

			area.setObjectLocation(hum, position);
			schedule.scheduleRepeating(hum);

		}
		for (int i = 0; i < Config.randomWalk; i++) {
			Int2D position = null;
			Human hum;

			do {
				// Zufaellig Position wird generiert.
				position = new Int2D(
						(int) (this.area.getWidth() * random.nextDouble()),
						(int) (this.area.getHeight() * random.nextDouble()));
				Bag bag = this.area
						.getObjectsAtLocation(position.x, position.y);
				// Falls an dieser Position schon ein Objekt ist, wird Position
				// wieder auf Null gesetzt und
				// es wird eine neue Position berechnet, bis eine freie Position
				// gefunden wurde.
				if (bag != null && !bag.isEmpty())
					position = null;
			} while (position == null);
			// Das Objekt wird an der generierten Position erzeugt.

			hum = new Human(position, 0, 0, 0);

			area.setObjectLocation(hum, position);
			schedule.scheduleRepeating(hum);

		}
		entropy.step(this);
	}

	public static void main(String[] args) {
		/*
		 * Bei Aufruf der main Methode wird die doLoop Methode aufgerufen. ->
		 * Die Simulation wird gestartet. Simulationsschritte: 1. Erzeugen einer
		 * Instanz der SimState Klasse, der eine Zufallszahl �bergeben wird. 2.
		 * Die start Methode wird aufgerufen, in der die Simulation
		 * initialisiert wird. 3. Die step Methode wird immer wieder aufgerufen
		 * was daf�r sorgt, dass die Agenten (wie im Schedule/Ablaufplan der
		 * Simulation vorgegeben) einen Schritt/Step t�tigen. 4. Wenn der
		 * Schedule keine Agenten mehr enth�lt / oder nach N Schritten wird die
		 * Simulation beendet.
		 */
		doLoop(Simulation.class, args);
		System.exit(0);
	}

	public SparseGrid2D getArea() {
		return area;
	}
}