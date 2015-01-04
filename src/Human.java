import java.util.Random;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

/*
 * Die Klasse Ant implementiert die Schnittstelle Steppable und besitzt eine step() Methode,
 * sodass jede Ant dem Schedule zugewiesen werden kann um steps zu machen.
 */
public class Human implements Steppable {
	private Random rand;
	private int waitsteps = 0;
	private int stayedSteps = 0;
	private Int2D position;
	private int direction;
	private int pause;
	public int getDirection() {
		return direction;
	}

	private int rule;
	public Human(Int2D pos, int direction, int rule, int pause) {
		rand = new Random();
		this.position = pos;
		this.direction = direction;
		this.rule = rule;
		if(pause < 0){
			System.out.println("Pause kleiner Null");
		}
		this.pause = pause;
	}

	@Override
	public void step(SimState state) {
		// SimState state muss in Simulation gecastet werden (was es ja auch ist, da Simulation eine Subklasse von SimState ist).
		Simulation simulation = (Simulation) state;
		SparseGrid2D area = simulation.getArea();
		
		
		int revertSpeed;
		if(waitsteps >= pause){
			waitsteps = 0;
			if(rule == 0){
				setPosition(rule0(area));
			}
			if(rule == 1){
				setPosition(rule1(area));
			}
			if(rule == 2){
				setPosition(rule2(area));
			}
		}
		else{
			waitsteps++;
		}

		// Neue Position wird auf der area gestzt
		area.setObjectLocation(this, this.position);
	}
	
	
	

	public Int2D rule2(SparseGrid2D area) {
		Int2D newPos = rule1(area);
		if(newPos == position && Config.staySteps != -1){
			if(stayedSteps >= Config.staySteps){
				stayedSteps = 0;
				return rule0(area);
			}
			else{
				stayedSteps++;
				return position;
			}
		}
		else
			stayedSteps = 0;
			return newPos;
	}

	public Int2D rule0(SparseGrid2D area) {
		//if(direction != 0){
		//	System.out.println("rule0 von einem nicht-Störer");
		//	return position;
		//}
		Int2D newPos;
		double stepDirection = rand.nextDouble();
		if(stepDirection < 0.25){
			newPos = new Int2D(position.x + 1, position.y);
		}
		else if(stepDirection < 0.5){
			newPos = new Int2D(position.x - 1, position.y);
		}
		else if(stepDirection < 0.75){
			newPos = new Int2D(position.x, position.y + 1);
		}
		else{
			newPos = new Int2D(position.x, position.y - 1);
		}
		if(getHumanAtPos(newPos, area) == 0)
			return newPos;
		else
			return position;
	}

	public Int2D rule1 (SparseGrid2D area){
		
		int nextFieldDiff;
		if(direction > 0)
			nextFieldDiff = 1;
		else if(direction < 0)
			nextFieldDiff = -1;
		else nextFieldDiff = 0;
		Int2D checkPos;
		
		if(nextFieldDiff == 0){
			System.out.println("rule1 von einem Störer aufgerufen");
			return position;
		}
		
		
		checkPos =new Int2D(position.x + nextFieldDiff, position.y);
		// next field with human in same direction --> stay
		if(getHumanAtPos(checkPos, area) == 1){
			return position;
		}
		// next field with human in different direction
		else if(getHumanAtPos(checkPos, area) == -1){
			checkPos = new Int2D(position.x, position.y + nextFieldDiff);
			//empty right field --> step right
			if(getHumanAtPos(checkPos, area) == 0){
				return checkPos;
			}
			//non-empty right field
			else
			{
				checkPos = new Int2D(position.x, position.y - nextFieldDiff);
				//empty left field --> step left
				if(getHumanAtPos(checkPos, area) == 0){
					return checkPos;
				}
				//non-empty left field: --> stay
				else
					return position;
			}
		}
		// empty next Field
		else{
			checkPos =new Int2D(position.x + 2 * nextFieldDiff, position.y);
			// human in same direction two fields ahead --> step forward
			if(getHumanAtPos(checkPos, area) == 1)
				return new Int2D(position.x + nextFieldDiff, position.y);
			// empty field or human in different direction two fields ahead 
			else{
				checkPos = new Int2D(position.x, nextFieldDiff + position.y);
				//right field is empty
				if(getHumanAtPos(checkPos, area) == 0){
					checkPos = new Int2D(position.x + nextFieldDiff, nextFieldDiff + position.y);
					// human with same direction right ahead
					if(getHumanAtPos(checkPos, area) == 1)
						return new Int2D(position.x, position.y + nextFieldDiff);
					// human different direction right ahead
					else if(getHumanAtPos(checkPos, area) == -1){
						return checkToGoLeft(area, nextFieldDiff);
					}
					//empty field right ahead
					else{
						checkPos = new Int2D(position.x + 2 * nextFieldDiff, nextFieldDiff + position.y);
						//human in same direction on field right, two ahead
						if(getHumanAtPos(checkPos, area) == 1)
							return new Int2D(position.x, position.y + nextFieldDiff);
						//empty field or human in different direction on field right, two ahead
						else
							return checkToGoLeft(area, nextFieldDiff);
					}
				}
				//right field is non-empty
				else{
					return checkToGoLeft(area, nextFieldDiff);
					
						
					//left field is non-empty
				}
			}
		}
	}
	
	
	
	private Int2D checkToGoLeft(SparseGrid2D area, int nextFieldDiff) {
		Int2D checkPos;
		checkPos = new Int2D(position.x, position.y - nextFieldDiff);
		//left field is empty
		if(getHumanAtPos(checkPos, area) == 0){
			
			checkPos = new Int2D(position.x + nextFieldDiff, position.y - nextFieldDiff);
			// human with same direction on field left ahead
			if(getHumanAtPos(checkPos, area) == 1){
				return new Int2D(position.x, position.y - nextFieldDiff);
			}
			//human with different direction on field left ahead
			else if(getHumanAtPos(checkPos, area) == -1){
				return stepAhead(nextFieldDiff);
			}
			//empty field left ahead
			else{
				checkPos = new Int2D(position.x + nextFieldDiff, position.y - 2 * nextFieldDiff);
				// human with same direction on field left, two ahead
				if(getHumanAtPos(checkPos, area) == 1){
					return stepLeft(nextFieldDiff);
				}
				//empty field or human with different direction on field left, two ahead
				else{
					return stepAhead(nextFieldDiff);
				}
			}
		}
		else 
			return new Int2D(position.x + nextFieldDiff, position.y); 

		
	}
	

	public Int2D stepLeft(int nextFieldDiff){
		setPosition(new Int2D(position.x, position.y - nextFieldDiff));
		return position;
	}
	

	public Int2D stepRight(int nextFieldDiff){
		setPosition(new Int2D(position.x, position.y + nextFieldDiff));
		return position;
	}
	
	public Int2D stepAhead(int nextFieldDiff){
		setPosition(new Int2D(position.x + nextFieldDiff, position.y));
		return position;
	}
	
	public int getHumanAtPos(Int2D pos, SparseGrid2D area){
		// return Values:
		// -1: Human in other direction at pos
		// 0: no Human at pos
		// 1: Human in same direction at pos
		Bag bag = area.getObjectsAtLocation(calculatePosition(pos));
		if (pos.y<0 || pos.y>=Config.height)
			return -1;
		else if(bag == null)
			return 0;
		else if(bag.numObjs > 1){
			System.out.println("Zwei Objekete an der selben Stelle!");
			return 0;}
		else if(((Human) bag.get(0)).direction == this.direction)
			return 1;
		else
			return -1;
	}
	
	
	public int getRule(){
		return rule;
	}
	
	public Entropy getEntropy() {
		return Entropy.getInstance();
	}


	public Int2D getPosition() {
		return this.position;
	}
	
	private Int2D calculatePosition(Int2D position){
		
		if(position.getX()>Config.width){
			
			return new Int2D(position.getX()%Config.width, position.y);
		
		}
		else if(position.getX()<0){
			return new Int2D(position.getX()+Config.width, position.y);
		}
		else {return position;}
	}
	
	public void setPosition(Int2D position) {
		this.position = calculatePosition(position);
	}

}