package j;

import java.io.File;
import java.util.Scanner;

public class Main {
    private static int getDatasetId(File[] listOfFiles) {
        System.out.println("Starting...");
        final int numFiles = listOfFiles.length;
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
        } while (idx >= numFiles || idx < 0);

        return idx;
    }

    private static File[] getListOfFiles() {
        final File      folder      = new File("data/");
        final File[]    listOfFiles = folder.listFiles();
        return listOfFiles == null ? new File[0] : listOfFiles;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

	public static void main(String[] args) {
        final File[] listOfFiles = getListOfFiles();

        int idx = -1;

        if (args.length > 0) {
            idx = parseInt(args[0]);
        }

        if (idx < 0) idx = getDatasetId(listOfFiles);

		// Read the file
        final Interpreter in = new Interpreter(listOfFiles[idx]);
        
        // Create the instance of the problem
        final LinKernighan lk = new LinKernighan(in.getCoordinates(), in.getIds(), 0L);

        // Time keeping
		final long start = System.currentTimeMillis();
		
		// Show the results even if shutdown
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

