package org.itmo;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashSet;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BFSTest {

    @Test
    public void bfsTest() throws IOException {
        int[] sizes = new int[]{10, 100, 1000, 10_000, 10_000, 50_000, 100_000, 1_000_000, 2_000_000};
        int[] connections = new int[]{50, 500, 5000, 50_000, 100_000, 1_000_000, 1_000_000, 10_000_000, 10_000_000};
        Random r = new Random(42);
        try (FileWriter fw = new FileWriter("tmp/results.txt")) {
            for (int i = 0; i < sizes.length; i++) {
                System.out.println("--------------------------");
                System.out.println("Generating graph of size " + sizes[i] + " ...wait");
                Graph g = new RandomGraphGenerator().generateGraph(r, sizes[i], connections[i]);
                System.out.println("Generation completed!\nStarting bfs");
                long serialTime = executeSerialBfsAndGetTime(g);
                long parallelTime = executeParallelBfsAndGetTime(g);
                fw.append("Times for " + sizes[i] + " vertices and " + connections[i] + " connections: ");
                fw.append("\nSerial: " + serialTime);
                fw.append("\nParallel: " + parallelTime);
                fw.append("\n--------\n");
            }
            fw.flush();
        }
    }

    @Test
    public void threadsTest() throws IOException {
        int[] sizes = new int[]{2_000_000};
        int[] connections = new int[]{10_000_000};
        int[] threadCounts = new int[]{1, 2, 4, 8, 10, 16, 32, 64, 100, 132, 164, 200};
        Random r = new Random(42);
        try (FileWriter fw = new FileWriter("tmp/threads_results.txt")) {
            for (int i = 0; i < sizes.length; i++) {
                System.out.println("--------------------------");
                System.out.println("Generating graph of size " + sizes[i] + " ...wait");
                Graph g = new RandomGraphGenerator().generateGraph(r, sizes[i], connections[i]);
                System.out.println("Generation completed!\nStarting bfs");


                for (int threadCount : threadCounts) {
                    long parallelTime = executeParallelBfsAndGetTime(g);
                    fw.append("Times for " + sizes[i] + " vertices and " + connections[i] + " connections: ");
                    fw.append("\nThreadCount: " + threadCount);
                    fw.append("\nParallel: " + parallelTime);
                    fw.append("\n--------\n");
                }
            }
            fw.flush();
        }
    }


    private long executeSerialBfsAndGetTime(Graph g) {
        long startTime = System.currentTimeMillis();
        int bfsed = g.bfs(0);
        assertEquals(g.getV(), bfsed);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private long executeParallelBfsAndGetTime(Graph g) {
        long startTime = System.currentTimeMillis();
        int bfsed = g.parallelBFS(0, Runtime.getRuntime().availableProcessors());
        assertEquals(g.getV(), bfsed);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private long executeParallelBfsAndGetTime(Graph g, int threadCount) {
        long startTime = System.currentTimeMillis();
        int bfsed = g.parallelBFS(0, threadCount);
        assertEquals(g.getV(), bfsed);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

}
