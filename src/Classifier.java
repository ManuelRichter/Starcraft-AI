import com.google.common.collect.Range;

public class Classifier {

	public Condition C = new Condition(); //max general condition
	public int A = 0; //action
	public double p = 0.0f; //reward
	public double e = 0.0f; ; //error
	public double F = 0.0f; //fitness
	public int exp = 0; //experience
	public int ts = 0; //time stamp
	public double as = 1; // action set size estimate
	public int n = 1;  //numerosity
	
	public double kapa = 0.0f;
	
	public Classifier()
	{		
		
	}	
	
	public boolean doesMatch(Environment env) 
	{	
		if(C.X.contains(env.X) && C.Y.contains(env.Y) && C.Z.contains(env.Z)) return true; 
		
		return false;
	}

	public double countWC() //the bigger volume means more general
	{
		return (C.X.upperEndpoint() - C.X.lowerEndpoint()) * (C.Y.upperEndpoint() - C.Y.lowerEndpoint()) * (C.Z.upperEndpoint() - C.Z.lowerEndpoint());

	}

	public boolean moreGeneral(Classifier cl) 
	{
		if (this.countWC() <= cl.countWC()) return false; //is not more general than cl
		
		if (C.X.encloses(cl.C.X) && C.Y.encloses(cl.C.Y) && C.Z.encloses(cl.C.Z)) return true;
		
		return false;
	}
}
