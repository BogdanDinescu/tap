import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IGraph {
    void loadFromFile(File file);
    Set<Integer> listNodes();
    List<Pair> listEdges();
    List<Integer> listNeighbors(int x);
    public List<Pair> listNeighborsWithWeight(int x);
    int numberOfNodes();
    int numberOfEdges();
    int grad(int x);
    boolean areAdjacent(int x, int y);
    void insertEdge(Pair p);
    void insertEdge(int x, int y);
    void insertEdge(int x, int y, int weight);
    void deleteEdge(Pair p);
    void deleteEdge(int x, int y);
    void deleteNode(int x);
    void contrEdge(Pair p);

    List<Integer> bfs(int s);
    List<Integer> dfs(int s);

    List<Collection<Integer>> algorithmA();

    List<Collection<Integer>> algorithmRA();

    List<Collection<Integer>> algorithmS();
}
