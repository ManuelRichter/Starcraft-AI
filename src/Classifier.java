
public class Classifier {

	public String C = ""; //condition
	public int A = 0; //action
	public double p = 0.0f; //reward
	public double e = 0.0f; ; //error
	public double F = 0.0f; //fitness
	public int exp = 0; //experience
	public int ts = 0; //timestamp
	public int as = 1; //
	public int n = 1;  //numerosity
	
	public Classifier()
	{		
		
	}
	
	
	public boolean doesMatch(String env) {
		if (C.equals(env)) return true;
		int count = 0;
		for (int i = 0;i<C.length();i++)
		{
			if (C.charAt(i) == env.charAt(i)) count++;
			if (C.charAt(i) == '#') count++;
		}
		if (count == C.length()) return true;
		return false;
	}
}
