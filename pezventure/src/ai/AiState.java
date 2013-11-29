package ai;

public interface AiState
{
	public void init();
	public void sense();
	public void think();
	public void act();
	public AiState exit();
	
}
