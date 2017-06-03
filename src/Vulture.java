import bwapi.*;

import java.util.ArrayList;
import java.util.HashSet;

public class Vulture {

    private final Mirror bwapi;
    private final HashSet<Unit> enemyUnits;
    final private Unit unit;
    //general 
    private int count = 0;
    private int action = 0;
    private int range = 32*5-5; //32 pixel times 5 range
    private ArrayList<Unit> eUnits = new ArrayList<Unit>();
    
    //for reward
    private Environment oldEnv = null;
    
    //for action - exploration
    private Position explStartPoint = null;
    private ArrayList<Position> lastKnownEnemyPos = new ArrayList<Position>();
    
    
    public Vulture(Unit unit, Mirror bwapi, HashSet<Unit> enemyUnits) 
    {
        this.unit = unit;
        this.bwapi = bwapi;
        this.enemyUnits = enemyUnits;
    }

    public void step() 
    {
    	Environment env = new Environment();
    	count++;
    	if (count == 6) 
    	{
    		env = genEnvironment();
			action = VultureAI.VultXCS.process(env, getTime()); //run XCS
			addDiscoveredEnemies();
			
    	}
    	bwapi.getGame().setScreenPosition(new Position(unit.getPosition().getX()-290,unit.getPosition().getY()-200));
		rememberEnemyPositions();
    	doAction(action);
    	
    	if (count == 10)	
		{
    		env = genEnvironment(); 

			int reward = calcReward(env,action);
			VultureAI.VultXCS.profit(reward,getTime()); //get reward

			count = 0;
		}	
    }
    
    private Environment genEnvironment()
    {
    	Unit target = getClosestEnemy();
		int HP = unit.getHitPoints();
		int enemyHP = 0;
		double distance = 0;
		
		if (target != null)
		{
			enemyHP = target.getHitPoints();
			distance = getDistance(target);
		}
    	return new Environment(HP,enemyHP,distance);
    }
    
    private void addDiscoveredEnemies() {
    	eUnits = (ArrayList<Unit>) unit.getUnitsInRadius(range+120);
	}

	private void rememberEnemyPositions() //run back to last known positions 
    { 
		for (Unit u : eUnits)
		{
			if (u.isVisible())
			{
				lastKnownEnemyPos.add(u.getPosition());
			}
		}
	}

	private void doAction(int action)
    {
    	Unit target;
    	switch (action)
    	{
    		case 0:		//flee
    			//System.out.println("Flees");
    			target = getClosestEnemy();
    			flee(target);
    			explStartPoint = null;
    			break;
    		case 1:		//fight
    			//System.out.println("Fights");
    			target = getClosestEnemy();
    			attack(target);
    			explStartPoint = null;
    			break;
    		case 2: 	//explore
    			if (enemyIsVisible()) return;
    			
    			if (explStartPoint != null)
    			{
    				if(lastKnownEnemyPos.size() == 0)
    				{
	    				
	    				int time = bwapi.getGame().elapsedTime();
	    				int Xoffset = 0;
	    				int Yoffset = 0;
	    				
	    				Position nextPoint = null;
	    				Xoffset = (int) Math.round(15.0*time/30*Math.cos((double) (time+1)/8));
	    				Yoffset = (int) Math.round(15.0*time/30*Math.sin((double) (time+1)/8));
						nextPoint = new Position(unit.getPosition().getX() + Xoffset,unit.getPosition().getY() + Yoffset);
	    				if (enemyIsVisible()) 
						{
	    					action = 1;	
	    					return;
    					}
	    				
	    				move(nextPoint);
    				}
    				else
    				{
    					move(new Position(lastKnownEnemyPos.get(0).getPoint().getX()+ range,lastKnownEnemyPos.get(0).getPoint().getY()+ range));
    					lastKnownEnemyPos.remove(0);
    				}
    			}
    			else explStartPoint = unit.getPosition();
    			
    			break;
    	}
    }
    
    private boolean enemyIsVisible() {
		for (Unit u :eUnits) 
			if (u.isVisible()) return true;

		return false;
	}

	private int calcReward(Environment currentEnv, int currentAction) //oldEnv = means few frames old
    {
    	int reward = 400;
    	if (eUnits.size() == 0)
    	{
    		reward = 500;
    	}
    	else
    	{
	    	//X HP - Y enemyHP - Z elapsedTime
    		if (oldEnv != null)
    		{
		    	if(oldEnv.X == currentEnv.X) reward = reward + 1; //reward for not loosing health
		    	//if(oldEnv.Y > currentEnv.Y) reward = reward + 500; //reward for damaging the enemy
		    	if(oldEnv.X > currentEnv.X && oldEnv.Y == currentEnv.Y) reward = 0; //punishment for loosing health without doing dmg
		    	if(oldEnv.X == currentEnv.X && oldEnv.Y > currentEnv.Y && currentEnv.Z >= range - 20) reward = reward + 1000; //reward for not receiving dmg and doing dmg and holding distance
		    	if(oldEnv.X == currentEnv.X && oldEnv.Y > currentEnv.Y && currentEnv.Z < range - 20) reward = 0; //punishment for not holding distance
    		}
    		
	    	oldEnv = currentEnv;

    	}
    	if (currentAction == 2 && enemyIsVisible()) reward = 0; //punishment for exploring while enemies are near
    	if (currentAction == 0 && !enemyIsVisible()) reward = 0; //punishment for fighting without enemy
    	if (currentAction == 1 && !enemyIsVisible()) reward = 0; //punishment for fleeing without enemy
    	System.out.println("Reward:" + reward);
    	return reward;
    }

    private void flee(Unit target)
    {
    	if(target == null) return;
    	Position ownPos = unit.getPosition();
    	Position enemyPos = target.getPosition();
    	
    	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() + range, target.getPosition().getY() + range), false);
    	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() - range, target.getPosition().getY() + range), false);
    	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() + range, target.getPosition().getY() - range), false);
    	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() - range, target.getPosition().getY() - range), false);
    	
    }
    
    private void move(Position p) 
    {
        unit.move(p, false);
    }
    
    private void moveQueued(Position p) 
    {
        unit.move(p, false);
    }
    
    private void attack(Unit target)
    {
    	if (target!=null) 
    	{
        	Position ownPos = unit.getPosition();
        	Position enemyPos = target.getPosition();
        	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() + range, target.getPosition().getY() + range), false);
        	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() - range, target.getPosition().getY() + range), false);
        	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() + range, target.getPosition().getY() - range), false);
        	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() - range, target.getPosition().getY() - range), false);
        	
    		unit.attack(target,false);
    	}
    }
    
    private int getTime()
    {
    	return bwapi.getGame().elapsedTime();
    }

    private Unit getClosestEnemy() {
        Unit result = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Unit enemy : eUnits) {
            double distance = getDistance(enemy);
            if (distance < minDistance) {
                minDistance = distance;
                result = enemy;
            }
        }

        return result;
    }

    private double getDistance(Unit enemy) {
    	return this.unit.getPosition().getDistance(enemy.getPosition());
    }
}
