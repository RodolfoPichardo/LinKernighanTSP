public class Main{

	public static void main(String[] args) {
		String file = "data/tsp.tsp"; 
		System.out.println("Starting...");

       
        // Read the file
		Interpreter in = new Interpreter(file);
        
        // Create the instance of the problem
        LinKernighan lk = new LinKernighan(in.getCoordinates(), in.getIds());
 
        // Time keeping
		long start;
		start = System.currentTimeMillis();
               
        // Optimize the problem
        lk.runAlgorithm();

        System.out.printf("The solution took: %dms\n", System.currentTimeMillis()-start);
        System.out.println("The solution is: ");
        System.out.println(lk);
	}
}


