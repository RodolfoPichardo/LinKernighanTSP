package j;

import java.io.File;
import java.util.Scanner;

public class Main{

	public static void main(String[] args) {
    
		System.out.println("Starting...");
		
		File folder = new File("data/");
        File[] listOfFiles = folder.listFiles();
        final int numFiles = listOfFiles == null ? 0 : listOfFiles.length;

        for (int i = 0; i < numFiles; i++) {
        	String name = listOfFiles[i].getName();
        	if (listOfFiles[i].isFile() && name.substring(name.length() - 3).equalsIgnoreCase("tsp")) {
        		System.out.println("  [" + i + "] " + listOfFiles[i].getName());
            }
        }
        
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
        
        int idx;
        do {
	        System.out.print("Select the dataset to test: ");
	        idx = scanner.nextInt();
        } while(idx >= numFiles || idx < 0);

        
		// Read the file
		Interpreter in = new Interpreter(listOfFiles[idx]);
        
        // Create the instance of the problem
        LinKernighan lk = new LinKernighan(in.getCoordinates(), in.getIds());
        
        // Time keeping
		long start;
		start = System.currentTimeMillis();
		
		// Shpw the results even if shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	System.out.printf("The solution took: %dms\n", System.currentTimeMillis()-start);
                System.out.println("The solution is: ");
                System.out.println(lk);
            }
         });
		
		lk.run();

        
	}
}

