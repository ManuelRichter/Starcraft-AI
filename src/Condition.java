import com.google.common.collect.Range;

public class Condition 
{
	public Range<Double> X;
	public Range<Double> Y;
	public Range<Double> Z;
	
	public Condition()
	{
		X = Range.closedOpen(1.0,1.0);
		Y = Range.closedOpen(1.0,1.0);
		Z = Range.closedOpen(1.0,1.0);
	}
	

}
