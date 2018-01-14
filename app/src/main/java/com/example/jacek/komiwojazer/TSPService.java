package com.example.jacek.komiwojazer;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TSPService extends Service {

    private int theBestDistance = Integer.MAX_VALUE;
    private int iterator = 0;
    private static final String inputFileName = "input.txt";
    private static final String outputFileName = "output.txt";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Node[] generate(int n, Node[] A) {
        if (n == 1) {
            iterator++;
            int distance = countDistance(A);
            if (distance < theBestDistance) {
                theBestDistance = distance;
            }
        } else {
            for(int i = 0; i < n - 1; i++) {
                generate(n - 1, A);
                if (n % 2 == 0) {
                    Node a = A[i];
                    Node b = A[n - 1];
                    A[i] = b;
                    A[n-1] = a;
                } else {
                    Node a = A[0];
                    Node b = A[n - 1];
                    A[0] = b;
                    A[n-1] = a;
                }
            }
            generate(n - 1, A);
        }

        return A;
    }

    private int countDistance(Node[] graph) {
        int distance = 0;
        for (int i = 0; i < graph.length; i++) {
            distance += countDistanceBetweenTwoPoints(
                    graph[i],
                    i + 1 == graph.length ? graph[0] : graph[i + 1]
            );
        }

        return distance;
    }

    private int countDistanceBetweenTwoPoints(Node a, Node b) {
        int axbx = a.getX() - b.getX();
        int ayby = a.getY() - b.getY();
        int pow1 = (int) Math.pow(axbx, 2);
        int pow2 = (int) Math.pow(ayby, 2);

        return  (int) Math.sqrt(pow1 + pow2);
    }

    private final ITSPService.Stub mBinder = new ITSPService.Stub() {
        @Override
        public String getResult() throws RemoteException {

            Node[] graph = readGraphFromFile(inputFileName);
            writeToFile("Graph: ");
            writeGraphToFile(graph);

            Log.i("graph", "Start generate");
            Node[] shortestPath = generate(graph.length, graph);
            Log.i("graph", "Stop generate");


            writeToFile("Optimized distance: " + theBestDistance);
            writeToFile("Permutations count: " + iterator);
            writeToFile("Shortest path:");
            writeGraphToFile(shortestPath);
            writeToFile("");

            return  "Wyliczony dystans: " + theBestDistance + " Iteracje: " + iterator;
        }
    };

    private void writeGraphToFile(Node[] graph) {
        for (Node vertex : graph) {
            writeToFile(vertex.getName() + " " + vertex.getX() + " " + vertex.getY());
        }
    }

    private void writeToFile(String string) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        externalStorageDirectory = new File(externalStorageDirectory, outputFileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(externalStorageDirectory, true);
        } catch (FileNotFoundException ex) {
            //deal with the problem
        }
        PrintWriter pw = new PrintWriter(fos);
        pw.println(string);
        pw.close();
    }

    private Node[] readGraphFromFile(String fileName)
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, fileName);

        List<Node> graph = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                graph.add(
                    new Node(parts[0],
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]))
                );
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph.toArray(new Node[graph.size()]);
    }
}

class Node {
    private int x,y;
    private String name;

    Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}
