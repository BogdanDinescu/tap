import java.util.*;

public class Graph implements IGraph {
    private final HashMap<Integer, HashSet<Integer>> adjacencyList;
    private final boolean oriented;

    public Graph(boolean oriented) {
        this.oriented = oriented;
        this.adjacencyList = new HashMap<>();
    }
    @Override
    public Set<Integer> listNodes() {
        return adjacencyList.keySet();
    }
    @Override
    public List<Pair> listEdges() {
        List<Pair> result = new ArrayList<>();
        if (oriented) {
            adjacencyList.forEach((x, hashSet) -> hashSet.forEach(y -> result.add(new Pair(x, y))));
        } else {
            adjacencyList.forEach((x, hashSet) -> hashSet.forEach(y -> {
                if (x < y) {
                    result.add(new Pair(x, y));
                }
            }));
        }
        return result;
    }
    @Override
    public List<Integer> listNeighbors(int x) {
        return adjacencyList.get(x).stream().toList();
    }
    @Override
    public int numberOfNodes() {
        return adjacencyList.size();
    }
    @Override
    public int numberOfEdges() {
        return listEdges().size();
    }
    @Override
    public int grad(int x) {
        return adjacencyList.get(x).size();
    }
    @Override
    public boolean areAdjacent(int x, int y) {
        return adjacencyList.get(x).contains(y);
    }
    @Override
    public void insertEdge(Pair pair) {
        insertEdge(pair.getX(), pair.getY());
    }

    @Override
    public void insertEdge(int x, int y) {
        if (oriented) {
            adjacencyList.computeIfAbsent(x, k -> new HashSet<>());
            adjacencyList.get(x).add(y);
        } else {
            adjacencyList.computeIfAbsent(x, k -> new HashSet<>());
            adjacencyList.computeIfAbsent(y, k -> new HashSet<>());
            adjacencyList.get(x).add(y);
            adjacencyList.get(y).add(x);
        }
    }

    @Override
    public void deleteEdge(Pair pair) {
        this.deleteEdge(pair.getX(), pair.getY());
    }

    @Override
    public void deleteEdge(int x, int y) {
        deleteNode(x);
        deleteNode(y);
    }

    @Override
    public void deleteNode(int n) {
        List<Integer> integers = this.listNeighbors(n);
        integers.forEach(i -> adjacencyList.get(i).remove(n));
        adjacencyList.remove(n);
    }
    @Override
    public void contrEdge(Pair p) {
        List<Integer> neighborsOfY = this.listNeighbors(p.getY());
        List<Integer> neighborsOfX = this.listNeighbors(p.getX());
        Set<Integer> neighbors = new HashSet<>();
        neighbors.addAll(neighborsOfX);
        neighbors.addAll(neighborsOfY);
        neighbors.remove(p.getX());
        this.deleteNode(p.getX());
        System.out.println(neighbors);
    }
    public List<Integer> bfs(int s) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        LinkedList<Integer> queue = new LinkedList<>();

        visited.add(s);
        queue.add(s);

        while (queue.size() != 0) {

            s = queue.poll();
            result.add(s);

            listNeighbors(s).forEach(n -> {
                if (!visited.contains(n)) {
                    visited.add(n);
                    queue.add(n);
                }
            });

        }
        return result;
    }

    public List<Set<Integer>> refine(List<Set<Integer>> list, Set<Integer> u) {
        List<Set<Integer>> result = new LinkedList<Set<Integer>>();
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();
        list.forEach(s -> {
            s.forEach(v -> {
                u.forEach(u1 -> {
                    if (u1.equals(v)) {
                        s1.add(v);
                    }
                });
            });
            if (list.size() > 1 && list.get(1).isEmpty()) {
                s1.addAll(u);
            }
            if (s.containsAll(u)) {
                s2.addAll(s);
                s2.removeAll(u);
            }
        });
        result.add(s1);
        result.add(s2);

        return result;
    }

    private List<Set<Integer>> separateListOnSets(List<Integer> vertices, Integer s) {
        List<Set<Integer>> list = new LinkedList<Set<Integer>>();
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();

        for (Integer v : vertices) {
            if (v.equals(s)) {
                s1.add(v);
                s2.add(v);
            } else {
                s2.add(v);
            }
        }

        list.add(s1);
        list.add(s2);
        return list;
    }

    public List<Integer> lexBfs(int s) {
        List<Integer> result = new ArrayList<>();
        List<Set<Integer>> list = separateListOnSets(listNodes().stream().toList(), s);
        Set<Integer> visited = new HashSet<>();

        for (int i = 0; i < numberOfNodes(); i++) {
            Set<Integer> nodes = list.get(0);
            nodes.forEach(node -> {
                visited.add(node);
                result.add(node);
            });
            if (result.size() == numberOfNodes()) {
                break;
            }
            list = refine(list, nodes);
            Set<Integer> unvisited = new HashSet<>();
            nodes.forEach(node -> {
                listNeighbors(node).forEach(neighbor -> {
                    if (!visited.contains(neighbor)) {
                        unvisited.add(neighbor);
                    }
                });
            });
            list = refine(list, unvisited);
        }

        return result;
    }
}