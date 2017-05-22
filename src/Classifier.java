
public class Classifier {

	public String C = ""; //condition
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

//not needed?
	public void GenCondition(int length) {
		C= new char[length].toString();
	}


	public boolean isEmpty() 
	{
		if (C.equals("")) return true;
		return false;
	}


	public int countWC() //count # in condition 
	{
		int wcount = 0;
		for (char ch : C.toCharArray()) 
		{
			if (ch == '#') wcount++;
		}
		return wcount;
	}


	public boolean moreGeneral(Classifier cl) {
		if (this.countWC() <= cl.countWC()) return false; //is not more general than cl
		int i = 0;
		do {
			if(this.C.toCharArray()[i] != '#' && this.C.toCharArray()[i] != cl.C.toCharArray()[i]) return false;
			i++;
		} while (i<this.C.length());
		
		return true;
	}
}
