import java.util.ArrayList;

public class Main{

	public static void main(String[] args) {
		String file = "tsp.tsp"; 
		System.out.println("Starting...");

       
        // Read the file
		Interpreter in = new Interpreter(file);
        
        // Create the instance of the problem
        LinKernighan lk = new LinKernighan(in.getCoordinates(), in.getIds());
 
        // Time keeping
		long start;
		start = System.currentTimeMillis();
        
		/*
        // Optimize the problem
		int[] sampleTour = new int[]{0,1,2,3,4,5,6};
		ArrayList<Edge> e = lk.deriveEdgesFromTour(sampleTour);
		System.out.print("edges = ");
		for(Edge edge: e) {
			System.out.print(edge + ",");
		}
		System.out.println();
		
		ArrayList<Integer> t = new ArrayList<Integer>();
		t.add(-1);
		t.add(3);
		t.add(4);
		t.add(6);
		lk.tour = sampleTour;
		
		e = lk.deriveX(t);
		for(Edge edge: e) {
			System.out.print(edge + ",");
		}
		System.out.println();
		

		e = lk.deriveY(t);
		for(Edge edge: e) {
			System.out.print(edge + ",");
		}
		System.out.println();
		
		
		
		lk.constructNewTour(sampleTour, t);
		
		
		return;*/
        lk.runAlgorithm();

        System.out.printf("The solution took: %dms\n", System.currentTimeMillis()-start);
        System.out.println("The solution is: ");
        System.out.println(lk);
	}
}

