import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* TODO:
 * wymyslic jak przechowywac zadania i przerwy na maszynach
 * 'uszeregowanie pierwotne', czyli rozlozyc zadania zgodnie z przerwami i zalozeniami
 * zapis aktualnego rozwiazania do pliku w folderze ROZWIAZANIA
 * funkcja celu, czyli czas przetwarzania wszystkich zadan
 * szeregowanie algorytmem GENETYCZNYM
 */

public class Metasolver {
    private static List<Task> tasksContainer;
    private static List<Break> breaksContainer;
    private static int tasksAmount;

    public static void loadInstance() throws IOException {
        tasksContainer = new ArrayList<>();
        breaksContainer = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Podaj numer instancji problemu do rozwiazania: ");
        int instanceNumber = Integer.parseInt(br.readLine());
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

    private static void randomPopulation(int amount) {
        List<Integer> list = IntStream.range(1, tasksAmount + 1).boxed().collect(Collectors.toList());
        List<Integer[]> population = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Collections.shuffle(list);
            population.add((Integer[]) list.toArray(new Integer[list.size()]));
        }

        for (int i = 0; i < population.size(); i++) {
            for (int j = 0; j < population.get(i).length; j++) {
                System.out.printf(String.valueOf(population.get(i)[j]));
            }
            System.out.printf("\n");
            generateMachine(population.get(i));
        }
        System.out.printf("\n");
    }

    private static void generateMachine(Integer[] tasks) {
        List<Integer[]> machine1 = new ArrayList<>();
        List<Integer[]> machine2 = new ArrayList<>();
        int maxTimeM1 = 0, maxTimeM2 = 0;

        for (int i = 0; i < tasksAmount; i++) {
            int op1Time = tasksContainer.get(tasks[i] - 1).getOp1().getTime();
            int op2Time = tasksContainer.get(tasks[i] - 1).getOp2().getTime();
            Break tmpBreak;

            while ((tmpBreak = checkBreak(1, maxTimeM1, 1)) != null) {
                int breakTime = tmpBreak.getTime();
                machine1.add(new Integer[]{0, breakTime});
                maxTimeM1 += breakTime;
            }

            do {
                if ((tmpBreak = checkBreak(1, maxTimeM1, op1Time)) == null) {
                    machine1.add(new Integer[]{tasks[i], op1Time});
                    maxTimeM1 += op1Time;
                    op1Time = 0;
                } else {
                    op1Time = (int) Math.ceil(op1Time * 1.2);
                    int timeToBreak = tmpBreak.getStart() - maxTimeM1;
                    machine1.add(new Integer[]{tasks[i], timeToBreak});
                    maxTimeM1 += timeToBreak;
                    op1Time -= timeToBreak;

                    int breakTime = tmpBreak.getTime();
                    machine1.add(new Integer[]{0, breakTime});
                    maxTimeM1 += breakTime;
                }
            } while (op1Time > 0);

            if (i == 0) {
                machine2.add(new Integer[]{0, maxTimeM1});
                maxTimeM2 = maxTimeM1;
            }

            while ((tmpBreak = checkBreak(2, maxTimeM2, 1)) != null) {
                int breakTime = tmpBreak.getTime();
                machine2.add(new Integer[]{0, breakTime});
                maxTimeM2 += breakTime;
            }

            do {
                if ((tmpBreak = checkBreak(2, maxTimeM2, op2Time)) == null) {
                    machine2.add(new Integer[]{tasks[i], op2Time});
                    maxTimeM2 += op2Time;
                    op2Time = 0;
                } else {
                    op2Time = (int) Math.ceil(op2Time * 1.2);
                    int timeToBreak = tmpBreak.getStart() - maxTimeM2;
                    machine2.add(new Integer[]{tasks[i], timeToBreak});
                    maxTimeM2 += timeToBreak;
                    op2Time -= timeToBreak;

                    int breakTime = tmpBreak.getTime();
                    machine2.add(new Integer[]{0, breakTime});
                    maxTimeM2 += breakTime;
                }
            } while (op2Time > 0);
        }

        for (int i = 0; i < machine1.size(); i++) {
            for (int j = 0; j < machine1.get(i)[1]; j++) {
                System.out.printf(String.valueOf(machine1.get(i)[0]));
            }
        }
        System.out.printf("\n");
        for (int i = 0; i < machine2.size(); i++) {
            for (int j = 0; j < machine2.get(i)[1]; j++) {
                System.out.printf(String.valueOf(machine2.get(i)[0]));
            }
        }
        System.out.printf("\n");
    }

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
    	loadInstance();
        randomPopulation(5);

        for (int i = 0; i < tasksContainer.size(); i++) {
            System.out.println(tasksContainer.get(i).getId() + ") " + tasksContainer.get(i).getOp1().getTime() + ", " + tasksContainer.get(i).getOp2().getTime());
        }

        for (int i = 0; i < breaksContainer.size(); i++) {
            System.out.println(breaksContainer.get(i).getMachine() + ", " + breaksContainer.get(i).getStart() + ", " + breaksContainer.get(i).getTime());
        }

//        System.out.println("Zakonczono przetwarzanie instancji nr " + instanceNumber + " pomyslnie! Sprawdz plik ROZWIAZANIA/instancja" + instanceNumber + ".rozwiazanie");
    }
}