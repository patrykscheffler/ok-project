public class Operation {
    private int time;
    private int machine;
    private boolean done;

	public Operation(int time, int machine) {
    	this.setTime(time);
    	this.setMachine(machine);
    	this.setDone(false);
    }
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getMachine() {
		return machine;
	}

	public void setMachine(int machine) {
		this.machine = machine;
	}
	
	public boolean getDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}