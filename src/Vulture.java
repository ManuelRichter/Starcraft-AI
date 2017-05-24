import bwapi.*;

import java.util.ArrayList;
import java.util.HashSet;

public class Vulture {

    private final Mirror bwapi;
    private final HashSet<Unit> enemyUnits;
    final private Unit unit;
    //new 
    private int count = 0;
    private int action = 0;
    //for reward
    private Environment oldEnv = null;
    
    //for action - exploration
    private Position explStartPoint = null;
    private ArrayList<Position> lastKnownEnemyPos = new ArrayList<Position>();
    
    public Vulture(Unit unit, Mirror bwapi, HashSet<Unit> enemyUnits) {
        this.unit = unit;
        this.bwapi = bwapi;
        this.enemyUnits = enemyUnits;
    }

    public void step() 
    {
        /**
         * TODO: XCS
         */
    	Environment env = new Environment();
    	count++;
    	if (count == 4) 
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
    		env = new Environment(HP,enemyHP,distance);
			action = VultureAI.VultXCS.process(env); //run XCS
    	}
    	
    	doAction(action);
    	if (count == 7)	
		{
    		rememberEnemyPositions();
    		Unit target = getClosestEnemy();
    		if (target != null) //dont learn if target died 
    		{
    			int enemyHP = target.getHitPoints();
    			int HP = unit.getHitPoints();
    			int reward = calcReward(new Environment(HP,enemyHP,getDistance(target)),action);
    			VultureAI.VultXCS.profit(reward); //get reward
    		}
			count = 0;
		}	
    }
    
    private void rememberEnemyPositions() //run back to last known positions 
    { 
		for (Unit u : enemyUnits)
		{
			if (u.isVisible())
			{
				lastKnownEnemyPos.add(u.getPosition());
			}
		}
	}

	public void doAction(int action)
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
    			if (getClosestEnemy() != null) return;
    			
    			if (explStartPoint != null)
    			{
    				if(lastKnownEnemyPos.size() == 0)
    				{
	    				
	    				int time = bwapi.getGame().elapsedTime();
	    				int Xoffset = 0;
	    				int Yoffset = 0;
	    				
	    				Position nextPoint = null;
	    				Xoffset = (int) Math.round(20.0*time/30*Math.cos((double) (time+1)/8));
	    				Yoffset = (int) Math.round(20.0*time/30*Math.sin((double) (time+1)/8));
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
    					move(lastKnownEnemyPos.get(0));
    					lastKnownEnemyPos.remove(0);
    				}
    			}
    			else explStartPoint = unit.getPosition();
    			
    			break;
    	
    	}
    }
    
    private boolean enemyIsVisible() {
		for (Unit u :enemyUnits) 
			if (u.isVisible()) return true;
		
		return false;
	}

	public int calcReward(Environment currentEnv, int currentAction) //oldEnv = means few frames old
    {
    	int reward = 0;
    	if (getClosestEnemy() == null)
    	{
    		reward = 10;
    	}
    	else
    	{
	    	//X HP - Y enemyHP - Z elapsedTime
    		if (oldEnv != null)
    		{
		    	if(oldEnv.X == currentEnv.X) reward = reward + 1; //reward for not loosing health
		    	if(oldEnv.Y > currentEnv.Y) reward = reward + 500; //reward for damaging the enemy
		    	if(oldEnv.X > currentEnv.X && oldEnv.Y == currentEnv.Y) reward = 0; //punishment for loosing health without doing dmg
		    	if(oldEnv.X == currentEnv.X && oldEnv.Y > currentEnv.Y && currentEnv.Z > 10) reward = reward + 1000; //reward for not receiving dmg and doing dmg and holding distance
		    	if(oldEnv.X == currentEnv.X && oldEnv.Y > currentEnv.Y && currentEnv.Z < 10) reward = 0; //punishment for not holding distance
    		}
    		
	    	oldEnv = currentEnv;
	    	System.out.println("Reward:" + reward);
    	}
    	if (currentAction == 2 && getClosestEnemy() != null) reward = 0; //punishment for exploring while enemies are near
    	return reward;
    }

    public void flee(Unit target)
    {
    	if(target == null) return;
    	Position ownPos = unit.getPosition();
    	Position enemyPos = target.getPosition();
    	
    	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() + 60, target.getPosition().getY() + 60), false);
    	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() - 60, target.getPosition().getY() + 60), false);
    	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() + 60, target.getPosition().getY() - 60), false);
    	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() - 60, target.getPosition().getY() - 60), false);
    	
    }
    
    private void move(Position p) 
    {
        unit.move(p, false);
    }
    
    private void attack(Unit target)
    {
    	if (target!=null) 
    	{
        	Position ownPos = unit.getPosition();
        	Position enemyPos = target.getPosition();
        	
        	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() + 60, target.getPosition().getY() + 60), false);
        	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() < ownPos.getY()) unit.move(new Position(target.getPosition().getX() - 60, target.getPosition().getY() + 60), false);
        	if (enemyPos.getX() < ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() + 60, target.getPosition().getY() - 60), false);
        	if (enemyPos.getX() > ownPos.getX() && enemyPos.getY() > ownPos.getY()) unit.move(new Position(target.getPosition().getX() - 60, target.getPosition().getY() - 60), false);
        	
    		unit.attack(target);
    	}
    }
    
    private int getTime()
    {
    	return bwapi.getGame().elapsedTime();
    }

    private Unit getClosestEnemy() {
        Unit result = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Unit enemy : enemyUnits) {
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
