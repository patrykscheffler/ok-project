import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

    public static void main(String args[]) throws IOException {
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
        int max_long = (maxTaskTime * 2 * tasksAmount) + (int) Math.ceil(maxTaskTime * 0.2 * breakCountM1) + (int) Math.ceil(maxTaskTime * 0.2 * breakCountM2) + (breakCountM1 * maxBreakTime) + (breakCountM2 * maxBreakTime);

        int[][] visual = new int[2][max_long + 1];
        for (int i = 0; i <= 1; i++) {
            Arrays.fill(visual[i], 0);
        }
        visual[0][0] = breakCountM1;
        visual[1][0] = breakCountM2;

        String plik = "INSTANCJE/instancja" + instanceNumber + ".problem";
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(plik));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
        pw.write("**** " + Integer.toString(instanceNumber) + " ****\n");
        pw.write(Integer.toString(tasksAmount) + "\n");

        int op1Time;
        int op2Time;
        for (int i = 1; i <= tasksAmount; i++) {
            op1Time = randomInRange(minTaskTime, maxTaskTime);
            op2Time = randomInRange(minTaskTime, maxTaskTime);
            pw.write(op1Time + "; " + op2Time + "; 1; 2;\n");
        }

        int breakTime;
        int startTime;
        int counter = 1;
        for (int i = 0; i <= 1; i++) {
            for (int j = 1; j <= visual[i][0]; j++) {
                do {
                    breakTime = randomInRange(minBreakTime, maxBreakTime);
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
//        Wizualizacja przerw na maszynach:
        pw.println();
        for (int i = 0; i <= 1; i++) {
            for(int j = 1; j <= max_long; j++) {
                pw.write(Integer.toString(visual[i][j]));
            }
            pw.println();
        }

        pw.close();
        System.out.println("Zakonczono generowanie instancji problemu nr " + instanceNumber + " z " + tasksAmount + " zadaniami!");
    }
}