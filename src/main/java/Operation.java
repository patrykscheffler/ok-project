public class Operation {
    private int time;
    private int machine;
    private boolean done;

	public Operation(int time, int machine) {
		this.time = time;
		this.machine = machine;
		this.done = false;
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