
public class Constants {
	//TODO get values from paper
	
	
	public static boolean doSubsumption = true; //
	
	public static int[] possibleActions = {0,1,2}; 
	public static int maxPop = 200; //max population entries
	public static int ThetaMna = 3; // minimum of distinct actions in M
	//public static double Pr = 0.33; //P# probabilty to insert a # for covering
	public static double pExplr = 0.2; //probability to explore 
	public static double beta = 0.5; //learning rate
	public static double epsilon0 = 20.0; //minimum error to be ok with
	public static double alpha = 0.3; 	//
	public static double nu = 5.0; 		//
	public static int ThetaSub = 20; 	// minimum exp to subsume
	public static double gamma = 0.71 ; //discount factor
	public static int N = maxPop; // = maxPop? 
	public static double delta = 0.1; //
	public static double ThetaDel = 20.0; //threshold to consider deletion 
	public static double ThetaGA = 12.0; //threshold till genetic algorithm
	public static int ChoicePoint = 5; //also threshold for deletion ?!? //own value
	
	public static double pI = 10.0; //initial p
	public static double eI = 0.0; //inital e
	public static double FI = 0.01; //inital F
	
}
