import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Graph implements IGraph {
    private final HashMap<Integer, HashSet<Pair>> adjacencyList;
    private final boolean oriented;
    private final boolean weighted;
    private int numberOfEdges;

    public Graph(boolean oriented, boolean weighted) {
        this.oriented = oriented;
        this.weighted = weighted;
        this.adjacencyList = new HashMap<>();
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
        return adjacencyList.keySet();
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
        return adjacencyList.size();
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
        numberOfEdges++;
    }

    @Override
    public void insertEdge(int x, int y) {
        if (weighted)
            throw new RuntimeException("Weight omitted!");
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

    private boolean directConnect(List<Integer> parents) {
        AtomicBoolean change = new AtomicBoolean(false);
        this.listEdges().stream().parallel().forEach(pair -> {
            Integer a = pair.getA();
            Integer b = pair.getB();
            if (a > b) {
                int min = Math.min(parents.get(a), b);
                if (parents.get(a) != min) {
                    parents.set(a, min);
                    change.set(true);
                }
            } else {
                int min = Math.min(parents.get(b), a);
                if (parents.get(b) != min) {
                    parents.set(b, min);
                    change.set(true);
                }
            }
        });
        return change.get();
    }


    private boolean parentConnect(List<Integer> parents) {
        listNodes().stream().parallel().forEach(v -> {
            parents.set(parents.get(v), parents.get(v));
        });
        AtomicBoolean change = new AtomicBoolean(false);
        listEdges().stream().parallel().forEach(pair -> {
            Integer v = pair.getA();
            Integer w = pair.getB();
            if (parents.get(parents.get(v)) > parents.get(parents.get(w))) {
                change.set(true);
                parents.set(parents.get(parents.get(v)),
                        Math.min(parents.get(parents.get(parents.get(v))), parents.get(parents.get(w))));
            } else {
                change.set(true);
                parents.set(parents.get(parents.get(w)),
                        Math.min(parents.get(parents.get(parents.get(w))), parents.get(parents.get(v))));
            }
        });
        return change.get();
    }

    private boolean directRootConnect(List<Integer> parents) {
        AtomicBoolean change = new AtomicBoolean(false);
        this.listEdges().stream().parallel().forEach(pair -> {
            Integer a = pair.getA();
            Integer b = pair.getB();
            System.out.println(parents);
            System.out.println(pair);
            if (a > b && a.equals(parents.get(parents.get(a)))) {
                int min = Math.min(parents.get(a), b);
                if (parents.get(a) != min) {
                    parents.set(a, min);
                    change.set(true);
                }
            } else {
                if (b.equals(parents.get(parents.get(b)))) {
                    int min = Math.min(parents.get(b), a);
                    if (parents.get(b) != min) {
                        parents.set(b, min);
                        change.set(true);
                    }
                }
            }
            System.out.println(parents);
            System.out.println("\n");
        });
        return change.get();
    }

    private boolean shortcut(List<Integer> parents) {
        listNodes().stream().parallel().forEach(v -> {
            parents.set(parents.get(v), parents.get(v));
        });
        listNodes().stream().parallel().forEach(v -> {
            parents.set(v, parents.get(parents.get(parents.get(parents.get(v)))));
        });
        return false;
    }

    private boolean alter(List<Integer> parents) {
        AtomicBoolean change = new AtomicBoolean(false);
        listEdges().stream().parallel().forEach(pair -> {
            Integer a = pair.getA();
            Integer b = pair.getB();
            if (parents.get(a).equals(parents.get(b))) {
                // delete [a b]
                parents.set(a, parents.get(parents.get(a)));
                parents.set(b, parents.get(parents.get(b)));
            } else {
                // replace [a, b] by [parents[a], parents[b]]
                Integer gpa = parents.get(parents.get(a));
                if (!Objects.equals(gpa, parents.get(a))) {
                    parents.set(a, gpa);
                    change.set(true);
                }
                Integer gpb = parents.get(parents.get(b));
                if (!Objects.equals(gpb, parents.get(b))) {
                    parents.set(b, gpb);
                    change.set(true);
                }
            }
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
        List<Integer> parents = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(parents::add);

        boolean parentsChange;
        boolean shortcutChange;

        do {
            parentsChange = parentConnect(parents);
            System.out.println(parents);
            do {
                shortcutChange = shortcut(parents);
            } while (shortcutChange);
        } while (parentsChange);

        System.out.println(parents);
        return parentsToComponents(parents);
    }

    @Override
    public List<Collection<Integer>> algorithmA() {
        List<Integer> parents = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(parents::add);

        boolean changes1;
        boolean changes2;
        boolean changes3;

        do {
            changes1 = directConnect(parents);
            changes2 = shortcut(parents);
            changes3 = alter(parents);
        } while (changes1 && changes2 && changes3);

        return parentsToComponents(parents);
    }

    @Override
    public List<Collection<Integer>> algorithmRA() {
        List<Integer> parents = new ArrayList<>(this.numberOfNodes());
        IntStream.range(0, this.numberOfNodes()).forEach(parents::add);

        AtomicBoolean changes1 = new AtomicBoolean(false);
        AtomicBoolean changes2 = new AtomicBoolean(false);
        AtomicBoolean changes3 = new AtomicBoolean(false);
        Runnable r1 = () -> changes1.set(directRootConnect(parents));
        Runnable r2 = () -> changes2.set(shortcut(parents));
        Runnable r3 = () -> changes3.set(alter(parents));

        try {
            do {
                Thread t1 = new Thread(r1);
                t1.start();
                t1.join();

                Thread t2 = new Thread(r2);
                t2.start();
                t2.join();

                Thread t3 = new Thread(r3);
                t3.start();
                t3.join();
            } while (changes1.get() && changes2.get() && changes3.get());
        } catch (InterruptedException e) {
            return null;
        }

        return parentsToComponents(parents);
    }
}