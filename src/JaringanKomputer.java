import java.util.*;

// ================== GRAPH (Adjacency List) ==================
class Graph {
    private final Map<String, List<String>> adj = new LinkedHashMap<>();
    private final boolean directed;

    Graph(boolean directed) {
        this.directed = directed;
    }

    void addVertex(String v) {
        adj.putIfAbsent(v, new ArrayList<>());
    }

    void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        adj.get(from).add(to);
        if (!directed) {
            adj.get(to).add(from); // undirected: dua arah
        }
    }

    void print() {
        for (Map.Entry<String, List<String>> e : adj.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
    }

    // BFS: menelusuri perangkat per level dari titik awal
    List<String> bfs(String start) {
        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            order.add(cur);
            for (String next : adj.get(cur)) {
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return order;
    }

    // DFS: menelusuri sedalam mungkin dulu
    List<String> dfs(String start) {
        List<String> order = new ArrayList<>();
        dfsHelper(start, new HashSet<>(), order);
        return order;
    }

    private void dfsHelper(String cur, Set<String> visited, List<String> order) {
        visited.add(cur);
        order.add(cur);
        for (String next : adj.get(cur)) {
            if (!visited.contains(next)) {
                dfsHelper(next, visited, order);
            }
        }
    }
}

// ================== HASH TABLE (Chaining) ==================
class HashTable {
    private static class Node {
        int id;
        String data;
        Node next;

        Node(int id, String data) {
            this.id = id;
            this.data = data;
        }
    }

    private final Node[] table;
    private final int m;

    HashTable(int m) {
        this.m = m;
        this.table = new Node[m];
    }

    private int hash(int id) {
        return id % m; // h(ID) = ID mod m
    }

    void put(int id, String data) {
        int idx = hash(id);
        for (Node n = table[idx]; n != null; n = n.next) {
            if (n.id == id) { // ID sudah ada -> update
                n.data = data;
                return;
            }
        }
        Node baru = new Node(id, data);
        baru.next = table[idx]; // sisip di depan chain
        table[idx] = baru;
    }

    String get(int id) {
        for (Node n = table[hash(id)]; n != null; n = n.next) {
            if (n.id == id) return n.data;
        }
        return null; // tidak ditemukan
    }

    boolean remove(int id) {
        int idx = hash(id);
        Node prev = null;
        for (Node n = table[idx]; n != null; prev = n, n = n.next) {
            if (n.id == id) {
                if (prev == null) table[idx] = n.next;
                else prev.next = n.next;
                return true;
            }
        }
        return false;
    }

    void print() {
        for (int i = 0; i < m; i++) {
            if (table[i] == null) continue;
            StringBuilder sb = new StringBuilder("Indeks " + i + " : ");
            for (Node n = table[i]; n != null; n = n.next) {
                sb.append("[").append(n.id).append(":").append(n.data).append("]");
                if (n.next != null) sb.append(" -> ");
            }
            System.out.println(sb);
        }
    }
}

// ================== MAIN ==================
public class JaringanKomputer {
    public static void main(String[] args) {
        // 1. Graph undirected (topologi fisik)
        System.out.println("=== Graph Undirected (Topologi Fisik) ===");
        Graph net = new Graph(false);
        net.addEdge("R1", "S1");
        net.addEdge("R1", "S2");
        net.addEdge("S1", "PC1");
        net.addEdge("S1", "PC2");
        net.addEdge("S2", "Server");
        net.addEdge("S2", "Printer");
        net.print();
        System.out.println("BFS dari R1 : " + net.bfs("R1"));
        System.out.println("DFS dari R1 : " + net.dfs("R1"));

        // 2. Graph directed (aliran data logis)
        System.out.println("\n=== Graph Directed (Aliran Data) ===");
        Graph flow = new Graph(true);
        flow.addEdge("PC1", "Server");
        flow.addEdge("PC2", "Server");
        flow.addEdge("Server", "Printer");
        flow.print();

        // 3. Hash table pencarian perangkat berdasarkan ID
        System.out.println("\n=== Hash Table (h(ID) = ID mod 10) ===");
        HashTable devices = new HashTable(10);
        devices.put(101, "PC1");
        devices.put(205, "Server");
        devices.put(318, "Router R1");
        devices.put(111, "PC2"); // collision dengan 101 (indeks 1)
        devices.print();

        System.out.println("\nCari ID 205 : " + devices.get(205));
        System.out.println("Cari ID 111 : " + devices.get(111));
        System.out.println("Cari ID 999 : " + devices.get(999));

        devices.remove(101);
        System.out.println("\nSetelah hapus ID 101:");
        devices.print();
    }
}