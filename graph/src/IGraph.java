import java.util.List;
import java.util.Set;

public interface IGraph {
    Set<Integer> listNodes();
    List<Pair> listEdges();
    List<Integer> listNeighbors(int x);
    int numberOfNodes();
    int numberOfEdges();
    int grad(int x);
    boolean areAdjacent(int x, int y);
    void insertEdge(Pair p);
    void insertEdge(int x, int y);
    void deleteEdge(Pair p);
    void deleteEdge(int x, int y);
    void deleteNode(int x);
    void contrEdge(Pair p);
}
