package GridBlocks;

public class GridNumberBlock {
	GridPosition pos;
	int num_value;
	String num_icon;
	
	public GridNumberBlock(GridPosition init_pos, int init_value)
	{
		pos = init_pos;
		num_value = init_value;
		
		num_icon = int_to_emote(num_value);
	}
	
	public String int_to_emote(int value)
	{
		switch (value) {
		case 1:
			return ":one:";
		case 2:
			return ":two:";
		case 3:
			return ":three:";
		case 4:
			return ":four:";
		case 5:
			return ":five:";
		case 6:
			return ":six:";
		case 7:
			return ":seven:";
		case 8:
			return ":eight:";
		case 9:
			return ":nine:";
		case 0:
			return ":zero:";
		default:
			break;
		}
		
		return ":hash:";
	}
	
	void DebugCalculations()
	{
		// Debug division
		System.out.println("4/5 = " + Math.floorDiv(4, 5) + '\n');
		System.out.println("4%5 = " + Math.floorMod(4, 5) + '\n');
		
		System.out.println("3/5 = " + Math.floorDiv(3, 5) + '\n');
		System.out.println("3%5 = " + Math.floorMod(3, 5) + '\n');
		
		System.out.println("2/5 = " + Math.floorDiv(2, 5) + '\n');
		System.out.println("2%5 = " + Math.floorMod(2, 5) + '\n');
		
		System.out.println("1/5 = " + Math.floorDiv(1, 5) + '\n');
		System.out.println("1%5 = " + Math.floorMod(1, 5) + '\n');
		
		System.out.println("0/5 = " + Math.floorDiv(0, 5) + '\n');
		System.out.println("0%5 = " + Math.floorMod(0, 5) + '\n');
		
		// Negative numbers
		
		System.out.println("-1/5 = " + Math.floorDiv(-1, 5) + '\n');
		System.out.println("-1%5 = " + Math.floorMod(-1, 5) + '\n');
		
		System.out.println("-2/5 = " + Math.floorDiv(-2, 5) + '\n');
		System.out.println("-2%5 = " + Math.floorMod(-2, 5) + '\n');
		
		System.out.println("-3/5 = " + Math.floorDiv(-3, 5) + '\n');
		System.out.println("-3%5 = " + Math.floorMod(-3, 5) + '\n');
		
		System.out.println("-4/5 = " + Math.floorDiv(-4, 5) + '\n');
		System.out.println("-4%5 = " + Math.floorMod(-4, 5) + '\n');
		
		System.out.println("-5/5 = " + Math.floorDiv(-5, 5) + '\n');
		System.out.println("-5%5 = " + Math.floorMod(-5, 5) + '\n');
		
		System.out.println("-6/5 = " + Math.floorDiv(-6, 5) + '\n');
		System.out.println("-6%5 = " + Math.floorMod(-6, 5) + '\n');
	}
}
