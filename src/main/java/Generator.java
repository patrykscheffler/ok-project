import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Generator {
    public static int randomWithRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int)(Math.random() * range) + (min <= max ? min : max);
    }
    
    public static boolean checkBreak(int arr[], int start, int time) {
    	for(int i=start; i<=start+time; i++) {
    		if(arr[i] == 1) { return false; }
    	}
    	return true;
    }

    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Podaj numer instancji problemu do wygenerowania: ");
        int instance = Integer.parseInt(br.readLine());
        System.out.print("Podaj ilosc zadan do wygenerowania w tej instancji: ");
        int tasks = Integer.parseInt(br.readLine());
        System.out.print("Podaj minimalny czas wykonywania pojedynczej operacji: ");
        int min_time = Integer.parseInt(br.readLine());
        System.out.print("Podaj maksymalny czas wykonywania pojedynczej operacji: ");
        int max_time = Integer.parseInt(br.readLine());
        int break_countM1 = randomWithRange(2, tasks/2);
        int break_countM2 = randomWithRange(2, tasks/2);
        System.out.print("Podaj minimalny czas trwania pojedynczej przerwy: ");
        int min_break = Integer.parseInt(br.readLine());
        System.out.print("Podaj maksymalny czas trwania pojedynczej przerwy: ");
        int max_break = Integer.parseInt(br.readLine());
        int max_long = (max_time*2*tasks) + (int)Math.ceil(max_time*0.2*break_countM1) + (int)Math.ceil(max_time*0.2*break_countM2) + (break_countM1*max_break) + (break_countM2*max_break);
        int[] M1 = new int[max_long+1];
        Arrays.fill(M1, 0);
        int[] M2 = new int[max_long+1];
        Arrays.fill(M2, 0);
        String plik = "INSTANCJE/instancja" + instance + ".problem";
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(plik));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
        pw.write("**** " + Integer.toString(instance) + " ****");
        pw.println();
        pw.write(Integer.toString(tasks));
        pw.println();
        int t1;
        int t2;
        for(int i=1; i<=tasks; i++) {
            t1 = randomWithRange(min_time, max_time);
            t2 = randomWithRange(min_time, max_time);
            pw.write(t1 + "; " + t2 + "; 1; 2;");
            pw.println();
        }
        int t;
    	int start;
        for(int i=1; i<=break_countM1; i++) {
        	do {
        		t = randomWithRange(min_break, max_break);
        		start = randomWithRange(0, max_long);
        	} while(!checkBreak(M1, start, t));
            pw.write(i + "; 1; " + t + "; " + start);
            pw.println();
            for(int j=start; j<=start+t; j++) {
            	M1[j] = 1;
            }
        }
        for(int i=break_countM1+1; i<=break_countM2+break_countM1; i++) {
        	do {
        		t = randomWithRange(min_break, max_break);
        		start = randomWithRange(0, max_long);
        	} while(!checkBreak(M2, start, t));
            pw.write(i + "; 2; " + t + "; " + start);
            pw.println();
            for(int j=start; j<=start+t; j++) {
            	M2[j] = 1;
            }
        }
        pw.write("*** EOF ***");
        //WIZUALIZACJA:
        /*pw.println();
        for(int i=1; i<=max_long; i++) {
        	pw.write(Integer.toString(M1[i]));
        }
        pw.println();
        for(int i=1; i<=max_long; i++) {
        	pw.write(Integer.toString(M2[i]));
        }*/
        //WIZUALIZACJA
        pw.close();
        System.out.println("Zakonczono generowanie instancji problemu nr " + instance + " z " + tasks + " zadaniami!");
    }
}