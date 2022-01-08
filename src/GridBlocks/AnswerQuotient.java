package GridBlocks;

public class AnswerQuotient {
	int expected_val;
	GridPosition[] pos;
	String icon_string = ":regional_indicator_q:";
	int num_blocks;
	
	public AnswerQuotient(int num)
	{
		num_blocks = num;
		
		pos = new GridPosition[num_blocks];
		
		for (int i =0; i < num_blocks; i++)
		{
			pos[i] = new GridPosition(0, 0);
		}
	}
}
