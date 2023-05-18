import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Graph implements IGraph {
    private final HashMap<Integer, HashSet<Pair>> adjacencyList;
    private final HashSet<Integer> nodeList;
    private final boolean oriented;
    private final boolean weighted;
    private int numberOfEdges;

    public Graph(boolean oriented, boolean weighted) {
        this.oriented = oriented;
        this.weighted = weighted;
        this.adjacencyList = new HashMap<>();
        this.nodeList = new HashSet<>();
    }

    public Graph() {
        this(false, false);
    }

    @Override
    public void loadFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // read first line
            String line = br.readLine();
            String[] nm = line.split(" ");
            int n = Integer.parseInt(nm[0]);
            int m = Integer.parseInt(nm[1]);

            for (int i = 0; i < m; i++){
                line = br.readLine();
                if (line != null){
                    String[] s = line.split(" ");
                    if (this.weighted) {
                        this.insertEdge(Integer.parseInt(s[0]),
                                Integer.parseInt(s[1]),
                                Integer.parseInt(s[2]));
                    } else {
                        this.insertEdge(Integer.parseInt(s[0]),
                                Integer.parseInt(s[1]));
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot read file");
        }
    }

    @Override
    public Set<Integer> listNodes() {
        return nodeList;
    }

    @Override
    public List<Pair> listEdges() {
        List<Pair> result = new ArrayList<>();
        if (oriented) {
            adjacencyList.forEach((x, hashSet) -> hashSet.forEach(p -> result.add(new Pair(x, p.getA()))));
        } else {
            adjacencyList.forEach((x, hashSet) -> hashSet.forEach(p -> {
                if (x < p.getA()) {
                    result.add(new Pair(x, p.getA()));
                }
            }));
        }
        return result;
    }

    @Override
    public List<Integer> listNeighbors(int x) {
        return adjacencyList.get(x).stream().map(Pair::getA).toList();
    }

    @Override
    public List<Pair> listNeighborsWithWeight(int x) {
        if (!weighted)
            throw new RuntimeException("Graph not weighted");
        return adjacencyList.get(x).stream().toList();
    }

    @Override
    public int numberOfNodes() {
        return nodeList.size();
    }

    @Override
    public int numberOfEdges() {
        return numberOfEdges;
    }

    @Override
    public int grad(int x) {
        return adjacencyList.get(x).size();
    }

    @Override
    public boolean areAdjacent(int x, int y) {
        return listNeighbors(x).contains(y);
    }

    @Override
    public void insertEdge(Pair pair) {
        insertEdge(pair.getA(), pair.getB());
    }

    @Override
    public void insertEdge(int x, int y, int weight) {
        adjacencyList.computeIfAbsent(x, k -> new HashSet<>());
        adjacencyList.get(x).add(new Pair(y, weight));
        if (!oriented) {
            adjacencyList.computeIfAbsent(y, k -> new HashSet<>());
            adjacencyList.get(y).add(new Pair(x,weight));
        }
        nodeList.add(x);
        nodeList.add(y);
        numberOfEdges++;
    }

    @Override
    public void insertEdge(int x, int y) {
        if (weighted)
            throw new RuntimeException("Weight omitted!");
        nodeList.add(x);
        nodeList.add(y);
        insertEdge(x, y, 0);
    }

    @Override
    public void deleteEdge(Pair pair) {
        this.deleteEdge(pair.getA(), pair.getB());
    }

    @Override
    public void deleteEdge(int x, int y) {
        deleteNode(x);
        deleteNode(y);
        numberOfEdges--;
    }

    @Override
    public void deleteNode(int n) {
        List<Integer> integers = this.listNeighbors(n);
        integers.forEach(i -> {
            if (adjacencyList.get(i) != null) {
                adjacencyList.get(i).removeIf(p -> p.getA().equals(n));
            }
        });
        nodeList.remove(n);
        adjacencyList.remove(n);
    }

    @Override
    public void contrEdge(Pair p) {
        List<Integer> neighborsOfY = this.listNeighbors(p.getB());
        List<Integer> neighborsOfX = this.listNeighbors(p.getA());
        Set<Integer> neighbors = new HashSet<>();
        neighbors.addAll(neighborsOfX);
        neighbors.addAll(neighborsOfY);
        neighbors.remove(p.getA());
        this.deleteNode(p.getA());
        nodeList.remove(p.getA());
        System.out.println(neighbors);
    }

    @Override
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

    public List<Integer> dfs(int s) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();

        visited.add(s);
        stack.add(s);

        while (!stack.isEmpty()) {

            s = stack.pop();
            result.add(s);

            listNeighbors(s).forEach(n -> {
                if (!visited.contains(n)) {
                    visited.add(n);
                    stack.add(n);
                }
            });
        }

        return result;
    }

    public List<Set<Integer>> refine(List<Set<Integer>> list, Set<Integer> u) {
        List<Set<Integer>> result = new LinkedList<>();
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
        List<Set<Integer>> list = new LinkedList<>();
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

    public List<Integer> disjktra(int s) {
        if (!weighted)
            throw new RuntimeException("Graph not weighted");

        // init
        List<Integer> dist = new ArrayList<>(this.numberOfNodes());
        PriorityQueue<Pair> queue = new PriorityQueue<>();
        IntStream.range(0, this.numberOfNodes()).forEach(x -> dist.add(Integer.MAX_VALUE));
        dist.set(0, s);
        queue.add(new Pair(dist.get(s), s));

        while (!queue.isEmpty()) {
            Integer x = queue.poll().getB();
            listNeighborsWithWeight(x).forEach(p -> {
                Integer y = p.getA();
                Integer w = p.getB();
                int newDist = dist.get(x) + w;
                if (dist.get(y) > newDist) {
                    queue.remove(new Pair(dist.get(y), y));
                    dist.set(y, newDist);
                    queue.add(new Pair(dist.get(y), y));
                }
            });
        }

        return dist;
    }

    private boolean isInParents(Map<Integer, Integer> parents, Integer y) {
        int currentNode = y;
        while (true) {
            Integer parent = parents.get(currentNode);
            if (parent == null) break;
            if (y.equals(parent)) {
                return true;
            }
            currentNode = parent;
        }
        return false;
    }

    public List<Integer> aStar(int start, int end, List<Integer> h) {
        if (!weighted)
            throw new RuntimeException("Graph not weighted");

        // init
        Queue<Tuple> open = new PriorityQueue<>(Comparator.comparingInt(Tuple::getF));
        LinkedList<Integer> closed = new LinkedList<>();
        List<Integer> g = new ArrayList<>(this.numberOfNodes());
        List<Integer> f = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(x -> g.add(0));
        f.addAll(h);
        Map<Integer, Integer> parents = new HashMap<>(this.numberOfNodes());
        open.add(new Tuple(start, g.get(start), f.get(start)));

        while (!open.isEmpty()) {
            Integer node = open.poll().getN();
            closed.add(node);

            if (node.equals(end)) {
                break;
            }

            listNeighborsWithWeight(node).forEach(p -> {
                Integer y = p.getA();
                Integer w = p.getB();

                if (!isInParents(parents, y)) {
                    int g_suc = g.get(node) + w;
                    int f_suc = g_suc + h.get(node);

                    if (closed.contains(y)) {
                        if (f_suc < f.get(y)) {
                            closed.removeIf(x -> x.equals(y));
                            parents.put(y, node);
                            g.set(y, g_suc);
                            f.set(y, f_suc);
                            open.add(new Tuple(y, g_suc, f_suc));
                        }
                    } else {
                        if (open.stream().anyMatch(t -> t.getN().equals(y))) {
                            if (f_suc < f.get(y)) {
                                open.removeIf(t -> t.getN().equals(y));
                                parents.put(y, node);
                                g.set(y, g_suc);
                                f.set(y, f_suc);
                                open.add(new Tuple(y, g_suc, f_suc));
                            }
                        } else {
                            open.add(new Tuple(y, g_suc, f_suc));
                            g.set(y, g_suc);
                            f.set(y, f_suc);
                        }
                    }
                }
            });
        }
        return g;
    }


    private static class inParallelWhile implements Runnable {

        private final Graph graph;
        private final LinkedList<Integer> cq;
        private final LinkedList<Integer> nq;
        private final List<AtomicBoolean> visited;
        private final List<Integer> parents;

        public inParallelWhile(Graph graph, LinkedList<Integer> cq, LinkedList<Integer> nq, List<AtomicBoolean> visited, List<Integer> parents) {
            this.graph = graph;
            this.cq = cq;
            this.nq = nq;
            this.visited = visited;
            this.parents = parents;
        }

        @Override
        public void run() {
            Integer u = cq.poll();
            if (u != null) {
                graph.listNeighbors(u).forEach(v -> {
                    AtomicBoolean a = visited.get(v);
                    if (!a.get()) {
                        if (!a.getAndSet(true)) {
                            parents.set(v, u);
                            nq.add(v);
                        }
                    }
                });
            }
        }
    }

    public List<Integer> pbfs(int s) {
        List<Integer> parents = new ArrayList<>(this.numberOfNodes());
        List<AtomicBoolean> visited = new ArrayList<>(this.numberOfNodes());
        LinkedList<Integer> cq = new LinkedList<>();
        LinkedList<Integer> nq = new LinkedList<>();
        IntStream.range(0, this.numberOfNodes()).parallel().forEach(x -> parents.add(Integer.MAX_VALUE));
        IntStream.range(0, this.numberOfNodes()).parallel().forEach(x -> visited.add(new AtomicBoolean(false)));
        List<Thread> threads = new LinkedList<>();

        parents.set(s, Integer.MAX_VALUE);
        visited.get(s).getAndSet(true);
        cq.add(s);

        while (cq.size() != 0) {
            nq.clear();
            while (cq.size() != 0) {
                Thread thread = new Thread(new inParallelWhile(this, cq, nq, visited, parents));
                threads.add(thread);
                thread.start();
            }
            // synchronize
            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            // swap
            cq.addAll(nq);
            nq.clear();
        }
        // create result from parent tree
        List<Integer> result = new ArrayList<>(this.numberOfNodes());
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(s);
        result.add(s);
        while (queue.size() != 0) {
            Integer c = queue.poll();
            for (int i = 0; i < parents.size(); i++) {
                Integer p = parents.get(i);
                if (Objects.equals(p, c)) {
                    result.add(i);
                    queue.add(i);
                }
            }
        }
        return result;
    }

    private void isolate(int v) {
        if (adjacencyList.values().stream().flatMap(Set::stream).anyMatch(x -> x.getA().equals(v))) {
            adjacencyList.keySet().stream().parallel().forEach(x -> {
                adjacencyList.get(x).removeIf(p -> p.getA().equals(v));
            });
        }
    }

    private void pdfsRecursive(int v, List<Integer> l) {
        l.add(v);
        isolate(v);
        while (grad(v) > 0) {
            Optional<Pair> first = adjacencyList.get(v).stream().findFirst();
            if (first.isPresent()) {
                Integer u = first.get().getA();
                pdfsRecursive(u, l);
            }
        }
    }

    public List<Integer> pdfs(int s) {
        LinkedList<Integer> l = new LinkedList<>();
        //makeDirected();
        pdfsRecursive(s, l);
        return l;
    }

    private boolean directConnect(List<Integer> p, List<Integer> o) {
        AtomicBoolean change = new AtomicBoolean(false);
        this.listEdges().stream().parallel().forEach(pair -> {
            Integer v = pair.getA();
            Integer w = pair.getB();
            o.set(v, p.get(v));
            Integer parent = o.get(v);
            if (v > w) {
                parent = Math.min(parent, w);
            }
            p.set(v, parent);
            if (!o.get(v).equals(p.get(v))) {
                change.set(true);
            }
        });
        return change.get();
    }


    private boolean parentConnect(List<Integer> p, List<Integer> o) {
        AtomicBoolean change = new AtomicBoolean(false);
        listNodes().stream().parallel().forEach(v -> {
            o.set(v, p.get(v));
        });

        listEdges().stream().parallel().forEach(pair -> {
            Integer v = pair.getA();
            Integer w = pair.getB();
            if (o.get(v) > o.get(w)) {
                synchronized (p) {
                    p.set(o.get(v), Math.min(p.get(o.get(v)), o.get(w)));
                }
            }
        });
        return change.get();
    }

    private boolean directRootConnect(List<Integer> p, List<Integer> o) {
        AtomicBoolean change = new AtomicBoolean(false);
        this.listEdges().stream().parallel().forEach(pair -> {
            Integer v = pair.getA();
            Integer w = pair.getB();
            if (p.get(v).equals(v)) {
                int parent = v;
                if (v > w) {
                    parent = Math.min(parent, w);
                }
                p.set(v, parent);
                if (!p.get(v).equals(v)) {
                    change.set(true);
                }
            }
        });
        return change.get();
    }

    private boolean shortcut(List<Integer> p, List<Integer> o) {
        AtomicBoolean change = new AtomicBoolean(false);
        listNodes().stream().parallel().forEach(v -> {
            o.set(v, p.get(v));
        });
        listNodes().stream().parallel().forEach(v -> {
            p.set(v, o.get(o.get(v)));
            if (!p.get(v).equals(o.get(v))) {
                change.set(true);
            }
        });
        return change.get();
    }

    private boolean alter(List<Integer> parents) {
        AtomicBoolean change = new AtomicBoolean(false);
        listEdges().stream().parallel().forEach(pair -> {
            Integer v = pair.getA();
            Integer w = pair.getB();
            //if (!parents.get(v).equals(parents.get(w))) {
            //    synchronized ()
            //}
        });
        return change.get();
    }

    private List<Collection<Integer>> parentsToComponents(List<Integer> parents) {
        System.out.println(parents);
        HashSet<Integer> colors = new HashSet<>(parents);
        List<Collection<Integer>> response = new ArrayList<>(colors.size());
        colors.forEach(c -> {
            Set<Integer> component = new HashSet<>();
            for (int i = 0; i < parents.size(); i++) {
                if (Objects.equals(parents.get(i), c)) {
                    component.add(i);
                }
            }
            response.add(component);
        });
        return response;
    }

    @Override
    public List<Collection<Integer>> algorithmS() {
        List<Integer> p = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(p::add);
        List<Integer> o = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(o::add);

        boolean parentsChange;
        boolean shortcutChange;

        do {
            parentsChange = parentConnect(p, o);
            do {
                shortcutChange = shortcut(p, o);
            } while (shortcutChange);
        } while (parentsChange);

        return parentsToComponents(p);
    }

    @Override
    public List<Collection<Integer>> algorithmA() {
        List<Integer> p = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(p::add);
        List<Integer> o = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(o::add);

        boolean changes1;
        boolean changes2;

        do {
            changes1 = directConnect(p, o);
            changes2 = shortcut(p, o);
            alter(p);
        } while (changes1 || changes2);

        return parentsToComponents(p);
    }

    @Override
    public List<Collection<Integer>> algorithmRA() {
        List<Integer> p = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(p::add);
        List<Integer> o = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(o::add);

        boolean changes1;
        boolean changes2;

        do {
            changes1 = directRootConnect(p, o);
            changes2 = shortcut(p, o);
            alter(p);
        } while (changes1 && changes2);

        return parentsToComponents(p);
    }
}