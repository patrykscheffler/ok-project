import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Generator {
    /** Field that enables randomization */
    private final static Random random = new Random();

    /** Generate random integer in range */
    public static int randomInRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return random.nextInt(range) + (min <= max ? min : max);
    }

    /** Chceck if break can be generated in that place */
    public static boolean checkBreak(int arr[][], int machine, int start, int time) {
        if (arr[machine].length <= (start + time)) return false;
        for (int i = start; i <= (start + time); i++) {
            if (arr[machine][i] == 1) { return false; }
        }
        return true;
    }

    public static <E> void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Podaj numer instancji problemu do wygenerowania: ");
        int instanceNumber = Integer.parseInt(br.readLine());
        System.out.print("Podaj ilosc zadan do wygenerowania w tej instancji: ");
        int tasksAmount = Integer.parseInt(br.readLine());
        System.out.print("Podaj minimalny czas wykonywania pojedynczej operacji: ");
        int minTaskTime = Integer.parseInt(br.readLine());
        System.out.print("Podaj maksymalny czas wykonywania pojedynczej operacji: ");
        int maxTaskTime = Integer.parseInt(br.readLine());
        System.out.print("Podaj minimalny czas trwania pojedynczej przerwy: ");
        int minBreakTime = Integer.parseInt(br.readLine());
        System.out.print("Podaj maksymalny czas trwania pojedynczej przerwy: ");
        int maxBreakTime = Integer.parseInt(br.readLine());

        int breakCountM1 = randomInRange(2, tasksAmount/2);
        int breakCountM2 = randomInRange(2, tasksAmount/2);
        int maxBreakCount = breakCountM1 > breakCountM2 ? breakCountM1 : breakCountM2;

        String plik = "INSTANCJE/instancja" + instanceNumber + ".problem";
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(plik));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
        pw.write("**** " + Integer.toString(instanceNumber) + " ****\n");
        pw.write(Integer.toString(tasksAmount) + "\n");

        int op1Time;
        int op2Time;
        int max_longM1 = 0;
        int max_longM2 = 0;
        for (int i = 1; i <= tasksAmount; i++) {
            op1Time = randomInRange(minTaskTime, maxTaskTime);
            op2Time = randomInRange(minTaskTime, maxTaskTime);
            max_longM1 += op1Time;
            max_longM2 += op2Time;
            pw.write(op1Time + "; " + op2Time + "; 1; 2;\n");
        }
        
        int break_longM1 = 0;
        ArrayList<Integer> breaksM1 = new ArrayList<Integer>();
        for (int j = 1; j <= breakCountM1; j++) {
        	Integer breakTime = randomInRange(minBreakTime, maxBreakTime);
        	break_longM1 += breakTime;
        	breaksM1.add(breakTime);
        }
        
        int break_longM2 = 0;
        ArrayList<Integer> breaksM2 = new ArrayList<Integer>();
        for (int j = 1; j <= breakCountM2; j++) {
        	Integer breakTime = randomInRange(minBreakTime, maxBreakTime);
        	break_longM2 += breakTime;
        	breaksM2.add(breakTime);
        }
        
        int max_long = max_longM1 > max_longM2 ? max_longM1 : max_longM2;
        int max_breaks = break_longM1 > break_longM2 ? break_longM1 : break_longM2;
        max_long +=
        		max_breaks +
        		(int) Math.ceil(maxTaskTime * 0.2 * maxBreakCount) +
        		maxTaskTime; //for IDLE at M2 while procesing OP1 at M1

        int[][] visual = new int[2][max_long + 1];
        for (int i = 0; i <= 1; i++) {
            Arrays.fill(visual[i], 0);
        }
        visual[0][0] = breakCountM1;
        visual[1][0] = breakCountM2;

        int breakTime;
        int startTime;
        int counter = 1;
        for (int i = 0; i <= 1; i++) {
            for (int j = 1; j <= visual[i][0]; j++) {
                do {
                	if(i == 0) { //M1
                		breakTime = breaksM1.get(j-1);
                	} else { //M2
                		breakTime = breaksM2.get(j-1);
                	}
                    startTime = randomInRange(0, max_long);
                } while (!checkBreak(visual, i, startTime, breakTime));

                pw.write(counter + "; " + (i + 1) + "; " + breakTime + "; " + startTime + "\n");
                counter++;

                for (int k = startTime; k <= (startTime + breakTime); k++) {
                    visual[i][k] = 1;
                }
            }
        }
        pw.write("*** EOF ***");
        
        /** Wizualizacja przerw na maszynach */
        /*pw.println();
        for (int i = 0; i <= 1; i++) {
            for(int j = 1; j <= max_long; j++) {
                pw.write(Integer.toString(visual[i][j]));
            }
            pw.println();
        }*/
        
        pw.close();
        System.out.println("Zakonczono generowanie instancji problemu nr " + instanceNumber + " z " + tasksAmount + " zadaniami!");
    }
}