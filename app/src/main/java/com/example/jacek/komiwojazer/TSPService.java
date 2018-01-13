package com.example.jacek.komiwojazer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TSPService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry< Node, Integer> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private static void CalculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    private static Node getLowestDistanceNode(Set < Node > unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private Element[] generate(int n, Element[] A) {
        if (n != 1) {
            for(int i = 0; i < n - 1; i++) {
                generate(n - 1, A);
                if (n % 2 == 0) {
                    Element a = A[i];
                    Element b = A[n - 1];
                    A[i] = b;
                    A[n-1] = a;
                } else {
                    Element a = A[0];
                    Element b = A[n - 1];
                    A[0] = b;
                    A[n-1] = a;
                }
            }
            generate(n - 1, A);
        }

        return A;
    }

    private int countDistance(Element[] graph) {
        int distance = 0;
        for (int i = 0; i < graph.length; i++) {
            Element a = graph[i];
            Element b = i + 1 < graph.length ? graph[i + 1] : graph[0];

            distance += countDistanceBetweenTwoPoints(a, b);
        }

        return distance;
    }

    private int countDistanceBetweenTwoPoints(Element a, Element b) {
        int axbx = a.getX() - b.getX();
        int ayby = a.getY() - b.getY();
        int pow1 = (int) Math.pow(axbx, 2);
        int pow2 = (int) Math.pow(ayby, 2);

        return  (int) Math.sqrt(pow1 + pow2);
    }

    private Element[] optimize(Element[] graph2) {
        Element[] optimized = graph2;

        Node[] nodes = {};
        for (int i = 0; i < graph2.length; i++) {
            Node a = new Node(Integer.toString(i));
            nodes[i] = ;
        }

        for (int i = 0; i < nodes.length; i++) {
            Node current = nodes[i];
            for (int j = 0; j < nodes.length; j++) {
                if (j != i) {
                    current.addDestination(nodes[j], countDistanceBetweenTwoPoints(graph2[i], graph2[j]));
                }
            }
        }

        Graph graph = new Graph();

        for (Node node : nodes) {
            graph.addNode(node);
        }

        graph = calculateShortestPathFromSource(graph, nodes[0]);



//        int distance = 0;
//
//        for (int i = 0; i < 10; i++) {
//            distance = countDistance(optimized);
//
//            optimized = generate(graph.length, graph);
//        }

        return optimized;
    }

    private final ITSPService.Stub mBinder = new ITSPService.Stub() {
        @Override
        public int getResult(int a) throws RemoteException {
            /**
             1 565.0 575.0
             2 25.0 185.0
             3 345.0 750.0
             4 945.0 685.0
             5 845.0 655.0
             6 880.0 660.0
             7 25.0 230.0
             8 525.0 1000.0
             9 580.0 1175.0
             10 650.0 1130.0
             */

            Element[] graph = {
                new Element(565, 575),
                new Element(25, 185),
                new Element(345, 750),
                new Element(945, 685),
            };

            Element[] cos = optimize(graph);

            return a;
        }
    };
}

class Element {
    private int x,y;

    public Element(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Graph {

    private Set<Node> nodes = new HashSet<>();

    void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }
}

class Node {

    private String name;

    private List<Node> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<Node, Integer> adjacentNodes = new HashMap<>();

    void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<Node> getShortestPath() {
        return shortestPath;
    }

    void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<Node, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }
}
