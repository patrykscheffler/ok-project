import java.util.List;

public class Individual {
    private Integer[] genes;
    private List<Integer[]> machine1;
    private List<Integer[]> machine2;
    private int fitness;

    public Individual(Integer[] genes, List<Integer[]> machine1, List<Integer[]> machine2, int fitness) {
        this.genes = genes;
        this.machine1 = machine1;
        this.machine2 = machine2;
        this.fitness = fitness;
    }

    public Integer[] getGenes() {
        return genes;
    }

    public void setGenes(Integer[] genes) {
        this.genes = genes;
    }

    public List<Integer[]> getMachine1() {
        return machine1;
    }

    public void setMachine1(List<Integer[]> machine1) {
        this.machine1 = machine1;
    }

    public List<Integer[]> getMachine2() {
        return machine2;
    }

    public void setMachine2(List<Integer[]> machine2) {
        this.machine2 = machine2;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}
