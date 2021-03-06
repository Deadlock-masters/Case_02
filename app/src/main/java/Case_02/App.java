/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Case_02;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class App {

    public static final int NM = 3;

    /**
     * This method writes the configuration file for the pagination system
     * @param args the command line arguments
     */
    public static String generatePagination(int[] config) {
        // Here we create a file with the cocatenation of the size of the elements
        // the size of the whole number
        // the number of rows and the number of columns
        // and the type of path
        String fName= "output/"+config[0]+"_"+config[1]+"_"+config[2]+"_"+config[3]+"_"+config[4]+".txt";
        try {
            File f = new File(fName);
            if (f.createNewFile()) {
            } else {
            } 
            FileWriter fw = new FileWriter(f.getAbsoluteFile());

            int NR = NM*config[2]*config[3];

            int NP = NR * config[1]/config[0];

            try {
                fw.write(config[0]+"\n"+config[1]+"\n"
                        +config[2]+"\n"+config[3]+"\n"
                        +config[4]+"\n"+NP+"\n"+NR+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (config[4]==1) {
                for (int i = 0; i < config[3]; i++) {
                    for (int j = 0; j < config[2]; j++) {
                        for (int k = 0; k < NM; k++) {
                            String rep = coordenatesToString(i,j,k, config, NP, NR);

                            rep =  rep + "\n";
                            try {
                                fw.write(rep);
                            } catch (IOException e) {
                               e.printStackTrace();
                            }
                       }
                   } 
                }                
            }
            if (config[4]==2){
                for (int j = 0; j < config[3]; j++) {
                    for (int i = 0; i < config[2]; i++) {
                        for (int k = 0; k < NM; k++) {
                            String rep = coordenatesToString(i,j,k, config, NP, NR);
                            
                            rep =  rep + "\n";
                            try {
                                fw.write(rep);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } 
                }
            }

            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace(); }
        return fName;
    }
    /**
     * This method converts the coordinates of the page to a string
     * @param i
     * @param j
     * @param k
     * @param config
     * @param NP
     * @param NR
     * @return a string with correct format
     */
    private static String coordenatesToString(int i, int j, int k, int[] config, int NP, int NR) {
        String letter = "";
        int x = 0;

        if (k==0)
            { x = i*config[2]+j; letter = "A"; }
        else if (k==1)
            { x = i*config[2]+j+config[2]*config[3]; letter = "B"; }
        else
            { x = i*config[2]+j+config[2]*config[3]*2; letter = "C"; }
        int pageCols = config[0]/config[1];

        int page = x/pageCols;
       
        int row = (x%pageCols)*config[1];

        return letter+": "+"["+i+"-"+j+"]"+", "+page+", "+row;
    }

    /**
     * This method reads the configuration file and creates the pages
     * @param pageFrames the number of page frames
     * @param fileConfig A string with the location of the configuration file
     */
    public static int[] loadPagination(int pageFrames, String fileConfig) {
        ArrayList<Integer> coords = new ArrayList<>();
        int TP =-1;
        int TE = -1;
        int NF = -1;
        int NC = -1;
        int TR = -1;
        int NP = -1;
        int NR = -1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileConfig));
             TP = Integer.parseInt(br.readLine());
             TE = Integer.parseInt(br.readLine());
             NF = Integer.parseInt(br.readLine());
             NC = Integer.parseInt(br.readLine());
             TR = Integer.parseInt(br.readLine());
             NP = Integer.parseInt(br.readLine());
             NR = Integer.parseInt(br.readLine());

            while(br.ready()) {
                String temp = br.readLine();
                temp = temp.replace(" ", "");
                String [] tempArray = temp.split(",");
                coords.add(Integer.parseInt(tempArray[1]));
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConcurrentHashMap<Integer,Page> pagination = new ConcurrentHashMap<>(NP);
        if (NP==1)
        {
            pagination.put(0, new Page(0));
            pagination.put(1, new Page(1));
        }
        else {
            for (int i = 0; i < NP; i++) {
                pagination.put(i, new Page(i));
            }
        }
        // Usea  cyclic barrier to synchronize the threads we use only 23 threads
        // one for updating the pages and the other for use the aging algorithm
        CyclicBarrier barrier = new CyclicBarrier(2);
        
        // Update the pages
        UpdaterThread ut = new UpdaterThread(pagination, barrier, coords, pageFrames);
        ut.start();
        // Use the aging algorithm
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        int[] retArray = new int[9];

        retArray[0] = TP;
        retArray[1] = TE;
        retArray[2] = NF;
        retArray[3] = NC;
        retArray[4] = TR;
        retArray[5] = NP;
        retArray[6] = NR;
        retArray[7] = pageFrames;
        retArray[8] = ut.getFailures();
        
        return retArray;
        
    }

    private static void generateTestCases() {
        ArrayList<int[]> configPath = new ArrayList<>();
        ArrayList<Integer> configMP = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("config/configPath.txt"));
            while(br.ready()) {
                String temp = br.readLine();
                temp = temp.replace(" ", "");
                String [] tempArray = temp.split(",");
                int[] config = new int[tempArray.length];
                for (int i = 0; i < tempArray.length; i++) {
                    config[i] = Integer.parseInt(tempArray[i]);
                }
                configPath.add(config);
            }
            br.close();

            BufferedReader br2 = new BufferedReader(new FileReader("config/configMP.txt"));

            while(br2.ready()) {
                String temp = br2.readLine();
                configMP.add(Integer.parseInt(temp));
            }
            br2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter("output/results.csv"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<int[]> results = new ArrayList<>();
        try {
            bw.write("TP,TE,NF,NC,TR,NP,NR,MP,EP\n");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // get all the files in the folder output
        for (int i = 0; i < configPath.size(); i++) {
            String fileName = generatePagination(configPath.get(i));
            for (int j = 0; j < configMP.size(); j++) {
                System.out.println("Running test case: "+fileName+" with "+configMP.get(j)+" page frames");
                int[] res = loadPagination(configMP.get(j), fileName);
                results.add(res);
                try {
                    bw.write(res[0]+","+res[1]+","+res[2]+","+res[3]+","+res[4]+","+res[5]+","+res[6]+","+res[7]+","+res[8]+"\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        try {
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
    Scanner option = new Scanner(System.in);
    System.out.println("Welcome to the pagination system");
    System.out.println("Please select wich option you want to use \n 1. Generate a file "+
    "\n 2. Find the pagination errors \n 3. Test pagination \n 4. Generate the test cases"+
    "\n 5. Exit");
    
    // Make a switch to select the option
    int optionSelected = option.nextInt();

    option.nextLine();

    switch (optionSelected) {
        case 1: 
            System.out.println("Please enter the configuration for generating the file");
            System.out.println("The format should be : \n TP, TE, NF, NC, TR");
            System.out.println("TP = Total Pages \n TE = Total size of the whole number \n NF = Number of rows \n NC = Number of columns \n TR = Type of path");
            System.out.println("Example: 10, 10, 10, 10, 1");
            String configuration = option.nextLine();
            System.out.println("The configuration is: "+configuration);
            configuration = configuration.replaceAll(" ", "");
            String[] configurationArray = configuration.split(",");
            if (configurationArray.length != 5) {
                System.out.println("The configuration is not valid! \n Do you want to try again? \n 1. Yes \n 2. No");
                int optionSelected2 = option.nextInt();
                if (optionSelected2 == 1) {
                    main(args);
                } else {
                    System.out.println("Goodbye!");
                    break;
                }
            }
            // Convert the array of strings to ints

            int[] configurationInt = new int[configurationArray.length];
            for (int i = 0; i < configurationInt.length; i++) {
                configurationInt[i] = Integer.parseInt(configurationArray[i]);
            }

            generatePagination(configurationInt);
            break;
        case 2:
            System.out.println("Please enter the file name for the pagination system and the number of page frames");
            System.out.println("Example: pagination.txt, 10");
            String request = option.nextLine();
            request = request.replaceAll(" ", "");
            String[] req = request.split(",");
            int nPagninationErrors = loadPagination(Integer.parseInt(req[1]), "output/"+req[0])[7];
            System.out.println("File selected "+req[0]);
            System.out.println("Page frames are "+req[1]);
            System.out.println("PaginationErrors"+nPagninationErrors);

            break;
        case 3:
            System.out.println("Tesing pagination");
            int errors = loadPagination(3, "output/8_2_4_4_1.txt")[7];

            System.out.println("The number of errors is: "+errors);
            break;
        
        case 4:
            System.out.println("Generating the test cases");
            generateTestCases();
            break;
            
        case 5:
            System.out.println("Exiting the program");
            break;
        }
    option.close();
    }
}
