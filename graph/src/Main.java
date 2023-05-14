import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new FileReader("facebook_combined.txt"))) {

            String line = reader.readLine();

            while (line != null) {
                // read next line
                String[] number = line.split(" ");
                Pair p = new Pair(Integer.valueOf(number[0]), Integer.valueOf(number[1]));
                graph.insertEdge(p);
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Test1:");
        System.out.println(graph.grad(7) == 20);
        System.out.println("Test for number of edges");
        System.out.println(graph.numberOfEdges() == 88234);


        Graph g = new Graph(true, false);

        g.insertEdge(0, 1);
        g.insertEdge(0, 2);
        g.insertEdge(1, 2);
        g.insertEdge(2, 0);
        g.insertEdge(2, 3);
        g.insertEdge(3, 3);

        List<Integer> bfs = g.bfs(2);
        System.out.println("\nTest for bfs");
        bfs.forEach(System.out::print);
        assert bfs.get(0) == 2 : "first bfs element is not 2";
        assert bfs.get(1) == 0 : "second bfs element is not 0";
        assert bfs.get(2) == 3 : "third bfs element is not 3";
        assert bfs.get(3) == 1 : "fourth bfs element is not 1";

        List<Integer> dfs = g.dfs(0);
        System.out.println("\nTest for dfs");
        dfs.forEach(System.out::print);
        assert dfs.get(0) == 0 : "first bfs element is not 0";
        assert dfs.get(1) == 2 : "second bfs element is not 2";
        assert dfs.get(2) == 3 : "third bfs element is not 3";
        assert dfs.get(3) == 1 : "fourth bfs element is not 1";


        g = new Graph();
        g.insertEdge(1, 2);
        g.insertEdge(2, 5);
        g.insertEdge(5, 6);
        g.insertEdge(6, 1);
        g.insertEdge(2, 6);
        g.insertEdge(1, 5);
        g.insertEdge(6, 8);
        g.insertEdge(7, 8);
        g.insertEdge(3, 8);
        g.insertEdge(3, 4);
        g.insertEdge(4, 6);
        g.insertEdge(4, 8);
        g.insertEdge(3, 6);
        System.out.println("\nTest for lexBFS");
        g.lexBfs(8).forEach(System.out::println);


        g = new Graph(false, true);
        g.insertEdge(0, 1, 7);
        g.insertEdge(0, 2, 9);
        g.insertEdge(1, 2, 10);
        g.insertEdge(0, 5, 14);
        g.insertEdge(1, 3, 15);
        g.insertEdge(2, 5, 2);
        g.insertEdge(2, 3, 11);
        g.insertEdge(4, 5, 9);
        g.insertEdge(3, 4, 6);

        System.out.println("Test for disjktra");
        List<Integer> dist = g.disjktra(0);
        System.out.println(dist);
        System.out.println(dist.get(4).equals(20));


        g = new Graph(false, true);
        g.insertEdge(0, 1, 4);
        g.insertEdge(0, 2, 3);
        g.insertEdge(1, 5, 5);
        g.insertEdge(1, 4, 12);
        g.insertEdge(2, 4, 10);
        g.insertEdge(2, 3, 7);
        g.insertEdge(3, 4, 2);
        g.insertEdge(5, 6, 16);
        g.insertEdge(4, 6, 5);

        System.out.println("\nTest for A*");
        List<Integer> h = new ArrayList<>(g.numberOfNodes());
        h.add(14);
        h.add(12);
        h.add(11);
        h.add(6);
        h.add(4);
        h.add(11);
        h.add(Integer.MAX_VALUE);
        List<Integer> dist2 = g.aStar(0, 6, h);
        System.out.println(dist2);
        System.out.println(dist2.get(6).equals(17));

        g = new Graph(true, false);

        g.insertEdge(0, 1);
        g.insertEdge(0, 2);
        g.insertEdge(1, 2);
        g.insertEdge(2, 0);
        g.insertEdge(2, 3);
        g.insertEdge(3, 3);

        System.out.println("\nTest for paralel bfs");
        List<Integer> pbfs = g.pbfs(2);
        System.out.println(pbfs);
        assert bfs.get(0) == 2 : "first bfs element is not 2";
        assert bfs.get(1) == 0 : "second bfs element is not 0";
        assert bfs.get(2) == 3 : "third bfs element is not 3";
        assert bfs.get(3) == 1 : "fourth bfs element is not 1";

        System.out.println("\nTest for paralel dfs");
        List<Integer> pdfs = g.dfs(0);
        System.out.println(pdfs);
        assert dfs.get(0) == 0 : "first bfs element is not 0";
        assert dfs.get(1) == 2 : "second bfs element is not 2";
        assert dfs.get(2) == 3 : "third bfs element is not 3";
        assert dfs.get(3) == 1 : "fourth bfs element is not 1";

        g = new Graph(true, false);
        g.loadFromFile(new File("./graphExample.txt"));
        List<Collection<Integer>> components = null;


        System.out.println("\n Test for algorithm S");
        //components = g.algorithmS();
        //components.forEach(System.out::println);
        //System.out.println(testComponents(components));

        System.out.println("\n Test for algorithm A");
        components = g.algorithmA();
        components.forEach(System.out::println);
        System.out.println(testComponents(components));

        System.out.println("\n Test for algorithm RA");
        components = g.algorithmRA();
        components.forEach(System.out::println);
        System.out.println(testComponents(components));
        //writeToFile(new File("./out.txt"), components.get(0));
    }

    private static boolean testComponents(List<Collection<Integer>> components) {
        if (2 != components.size()) {
            return false;
        }
        boolean ok = false;
        boolean firstComponent = components.get(0).containsAll(List.of(0, 1, 2));
        boolean secondComponent = components.get(1).containsAll(List.of(3, 4));
        ok = firstComponent && secondComponent;
        if (!ok) {
            firstComponent = components.get(1).containsAll(List.of(0, 1, 2));
            secondComponent = components.get(0).containsAll(List.of(3, 4));
            ok = firstComponent && secondComponent;
        }
        return ok;
    }

    private static void writeToFile(File file, Collection<Integer> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            list.forEach(x -> {
                try {
                    bw.write(String.valueOf(x));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Cannot read file");
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot read file");
        }
    }
}
