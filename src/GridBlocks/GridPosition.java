package GridBlocks;

public class GridPosition {
	public int x;
	public int y;
	
	public GridPosition(int init_x, int init_y)
	{
		x = init_x;
		y = init_y;
	}
	
	public boolean equals(GridPosition other_pos)
	{
		if (other_pos.x == x && other_pos.y == y)
		{
			return true;
		}
		
		return false;
	}
}
