import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    }
}
