
class process {
	public int PID;
	public int arrivalTime;
	public int burstTime;
	public int priority;
	
	public process(int PID, int arrivalTime, int burstTime, int priority){
		this.PID = PID;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;
		this.priority = priority;
	}
}
