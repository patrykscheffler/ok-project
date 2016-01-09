public class Task {
	private int id;
    private Operation op1;
    private Operation op2;

	public Task(int id, int time_op1, int time_op2) {
		this.setId(id);
    	this.setOp1(time_op1);
    	this.setOp2(time_op2);
    }
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Operation getOp1() {
		return op1;
	}

	public void setOp1(int time) {
		this.op1 = new Operation(time, 1);
	}

	public Operation getOp2() {
		return op2;
	}

	public void setOp2(int time) {
		this.op2 = new Operation(time, 2);
	}
}