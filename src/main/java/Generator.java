import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

//1. CZY W NASZYM PRZYPADKU OBIE OPERACJNE JEDNEGO ZADANIA MOGA SIE WYKONAC NA TEJ SAMEJ MASZYNIE?
//2. CZY PRZERWA MUSI SIE SKONCZYC NA DANEJ MASZYNIE BY ZACZELA SIE DRUGA PRZERWA?

public class Generator {
    public static int randomWithRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int)(Math.random() * range) + (min <= max ? min : max);
    }

    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Podaj numer instancji problemu do wygenerowania: ");
        int instance = Integer.parseInt(br.readLine());
        System.out.print("Podaj ilosc zadan n do wygenerowania w ten instancji: ");
        int tasks = Integer.parseInt(br.readLine());
        System.out.print("Podaj minimalny czas wykonywania pojedynczej operacji: ");
        int min_time = Integer.parseInt(br.readLine());
        System.out.print("Podaj maksymalny czas wykonywania pojedynczej operacji: ");
        int max_time = Integer.parseInt(br.readLine());
        System.out.print("Podaj wspolczynnik X ilosci przerw maszyny (ans=X*n): ");
        double x = Double.parseDouble(br.readLine());
        int break_count = (int) (x*tasks);
        System.out.print("Podaj minimalny czas trwania pojedynczej przerwy: ");
        int min_break = Integer.parseInt(br.readLine());
        System.out.print("Podaj maksymalny czas trwania pojedynczej przerwy: ");
        int max_break = Integer.parseInt(br.readLine());
        String plik = "INSTANCJE/instancja" + instance + ".problem";
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(plik));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
        pw.write("**** " + Integer.toString(instance) + " ****");
        pw.println();
        pw.write(Integer.toString(tasks));
        pw.println();
        for(int i=1; i<=tasks; i++) {
            int t1 = randomWithRange(min_time, max_time);
            int t2 = randomWithRange(min_time, max_time);
            int m1 = randomWithRange(1, 2);
            int m2 = randomWithRange(1, 2);
            pw.write(t1 + "; " + t2 + "; " + m1 + "; " + m2 + ";");
            pw.println();
        }
        for(int i=1; i<=break_count; i++) {
            int m = randomWithRange(1, 2);
            int t = randomWithRange(min_break, max_break);
            int start = randomWithRange(0, max_time*tasks*2);
            pw.write(i + "; " + m + "; " + t + "; " + start);
            pw.println();
        }
        pw.write("*** EOF ***");
        pw.close();
        System.out.println("Zakonczono generowanie instancji problemu nr " + instance + " z " + tasks + " zadaniami!");
    }
}