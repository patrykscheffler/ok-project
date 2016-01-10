public class Task {
	private int id;
    private Operation op1;
    private Operation op2;

	public Task(int id, int timeOp1, int timeOp2) {
		this.id = id;
		this.op1 = new Operation(timeOp1, 1);
		this.op2 = new Operation(timeOp2, 2);
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

	public void setOp1(Operation op1) {
		this.op1 = op1;
	}

	public Operation getOp2() {
		return op2;
	}

	public void setOp2(Operation op2) {
		this.op2 = op2;
	}
}