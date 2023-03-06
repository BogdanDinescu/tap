import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph(false);
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
        System.out.println(graph.grad(7) == 20);


        Graph g = new Graph(true);

        g.insertEdge(0, 1);
        g.insertEdge(0, 2);
        g.insertEdge(1, 2);
        g.insertEdge(2, 0);
        g.insertEdge(2, 3);
        g.insertEdge(3, 3);

        List<Integer> bfs = g.bfs(2);
        bfs.forEach(System.out::print);
        assert bfs.get(0) == 2 : "first bfs element is not 2";
        assert bfs.get(1) == 0 : "second bfs element is not 0";
        assert bfs.get(2) == 3 : "third bfs element is not 3";
        assert bfs.get(3) == 1 : "fourth bfs element is not 1";


        g = new Graph(false);
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
        System.out.println();
        g.lexBfs(8).forEach(System.out::println);


    }
}
