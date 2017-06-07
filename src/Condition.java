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
	
	public Condition(String arrSav)
	{
		arrSav = arrSav.replace("?", ";"); //so ugly
		arrSav = arrSav.replace("(", "");
		arrSav = arrSav.replace(")", "");
		arrSav = arrSav.replace("]", "");
		
		String[] s = arrSav.split(":");
		
		String[] x = s[0].split(";");
		String[] y = s[1].split(";");
		String[] z = s[2].split(";");
		this.X = Range.closedOpen(Double.parseDouble(x[0]),Double.parseDouble(x[1]));
		this.Y = Range.closedOpen(Double.parseDouble(y[0]),Double.parseDouble(y[1]));
		this.Z = Range.closedOpen(Double.parseDouble(z[0]),Double.parseDouble(z[1]));
	}
	
	@Override
	public String toString()
	{
		return  X.toString() + ":" + Y + ":" + Z;
	}
	
}
