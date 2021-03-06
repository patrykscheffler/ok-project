import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Metasolver {
	/** Tuning metaheurystyki */
	private final static int splitPlaceTuning = 50; //miejsce podzialu przy krzyzowaniu [%]
	private final static int populationAmountTuning = 200; //liczebnosc generowanej populacji
	private final static double mutationRateTuning = 5; //procent szansy na mutacje [%]
    private final static int tournamentSizeTuning = 20; //liczba uszeregowan brana do turnieju
    private final static boolean keepBestIndividual = true;
    
    /** Warunek stopu */
    private final static String stopCause = "DIFFERENCE"; //warunek stopu; TIME / PERCENT / DIFFERENCE / ITERATIONS
    private final static double stopArg = 0.001; //argument warunku stopu; SEKUNDY / PROCENT[%] / PROCENT[%] / LICZBA ITERACJI
	
	private static int instanceNumber;
    private static List<Task> tasksContainer;
    private static List<Break> breaksContainer;
    private static List<Individual> population;
    private static int tasksAmount;
    private static int firstScheduleFitness;
    private static Individual theBest;

    /** Pobiera numer instancji do zaladowania z folderu INSTANCJE;
     * Odczytuje plik konkretnej instancji;
     * Zapisuje instancje w dwoch listach (zadania i przerwy osobno) */
    private static void loadInstance(int instanceNumber) throws IOException {
        tasksContainer = new ArrayList<Task>();
        breaksContainer = new ArrayList<Break>();

        String plik = "INSTANCJE/instancja" + instanceNumber + ".problem";
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(plik));
        BufferedReader read = new BufferedReader(new InputStreamReader(is));
        read.readLine();
        tasksAmount = Integer.parseInt(read.readLine());
        String line;
        String taskParts[];
        int id = 1;

        Task tmpTask;
        for (int i = 0; i < tasksAmount; i++) {
            line = read.readLine();
            taskParts = line.split(";");
            tmpTask = new Task(
                    (i + 1),
                    Integer.parseInt(taskParts[0].replaceAll("[\\D]", "")),
                    Integer.parseInt(taskParts[1].replaceAll("[\\D]", "")));
            tasksContainer.add(tmpTask);
        }

        String breakParts[];

        Break tmpBreak;
        while (!(line = read.readLine()).endsWith("*")) {
            breakParts = line.split(";");
            tmpBreak = new Break(
                    Integer.parseInt(breakParts[0].replaceAll("[\\D]", "")),
                    Integer.parseInt(breakParts[1].replaceAll("[\\D]", "")),
                    Integer.parseInt(breakParts[2].replaceAll("[\\D]", "")),
                    Integer.parseInt(breakParts[3].replaceAll("[\\D]", "")));
            breaksContainer.add(tmpBreak);
        }
    }
    
    /** Zwraca wartosc funkcji celu dla pierowotnego ulozenia */
    private static Individual getFirstSchedule() {
    	List<Integer> list = IntStream.range(1, tasksAmount + 1).boxed().collect(Collectors.toList());
    	Individual firstSchedule = generateMachine(list.toArray(new Integer[list.size()]));
    	return firstSchedule;
    }
    
    /** Zapisuje najlepsze (aktualnie) rozwiazanie do pliku w folderze ROZWIAZANIA */
    private static void saveSolution() throws FileNotFoundException {
    	int breaksCountM1 = 0;
        int breaksCountM2 = 0;
        int breaksTimeM1 = 0;
        int breaksTimeM2 = 0;
        int idleCountM1 = 0;
        int idleCountM2 = 0;
        int idleTimeM1 = 0;
        int idleTimeM2 = 0;
    	String plik = "ROZWIAZANIA/instancja" + instanceNumber + ".rozwiazanie";
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(plik));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os)); 
        pw.write("**** " + Integer.toString(instanceNumber) + " ****\n");
        
        pw.write(Integer.toString(theBest.getFitness()) + "; " + Integer.toString(firstScheduleFitness) + "\n");
        pw.write("M1: ");
        
        int timeOnMachine = 0;
        for (int i = 0; i < theBest.getMachine1().size(); i++) {
            int taskId = theBest.getMachine1().get(i)[0];
            int taskTime = theBest.getMachine1().get(i)[1];
            int instanceTaskTime = 0;
        	if(taskId > 0) { //OPERATION
        		instanceTaskTime = tasksContainer.get(taskId - 1).getOp1().getTime();
        		pw.write("o1_" + Integer.toString(taskId) +
        				", " + Integer.toString(timeOnMachine) +
        				", " + Integer.toString(instanceTaskTime) +
        				", " + Integer.toString(taskTime) +
        				"; ");
        	} else if(taskId == 0) { //IDLE
        		idleCountM1++;
        		idleTimeM1 += taskTime;
        		pw.write("idle_" + Integer.toString(idleCountM1) + "_M1" +
        				", " + Integer.toString(timeOnMachine) +
        				", " + Integer.toString(taskTime) +
        				"; ");
        	} else { //MAINTENCE BREAK
        		breaksCountM1++;
        		breaksTimeM1 += taskTime;
        		pw.write("maint_" + Integer.toString(breaksCountM1) + "_M1" +
        				", " + Integer.toString(timeOnMachine) +
        				", " + Integer.toString(taskTime) +
        				"; ");
        	}
        	timeOnMachine += taskTime;
        }
        
        pw.write("\nM2: ");
        
        timeOnMachine = 0;
        for (int i = 0; i < theBest.getMachine2().size(); i++) {
            int taskId = theBest.getMachine2().get(i)[0];
            int taskTime = theBest.getMachine2().get(i)[1];
        	if(taskId > 0) { //OPERATION
        		pw.write("o2_" + Integer.toString(taskId) +
        				", " + Integer.toString(timeOnMachine) +
        				", " + Integer.toString(taskTime) +
        				", " + Integer.toString(0) +
        				"; ");
        	} else if(taskId == 0) { //IDLE
        		idleCountM2++;
        		idleTimeM2 += taskTime;
        		pw.write("idle_" + Integer.toString(idleCountM2) + "_M2" +
        				", " + Integer.toString(timeOnMachine) +
        				", " + Integer.toString(taskTime) +
        				"; ");
        	} else { //MAINTENCE BREAK
        		breaksCountM2++;
        		breaksTimeM2 += taskTime;
        		pw.write("maint_" + Integer.toString(breaksCountM2) + "_M2" +
        				", " + Integer.toString(timeOnMachine) +
        				", " + Integer.toString(taskTime) +
        				"; ");
        	}
        	timeOnMachine += taskTime;
        }
        
        pw.write("\n");
        
        pw.write(Integer.toString(breaksCountM1) + ", " + Integer.toString(breaksTimeM1) + "\n");
        pw.write(Integer.toString(breaksCountM2) + ", " + Integer.toString(breaksTimeM2) + "\n");
        
        pw.write(Integer.toString(idleCountM1) + ", " + Integer.toString(idleTimeM1) + "\n");
        pw.write(Integer.toString(idleCountM2) + ", " + Integer.toString(idleTimeM2) + "\n");
        
        pw.write("*** EOF ***");
        pw.close();
        
        System.out.println("Zakonczono przetwarzanie instancji nr " + instanceNumber + " pomyslnie! Sprawdz plik ROZWIAZANIA/instancja" + instanceNumber + ".rozwiazanie");
    }

    /** Miesza kolejnosc zadan w liscie i tworzy z nich populacje;
     * Zapisuje wygenerowana populacje na maszynach w liscie */
    private static void randomPopulation() {
        List<Integer> list = IntStream.range(1, tasksAmount + 1).boxed().collect(Collectors.toList());
        List<Integer[]> tmpPopulation = new ArrayList<>();
        population = new ArrayList<Individual>();

        for (int i = 0; i < populationAmountTuning; i++) {
            Collections.shuffle(list);
            tmpPopulation.add((Integer[]) list.toArray(new Integer[list.size()]));
        }

        for (int i = 0; i < tmpPopulation.size(); i++) {
        	/*System.out.println("Jedna z wielu skladowych populacji (identyfikatory zadan):");
            for (int j = 0; j < tmpPopulation.get(i).length; j++) {
                System.out.printf(String.valueOf(tmpPopulation.get(i)[j]) + " ");
            }
            System.out.printf("\n");*/
            population.add(generateMachine(tmpPopulation.get(i)));
        }
        //System.out.printf("\n");
    }

    /** Pobiera liste zadan z pewna kolejnoscia ich ulozenia;
     * Zwraca nam obiekt z lista zadan i konkretnymi uszeregowanami na maszynach;
     * W zmiennej fitness trzymamy policzona funkcje celu, czyli czas po ktorym
     * wykonaja sie wszystkie zadania wraz z przerwami */
    private static Individual generateMachine(Integer[] tasks) { 
        List<Integer[]> machine1 = new ArrayList<Integer[]>();
        List<Integer[]> machine2 = new ArrayList<Integer[]>();
        int maxTimeM1 = 0, maxTimeM2 = 0;

        /** Przetwarzamy kazde zadanie, najpierw na I maszynie a pozniej na II */
        for (int i = 0; i < tasksAmount; i++) {
            int op1Time = tasksContainer.get(tasks[i] - 1).getOp1().getTime();
            int op2Time = tasksContainer.get(tasks[i] - 1).getOp2().getTime();
            int op1TimeCopy = op1Time;
            int op2TimeCopy = op2Time;
            Break tmpBreak;

            /** Puki napotykamy przerwe na poczatku to ja zapisujemy na maszynie */
            while ((tmpBreak = checkBreak(1, maxTimeM1, 1)) != null) {
            	int breakId = tmpBreak.getId()*(-1);
                int breakTime = tmpBreak.getTime();
                machine1.add(new Integer[]{breakId, breakTime});
                maxTimeM1 += breakTime;
            }

            /** Powtarzamy, puki nie wykonamy w calosci operacji */
            do {
            	/** Jesli nie ma przerwy na przeszkodzie to zapisujemy operacje na maszyne */
                if ((tmpBreak = checkBreak(1, maxTimeM1, op1Time)) == null) {
                    machine1.add(new Integer[]{tasks[i], op1Time});
                    maxTimeM1 += op1Time;
                    op1Time = 0;
                /** Jesli bedzie przerwa w ciagu operacji to:
                 * obliczamy dlugosc operacji wraz z kara 20%
                 * zapisujemy zadanie do momentu przerwy na maszynie
                 * zapisujemy nastepnie przerwe, ktorej sie spodziewalismy */
                } else {
                    op1Time += (int) Math.ceil(op1TimeCopy * 0.2);
                    int timeToBreak = tmpBreak.getStart() - maxTimeM1;
                    machine1.add(new Integer[]{tasks[i], timeToBreak});
                    maxTimeM1 += timeToBreak;
                    op1Time -= timeToBreak;

                    int breakId = tmpBreak.getId()*(-1);
                    int breakTime = tmpBreak.getTime();
                    machine1.add(new Integer[]{breakId, breakTime});
                    maxTimeM1 += breakTime;
                }
            } while (op1Time > 0);

            /** Na maszynie drugiej nie moze zaczac sie zadne zadanie
             * do puki na masznie pierwszej jakies sie nie zakonczy
             * czyli mozemy sie od razu przesunac w pierwszej iteracji */
            if (i == 0) {
                machine2.add(new Integer[]{0, maxTimeM1});
                maxTimeM2 = maxTimeM1;
            }
            
            /** Na maszynie drugiej nie mamy do przetworzenia zadania;
             * na przyklad w wyniku trwajacej przerwy na M1 */
            if (maxTimeM1 - maxTimeM2 > 0) {
                machine2.add(new Integer[] {0, maxTimeM1 - maxTimeM2});
                maxTimeM2 += maxTimeM1 - maxTimeM2;
            }

            /** Puki napotykamy przerwe na poczatku to ja zapisujemy na maszynie */
            while ((tmpBreak = checkBreak(2, maxTimeM2, 1)) != null) {
            	int breakId = tmpBreak.getId()*(-1);
                int breakTime = tmpBreak.getTime();
                machine2.add(new Integer[]{breakId, breakTime});
                maxTimeM2 += breakTime;
            }

            /** j/w */
            do {
            	/** j/w */
                if ((tmpBreak = checkBreak(2, maxTimeM2, op2Time)) == null) {
                    machine2.add(new Integer[]{tasks[i], op2Time});
                    maxTimeM2 += op2Time;
                    op2Time = 0;
                /** j/w */
                } else {
                    op2Time = (int) Math.ceil(op2TimeCopy * 0.2);
                    int timeToBreak = tmpBreak.getStart() - maxTimeM2;
                    machine2.add(new Integer[]{tasks[i], timeToBreak});
                    maxTimeM2 += timeToBreak;
                    op2Time -= timeToBreak;

                    int breakId = tmpBreak.getId()*(-1);
                    int breakTime = tmpBreak.getTime();
                    machine2.add(new Integer[]{breakId, breakTime});
                    maxTimeM2 += breakTime;
                }
            } while (op2Time > 0);
        }

        /** Wypisywanie maszyny I w jedkostkach czasu */
        /*System.out.println("Maszyna I w jednostkach czasu:");
        for (int i = 0; i < machine1.size(); i++) {
            for (int j = 0; j < machine1.get(i)[1]; j++) {
                System.out.printf(String.valueOf(machine1.get(i)[0]) + " ");
            }
        }
        System.out.printf("\n"); */
        /** Wypisywanie maszyny II w jedkostkach czasu */
        /*System.out.println("Maszyna II w jednostkach czasu:");
        for (int i = 0; i < machine2.size(); i++) {
            for (int j = 0; j < machine2.get(i)[1]; j++) {
                System.out.printf(String.valueOf(machine2.get(i)[0]) + " ");
            }
        }
        System.out.printf("\n");*/

        int fitness = maxTimeM1 > maxTimeM2 ? maxTimeM1 : maxTimeM2;
        Individual individual = new Individual(tasks, machine1, machine2, fitness);

        return individual;
    }

    /** Zwraca nowa populacje o zadanym rozmiarze, wykonujac na starej uprzednio:
     * turniej i wylonienie dwoch najlepszych (dla danej probki) ulozen z obecnej populacji,
     * krzyzowanie na dwoch najlepszych ulozeniach z turnieji
     * mutowanie nowego ulozenia po krzyzowaniu */
    public static void evolvePopulation() {
        List<Individual> newPopulation = new ArrayList<>();

        int offset = 0;
        if (keepBestIndividual) {
            newPopulation.add(selectTheBest());
            offset = 1;
        }

        int selectedAmount = populationAmountTuning / tournamentSizeTuning;
        List<Individual> selected = new ArrayList<>();
        for (int i = 0; i < selectedAmount; i++) {
            selected.add(tournamentSelection());
        }

        Random r = new Random();
        for (int i = offset; i < populationAmountTuning; i++) {
            Individual ind1 = selected.get(r.nextInt(selectedAmount));
            Individual ind2 = selected.get(r.nextInt(selectedAmount));
            Integer[] newIndGene = crossover(ind1, ind2);
            newIndGene = mutation(newIndGene);
            Individual newInd = generateMachine(newIndGene);
            newPopulation.add(newInd);
        }
        population = newPopulation;
    }

    /** Tworzy turniej o zadanym rozmiarze z populacji i wybiera z niego najlepsze rozwiazanie */
    private static Individual tournamentSelection() {
        List<Individual> tournament = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < tournamentSizeTuning; i++) {
            int randomId = r.nextInt(population.size());
            tournament.add(population.get(i));
        }

        Individual fittest = tournament.get(0);
        for (int i = 1; i < tournament.size(); i++) {
            if (tournament.get(i).getFitness() < fittest.getFitness()) {
                fittest = tournament.get(i);
            }
        }

        return fittest;
    }
    
    /** Wybiera najlepsze rozwiazanie z aktualnej populacji */
    private static Individual selectTheBest() {
        Individual theBest = population.get(0);
        for (int i = 1; i < population.size(); i++) {
            if (population.get(i).getFitness() < theBest.getFitness()) {
            	theBest = population.get(i);
            }
        }

        return theBest;
    }

    /** Pobiera dwa konkretne uszeregowania na maszynach i bierze z nich kolejnosc zadan;
     * Dzieli te listy i wg. tego miejsca wykonuje krzyzowanie
     * Zwraca polaczona, skrzyzowana liste zadan */
    private static Integer[] crossover(Individual ind1, Individual ind2) {
        Integer[] tasks1 = ind1.getGenes();
        Integer[] tasks2 = ind2.getGenes();

        List<Integer> newTasks = new ArrayList<Integer>();
        int splitPlace = (int) Math.floor(tasks1.length * (splitPlaceTuning/100));

        for (int i = 0; i < splitPlace; i++) {
            newTasks.add(tasks1[i]);
        }

        for (int i = 0; i < tasks1.length; i++) {
            if (!newTasks.contains(tasks2[i])) {
                newTasks.add(tasks2[i]);
            }
        }

        Integer[] result = (Integer[]) newTasks.toArray(new Integer[newTasks.size()]);
        return result;
    }

    /** Pobiera liste zadan;
     * przy zadanym procencie szansy zamienia sasiadujace wartosci ze soba */
    private static Integer[] mutation(Integer[] tasks) {
        double mutationRate = mutationRateTuning/100.0;
        Random r = new Random();
        int first, second;

        for (int i = 0; i < tasks.length; i++) {
            if (Math.random() <= mutationRate) {
                first = r.nextInt(tasks.length);
                do {
                    second = r.nextInt(tasks.length);
                } while (second == first);

                Integer tmp = tasks[first];
                tasks[first] = tasks[second];
                tasks[second] = tmp;
            }
        }

        return tasks;
    }

    /** Pobiera numer maszyny, konkretny moment oraz czas trwania operacji
     * Sprawdza czy w ciagu zadanego czasu wystepuje przerwa na maszynie
     * Zwraca null jesli nie wystapi przerwa lub zwraca przerwe, ktora wystapi */
    private static Break checkBreak(int machine, int start, int time) {
        for (int i = 0; i < breaksContainer.size(); i++) {
            Break tmpBreak = breaksContainer.get(i);
            if (tmpBreak.getMachine() != machine) continue;

            int startBreak = tmpBreak.getStart();
            int endBreak = startBreak + tmpBreak.getTime();
            if ((start >= startBreak && start < endBreak) ||
                    (startBreak >= start && startBreak < start + time)) {
                return tmpBreak;
            }
        }
        return null;
    }

    public static void main(String args[]) throws NumberFormatException, IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Podaj liczbe instancji do rozwiazania: ");
        int instanceAmount = Integer.parseInt(br.readLine());
        System.out.print("Podaj numer pierwszej instancji do rozwiazania: ");
        instanceNumber = Integer.parseInt(br.readLine());

        for(instanceNumber=instanceNumber; instanceNumber<instanceAmount; instanceNumber++) {
			loadInstance(instanceNumber);
			firstScheduleFitness = getFirstSchedule().getFitness();
		    randomPopulation();
		    long startTime = System.nanoTime();
		    long stopTime = startTime;
		    double duration = 0.0;
		    double percent = 100.0;
		    if(stopCause == "TIME") {
		    	while((double)(duration / 1000000000.0) <= stopArg) {
		        	evolvePopulation();
		        	stopTime = System.nanoTime();
		        	duration = (stopTime - startTime);
		        	System.out.println(Integer.toString((int)(duration / 1000000000.0)) +
		        			"s out of " + stopArg + "s");
		    	}
		    } else if(stopCause == "PERCENT") {
		    	while(percent < stopArg) {
		    		evolvePopulation();
		    		percent = (((firstScheduleFitness - selectTheBest().getFitness()) * 100.0) / firstScheduleFitness);
		    		System.out.println(Double.toString(percent) +
		    				"% out of " + Double.toString(stopArg) + "%");
		    	}
		    } else if(stopCause == "DIFFERENCE") {
		    	int lastTheBest;
		    	lastTheBest = firstScheduleFitness;
		    	double diff = 100.0;
		    	while(diff >= stopArg) {
		    		evolvePopulation();
		    		diff = (((lastTheBest - (lastTheBest = selectTheBest().getFitness())) * 100.0) / lastTheBest);
		    		if(diff < 0.0) { diff = 100.0; } else {
		    			System.out.println(Double.toString(diff));
		    		}
		    	}
		    } else if(stopCause == "ITERATIONS") {
		    	for(int i=0; i<stopArg; i++) {
		    		evolvePopulation();
		    		System.out.println("Iteration no " + i + " out of " + stopArg);
		    	}
		    }
		    theBest = selectTheBest();
		    stopTime = System.nanoTime();
			duration = (stopTime - startTime);
			percent = (((firstScheduleFitness - theBest.getFitness()) * 100.0) / firstScheduleFitness);
			System.out.println("It took " + Integer.toString((int)(duration / 1000000000.0)) +
					"s to solve this problem! Optimised: " + Double.toString(percent) + "%");
			
		    saveSolution();
        }
    }
}