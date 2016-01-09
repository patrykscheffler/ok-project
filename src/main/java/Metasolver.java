import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* TODO:
 * wymyslic jak przechowywac zadania i przerwy na maszynach
 * 'uszeregowanie pierwotne', czyli rozlozyc zadania zgodnie z przerwami i zalozeniami
 * zapis aktualnego rozwiazania do pliku w folderze ROZWIAZANIA
 * funkcja celu, czyli czas przetwarzania wszystkich zadan
 * szeregowanie algorytmem GENETYCZNYM
 */

public class Metasolver {
    public static void main(String args[]) throws NumberFormatException, IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Podaj numer instancji problemu do rozwiazania: ");
        int instance = Integer.parseInt(br.readLine());
        String plik = "INSTANCJE/instancja" + instance + ".problem";
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(plik));
        BufferedReader read = new BufferedReader(new InputStreamReader(is));
        read.readLine();
        int tasks = Integer.parseInt(read.readLine());
        String task;
        String task_parts[];
        int id = 1;
        ArrayList<Task> tasksContainer = new ArrayList<Task>();
        tasksContainer.add(null);
        Task tmpTask;
        while((task = read.readLine()).endsWith(";")) {
        	task_parts = task.split(";");
        	tmpTask = new Task(
        			id, 
        			Integer.parseInt(task_parts[0].replaceAll("[\\D]", "")), 
        			Integer.parseInt(task_parts[1].replaceAll("[\\D]", "")));
        	tasksContainer.add(tmpTask);
        }
        String breakk = task;
        String break_parts[];
        ArrayList<Break> breaksContainer = new ArrayList<Break>();
        breaksContainer.add(null);
        Break tmpBreak;
        do {
        	break_parts = breakk.split(";");
        	tmpBreak = new Break(
        			Integer.parseInt(break_parts[0].replaceAll("[\\D]", "")), 
        			Integer.parseInt(break_parts[1].replaceAll("[\\D]", "")), 
        			Integer.parseInt(break_parts[2].replaceAll("[\\D]", "")), 
        			Integer.parseInt(break_parts[3].replaceAll("[\\D]", "")));
        	breaksContainer.add(tmpBreak);
        } while(!(breakk = read.readLine()).endsWith("*"));
        //
        System.out.println("Zakoñczono przetwarzanie instancji nr " + instance + " pomyœlnie! SprawdŸ plik ROZWIAZANIA/instancja" + instance + ".rozwiazanie");
    }
}