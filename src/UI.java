import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Scanner;
public class UI {
    private final static Scanner EINGABE = new Scanner(System.in);
    public static void main(String[] args) {
        Dictionary<String, String> dict = new SortedArrayDictionary<>();
        while (EINGABE.hasNextLine()){
            String command = EINGABE.nextLine();
            String[] Input = command.split("[^a-z^A-Z^ß^ä^ö^ü^Ä^Ö^Ü]+");
            if (Input[0].equals("create")){
                if(Input[1].equals("SortedArrayDictionary")){
                    dict = new SortedArrayDictionary<>();
                    System.out.println('S');
                } else if (Input[1].equals("HashDictionary")){
                    dict = new HashDictionary<>(3);
                    System.out.println('H');
                }else if(Input[1].equals("BinaryTreeDictionary")){
                    dict = new BinaryTreeDictionary<>();
                    System.out.println('B');
                }
                else {
                    dict = new SortedArrayDictionary<>();
                    System.out.println('S');
                }
            }
            //read
            if (Input[0].equals("read")){
                System.out.println("r");
                int n = 0;

                String[] eingabe = command.split("[^a-z^A-Z^ß^ä^ö^ü^Ä^Ö^Ü^0-9^.]+");
                String leng = eingabe[1];

                try {
                    n = Integer.parseInt(leng);
                } catch (Exception e){
                   e.printStackTrace();
                }

                System.out.println(n);
                int count = 0;
                try {

                    LineNumberReader in;
                    in = new LineNumberReader(new FileReader(eingabe[2]));
                    String line;

                    // Text einlesen und Häfigkeiten aller Wörter bestimmen:
                    final long timeStart = System.currentTimeMillis();
                    while ((line = in.readLine()) != null&&count<n) {
                        String[] wf = line.split("[^a-z^A-Z^ß^ä^ö^ü^Ä^Ö^Ü]+");
                        int ab = 0;
                        String d = "";
                        String e = "";
                        for (String w : wf) {
                            if (w.length() == 0 || w.length() == 1)
                                continue;
                            //System.out.println(w);
                            if(ab == 0){
                                 d = w;
                            } else {
                                 e = w;
                            }
                            ab++;
                        }
                        dict.insert(d,e);
                        count++;
                    }
                    final long timeEnd = System.currentTimeMillis();
                    System.out.println("Laufzeit " + (timeEnd - timeStart) + " Millisek.");
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            if(Input[0].equals("p")){
                for (Dictionary.Entry<String, String> e : dict) {
                    System.out.println(e.getKey() + ": " + e.getValue());
                }
            }
            //Search
            if (Input[0].equals("s")){
                String[] eingabe = command.split("[^a-z^A-Z^ß^ä^ö^ü^Ä^Ö^Ü]+");

                String d = eingabe[1];
                final long timeStart = System.currentTimeMillis();
                String a = dict.search(d);
                final long timeEnd = System.currentTimeMillis();
                System.out.println("Laufzeit " + (timeEnd - timeStart) + " Millisek.");
                System.out.println(a);
            }
            //searchall
            if (Input[0].equals("searchAll")){
                /**
                 * search for all keys
                 */
                final long timeStart = System.currentTimeMillis();
                for (Dictionary.Entry<String,String> e:dict) {
                    String a = dict.search(e.getKey());
                }
                final long timeEnd = System.currentTimeMillis();
                System.out.println("Laufzeit " + (timeEnd - timeStart) + " Millisek.");
            }
            //searchallE
            if (Input[0].equals("searchAllE")){
                /**
                 * search for all keys
                 */
                final long timeStart = System.currentTimeMillis();
                for (Dictionary.Entry<String,String> e:dict) {
                    String a = dict.search(e.getValue());
                }
                final long timeEnd = System.currentTimeMillis();
                System.out.println("Laufzeit " + (timeEnd - timeStart) + " Millisek.");
            }
            //Insert
            if (Input[0].equals("i")){

                String[] eingabe = command.split("[^a-z^A-Z^ß^ä^ö^ü^Ä^Ö^Ü]+");

                String d = eingabe[1];
                String e = eingabe[2];
                System.out.println(dict.insert(d,e));
            }

            //remove
            if (Input[0].equals("r")){
                String[] eingabe = command.split("[^a-z^A-Z^ß^ä^ö^ü^Ä^Ö^Ü]+");
                String d = eingabe[1];
                String a = dict.remove(d);
                System.out.println(a);
            }
            //exit
            if (Input[0].equals("exit")){
                System.exit(0);
            }
        }


    }
}
