import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

/*
 * Das zugrundeliegende Modell und die GUI sind strikt voneinander getrennt. So kann das Modell bzw. die GUI
 * leicht  ver�ndert und verschiedene GUIs auf das Modell angewandt werden.
 */

// Die Klasse SimulationWithUi muss eine Subklasse von GUIState sein.
public class SimulationWithUI extends GUIState {

	/*
	 * Display2D display ist in der Lage definierte "Felder" der Simulation
	 * (auch meherere �bereinander) anzuzeigen. Das display wird in einem
	 * neuen Fenster, dem JFrame displayFrame, angezeigt. Das areaPortrayal ist
	 * f�r das (neu-)zeichnen des Displays und die Beobachtung der angezeigten
	 * Individuen verantwortlich (es gibt verschieden Typen von Portrayals; z.B.
	 * OvalPortrayal2D, das Kreise zeichnet).
	 */
	private Display2D display;
	private JFrame displayFrame;
	private SparseGridPortrayal2D areaPortrayal = new SparseGridPortrayal2D();

	/*
	 * 2 Konstruktoren: Default-Konstruktor, der aus der aktuellen Systemzeit
	 * eine zul�ssige Simulation erzeugt, und diese an den super-Konstruktor
	 * �bergibt (beim initialen Start der Simulation). Konstruktor mit
	 * SimState als Parameter, ruft den super-Konstruktor mit dem �bergebenen
	 * SimState auf (z.B. wenn Simulation geladen (load) wird).
	 */
	public SimulationWithUI() {
		super(new Simulation(System.currentTimeMillis()));
	}

	public SimulationWithUI(SimState state) {
		super(state);
	}

	// getName() gibt den Namen der aktuellen Simulation zur�ck.
	public static String getName() {
		return "Traffic monitor";
	}

	/*
	 * init() wird beim initialen Erstellen der GUI aufgerufen. Als erstes, muss
	 * immer die super-Methode aufgerufen werden. Erzeugt ein display der
	 * Gr��e 600 x 600 Pixel. setClipping(false) sorgt daf�r, dass das
	 * zugrundeliegende "Feld" mit seiner Gr��e 50 x 50 (in Klasse Config
	 * definiert) das Display nicht auf 50x50 begrenzt. createFrame() erzuegt
	 * ein Fenster, in dem das Display angezeigt wird. setTitle() setzt den
	 * Namen. registerFrame() registriert das Display bei der �bergebenen
	 * Konsole. setVisible() sorgt daf�r, dass der Display auch sichtbar
	 * angezeigt wird. attach() h�ngt die areaPortrayal dem Display an.
	 */
	public void init(Controller c) {
		super.init(c);


		display = new Display2D(10*Config.width, 10*Config.height, this);

		display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("Area Display");
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.attach(areaPortrayal, "Yard");
	}

	/*
	 * start() wird beim Dr�cken des Play-Buttons aufgerufen, ruft die
	 * super-Methode auf und anschlie�end die setupPortrayals() Methode.
	 */
	public void start() {
		super.start();
		setupPortrayals();
	}

	/*
	 * load() wird aufgerufen, wenn eine Simulation von einem Checkpoint aus
	 * geladen wird. Sie hat fast die gleiche funktionalit�t wie start().
	 */
	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	/*
	 * setupPortrayals sorgt daf�r, dass beim Starten der Simulation, diese
	 * tats�chlich visualisiert wird. hat 2 Aufgaben: 1. Das Feld bzw. die
	 * Felder des Modells sollen dargestellt werden. 2. Der Display soll
	 * geresettet und bereinigt werden.
	 */
	public void setupPortrayals() {
		/*
		 * Die Variable state wurde beim Aufruf der start() bzw. load() Methode
		 * initialisiert. Sie ist in unserem Fall eine Instanz der Simulation.
		 */

		Simulation simulation = (Simulation) state;

		/*
		 * Vorgehen: 1. dem Abbild (areaPortrayal) sagen, welches Feld der
		 * Simulation dargestellt werden soll (setField(simulation.getArea()) 2.
		 * dem Abbild sagen, dass alle Objekte (zun�chst f�r die Objekte,
		 * dann f�r die Ameisen) als Kreise (OvalPortrayal2D()) dargestellt
		 * werden sollen (hier: verschiedenartige Farben, je nachdem, ob das
		 * Objekt getragen wird). 3. das Display reseten, damit es auch nach
		 * jedem Step gerepaintet wird. 4. die Hintergrundfarbe des Display
		 * bestimmen. 5. das Display einmal repainten, um den Initialzustand vor
		 * dem Start der Simulation anzuzeigen.
		 */
		areaPortrayal.setField(simulation.getArea());

		areaPortrayal.setPortrayalForClass(Human.class, new RectanglePortrayal2D() {
			public void draw(final Object object, final Graphics2D graphics,
					final DrawInfo2D info) {
				if (((Human) object).getDirection()>0) {
					paint = new Color(255, 255, 51);
				} else if(((Human) object).getDirection()<0){
					paint = new Color(255, 153, 51);
				}
				else paint = new Color(255, 0, 0);
				super.draw(object, graphics, info);
			}
		});
		display.reset();
		display.setBackdrop(Color.WHITE);

		display.repaint();
	}

	/*
	 * quit() wird aufgerufen, wenn die GUI geschlossen wird. Sofern der
	 * displayFrame nicht NULL ist wird er geschlossen, und displayFrame und
	 * display werden auf NULL gesetzt.
	 */
	public void quit() {
		super.quit();
		if (displayFrame != null)
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	/*
	 * main hat folgende Aufgaben: 1. Objekt der Subklasse erzeugen 2. Eine
	 * Console erzeugen, die die Simulation starten/stoppen/pausieren etc. kann
	 * 3. Die Console sichtbar machen
	 */
	public static void main(String[] args) {
		SimulationWithUI vid = new SimulationWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}
}