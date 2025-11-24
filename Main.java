
// Expensive Subway problem (P2-part A)
// Startup code given in the Fall 2025 for csi2110/csi2510
// This file only contains basic commands to read the data from the input files.
// Use and modify it freely
//
// Do not forget to add your name and student number on each program file you submit
//

import java.util.Scanner;
import java.util.ArrayList;


class Main {

    static void main (String[] args) {


      Scanner scanner = new Scanner(System.in); // Note that for Online Judge the input must be given in the standard I/O
            // to read from input files use the command 'java Main < test1.txt', test1.txt should be in the same folder
                                

      int nv, ne;


      while (true) { // This loop repeats for each problem that is included in the same input file (until 0 0 is read)

      	nv=scanner.nextInt(); // read number of vertices
      	ne=scanner.nextInt(); // read number of edges
        // System.out.println(nv +" " + ne);
        if ((nv==0) & (ne==0)) break; // if both are zero it is indication to stop 
        

        ArrayList<String> vertexNames=new ArrayList<String>(); // reading vertex names into an Array List
        for (int v=0; v<nv; v++) {
            String vName = scanner.next(); // here a vertex name is read
            vertexNames.add(vName);  
            // System.out.println(vName);
      }

        // you should do something here to add the vertices to the graph

      for (int e=0; e<ne; e++) {
          String v1Name = scanner.next(); // a vertex name that is one end of the edge
          String v2Name = scanner.next(); // a vertex name that is the other end of the edge
          int weight=scanner.nextInt(); // edge weight is given
          // System.out.println(v1Name+" "+v2Name+" "+weight);
          // here you have a new edge information: {v1Name,v2Name} with weight 'weight'
          // you should do something here in order to add this edge to the graph
      }

        String home=scanner.next(); // here is the name of the vertex of Peter's home
        // System.out.println(home);

        // Below is a silly printout that no matter the input give the answer "Impossible"
        // You should substitute this to call your algorithms/methods that will produce the correct answer

        System.out.println("Impossible");
        
      }


  }

}



