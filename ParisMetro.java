// Paris (P2- Part B)
// Startup code given in the Fall 2025 for csi2110/csi2510
// This file only contains basic commands to read the data from the input file 'metro.txt'
// Use and modify it freely
//
// Do not forget to add your name and student number on each program file you submit
//Name : Abigail Kim
//Student number : 300422108

import java.io.*;
import java.nio.channels.Pipe.SourceChannel;
import java.util.*;
import javax.sound.sampled.SourceDataLine;

public class ParisMetro {
    int n, m;
    HashMap<Integer, Integer> idToIndex = new HashMap<>();
    ArrayList<String> indexName = new ArrayList<>();
    ArrayList<ArrayList<Edge>> adj = new ArrayList<>();
    ArrayList<int[]> walkEdges = new ArrayList<>();
    HashMap<Long, Integer> bestSeg = new HashMap<>();
    ArrayList<ArrayList<Integer>> hubMems = new ArrayList<>();
    ArrayList<String> hubNames = new ArrayList<>();
    int[] vertexToHub;
    int hubCount = 0;
    ArrayList<EdgeHub> hubEdges = new ArrayList<>();
    ArrayList<EdgeHub> mst = new ArrayList<>();
    int totalCost = 0;


    public static void main (String[] args) {
        ParisMetro metro = new ParisMetro();
        metro.loadInput();
        metro.buildHubs();
        metro.computeSegments();
        metro.buildMST();
        metro.printResults();
    }

    void loadInput(){
        System.out.println("\nStart Reading Metro");
        Scanner scan = new Scanner(System.in);
        n = scan.nextInt(); // number of vertices
        m = scan.nextInt(); // number of edges
        System.out.println("Paris Metro Graph has "+n+" vertices and "+m+" edges.");
        scan.nextLine(); // new line

        //vertices
        for (int i=0; i <n; i++){
            if (!scan.hasNextLine()){
                break;
            }
            String vertexBefore = scan.next().trim();//vertex as string
            if (vertexBefore.equals("$")){
                break;
            }
            int vertexNumber = Integer.parseInt(vertexBefore); // vertex number
            
            String stationName = scan.nextLine().trim();

            //System.out.println(vertexNumber+" "+stationName);

            idToIndex.put(vertexNumber, indexName.size());
            indexName.add(stationName);
            adj.add(new ArrayList<>());
        }

        n=indexName.size();

        while (scan.hasNextLine()){
            String l = scan.nextLine().trim();
            if (l.equals("$")){
                break;
            }
        }

        for (int i=0; i <m; i++){
            if (!scan.hasNextInt()){
                break;
            }
            int v1=scan.nextInt();
            if (!scan.hasNextInt()){
                break;
            }
            int v2=scan.nextInt();
            if (!scan.hasNextInt()){
                break;
            }
            int weight=scan.nextInt(); // edge information

            //System.out.println("v1="+v1+" v2="+v2+" weight="+weight);
            Integer u = idToIndex.get(v1);
            Integer v = idToIndex.get(v2);
            if (u == null || v == null) continue;

            adj.get(u).add(new Edge(v,weight));
            if (weight==-1){
                walkEdges.add(new int[]{u,v});
                walkEdges.add(new int[]{v,u});
            }
        }
        System.out.println("End Reading Metro\n");
    }

    void buildHubs(){
        int[] degree = new int[n];
        for (int i = 0; i < n; i++) {
            degree[i] = adj.get(i).size();
        }
    

        UnionFind uf = new UnionFind(n);
        for (int[] e : walkEdges){
            int a = e[0];
            int b = e[1];
            uf.union(a,b);
        }

        HashMap<Integer,Integer> hubIdx = new HashMap<>();
        for (int i = 0; i < n; i++){
            if (degree[i] >= 3){
                int root = uf.find(i);
                if (!hubIdx.containsKey(root)){
                    hubIdx.put(root, hubIdx.size());
                }
            }
        }

        hubCount = hubIdx.size();
        hubMems.clear();
        hubNames.clear();

        for (int i=0; i<hubCount; i++){
            hubMems.add(new ArrayList<>());
        }

        HashMap<Integer,Integer> rootToFinalIdx = new HashMap<>();
        int idx = 0;
        for (Integer root : hubIdx.keySet()){
            rootToFinalIdx.put(root, idx++);
        }
        hubCount = idx;

        for (int v = 0; v < n; v++){
            int root = uf.find(v);
            if (rootToFinalIdx.containsKey(root)){
                int hid = rootToFinalIdx.get(root);
                hubMems.get(hid).add(v);
            }
        }
        vertexToHub = new int[n];

        
        Arrays.fill(vertexToHub, -1);
        for (int h = 0; h < hubCount; h++){
            int repr = hubMems.get(h).get(0);
            hubNames.add(indexName.get(repr));
            for (int v : hubMems.get(h)){
                vertexToHub[v] = h;
            }
        }

        System.out.println("DEBUG: Hub sizes (index:size, repr name):");
        for (int h = 0; h < hubCount; h++){
            int repr = hubMems.get(h).get(0);
            System.out.println("  " + h + ":" + hubMems.get(h).size() + " -> " + indexName.get(repr));
        }

        int totalHubVertices = 0;
        for (int v = 0; v < n; v++){
            if (vertexToHub[v] != -1) totalHubVertices++;
        }

        System.out.println("Hub Stations = "+ hubNames);
        System.out.println("Number of Hub Stations = " + hubCount+" (total Hub Vertices = " + totalHubVertices + ")");
    }

    void computeSegments(){
        bestSeg.clear();
        for (int hub1 = 0; hub1<hubCount; hub1++){
            for (int s : hubMems.get(hub1)){
                dijkstra(s, hub1);
            }
        }
        System.out.println("Number of Possible Segments = " + bestSeg.size());
    }

    void dijkstra(int s, int hubA) {
        int n = adj.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE/4);
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[1]));
        dist[s] = 0;
        pq.add(new int[]{s,0});

        while(!pq.isEmpty()){
            int[] current = pq.poll();
            int u = current[0];
            int du = current[1];
            if (du>dist[u]){
                continue;
            }
            int hubU = vertexToHub[u];

            if (hubU!= -1 && hubU!= hubA){
                long key = ((long) Math.min(hubA, hubU)<<32) | Math.max(hubA, hubU);
                bestSeg.put(key, Math.min(bestSeg.getOrDefault(key, Integer.MAX_VALUE), du));
                continue;
            }
            for (Edge e: adj.get(u)){
                int v = e.v;
                int w = e.w;
                if (w<=0){
                    w=1;
                }
                int nd = du+w;
                if (nd< dist[v]){
                    dist[v] = nd;
                    pq.add(new int[]{v,nd});
                }
            }
        }
    }

    void buildMST(){
        ArrayList<EdgeHub> hubEdges = new ArrayList<>();
        for (Long key : bestSeg.keySet()){
            int a = (int) (key>>32);
            int b=(int) (key & 0xffffffff);
            hubEdges.add(new EdgeHub(a,b,bestSeg.get(key)));
        }

        Collections.sort(hubEdges);
        UnionFind ufHub = new UnionFind (hubCount);
        mst = new ArrayList<>();
        totalCost = 0;

        for (EdgeHub e : hubEdges){
            if (ufHub.find(e.u)!= ufHub.find(e.v)){
                ufHub.union(e.u, e.v);
                mst.add(e);
                totalCost+=e.w;
            }
        }
    }

    void printResults(){
        System.out.println("Total Cost = $" + totalCost);
        System.out.println("Segments to Buy:");
        int count =1;
        for (EdgeHub e: mst){
            System.out.println(count++ + "(" + hubNames.get(e.u) + " - " + hubNames.get(e.v) + ") - $" + e.w);
        }
    }

static class Edge{
    int v;
    int w;
    Edge (int v, int w){
        this.v = v;
        this.w = w;
    }
}

static class EdgeHub implements Comparable<EdgeHub>{
    int u;
    int v;
    int w;
    EdgeHub(int u, int v, int w){
        this.u = u;
        this.v = v;
        this.w=w;
    }
    public int compareTo(EdgeHub other){
        return Integer.compare(this.w, other.w);
    }
}

static class UnionFind{
    int[] parent;
    int[] rank;
    UnionFind(int n){
        parent = new int[n];
        rank = new int[n];
        for (int i=0; i<n; i++){
            parent[i] =i;
        }
    }
    int find (int x){
        return parent[x] ==x? x: (parent[x] = find(parent[x]));
    }

    void union(int x, int y){
        x = find(x);
        y = find(y);
        if (x==y) return;
        if (rank[x] <rank[y]){
            parent[x] = y;
        }
        else if (rank[x]>rank[y]){
            parent[y] = x;
        }
        else{
            parent[y] = x;
            rank[x]++;
        }
    }
    
}
}
