package au.com.addstar.ShopSigns;

public class Util
{
	public static double parsePrice(String line, int number) throws IllegalArgumentException
	{
		if(line.startsWith("$"))
			line = line.substring(1);
		
		try
		{
			double amount = Double.parseDouble(line);
			if(amount < 0)
				throw new IllegalArgumentException("Line " + number + ": Price must be 0 or more");
			
			return amount;
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Line " + number + ": Price must be a decimal 0 or more. It may start with $");
		}
	}
}
