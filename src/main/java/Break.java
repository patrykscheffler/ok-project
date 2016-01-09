public class Break {
	private int id;
    private int machine;
    private int time;
    private int start;

	public Break(int id, int machine, int time, int start) {
		this.setId(id);
    	this.setMachine(machine);
    	this.setTime(time);
    	this.setStart(start);
    }
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getMachine() {
		return machine;
	}

	public void setMachine(int machine) {
		this.machine = machine;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}