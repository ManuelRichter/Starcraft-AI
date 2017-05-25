
public class Environment 
{
	public double X; //own HP
	public double Y; //enemy HP
	public double Z; //time
	
	public Environment()
	{
		
	}
	
	public Environment(int ownHP,int enemyHP,double distance)
	{
		X = ownHP;
		Y = enemyHP;
		Z = distance;
	}
}
