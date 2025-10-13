package org.itmo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

@State
@JCStressTest
@Outcome(id = "7", expect = Expect.ACCEPTABLE)
@Outcome(id = "6", expect = Expect.FORBIDDEN)
@Outcome(id = "5", expect = Expect.FORBIDDEN)
@Outcome(id = "4", expect = Expect.FORBIDDEN)
@Outcome(id = "3", expect = Expect.FORBIDDEN)
@Outcome(id = "2", expect = Expect.FORBIDDEN)
@Outcome(id = "1", expect = Expect.FORBIDDEN)
@Outcome(id = "0", expect = Expect.FORBIDDEN)
public class ParallelArtifactsTest {

    Graph graph = new RandomGraphGenerator().generateGraph(new Random(13), 7, 28);
    Queue<Integer> queue = new LinkedList<>();
//    Queue<Integer> queue = new ConcurrentLinkedQueue<>();
    AtomicIntegerArray visited = new AtomicIntegerArray(graph.getV());
    {
        queue.add(0);
        queue.add(1);
    }

    @Actor
    public void actor1() {
        createBfsRunner().run();
    }

    @Actor
    public void actor2() {
        createBfsRunner().run();
    }

    @Arbiter
    public void arbiter(I_Result r) {
        int count = 0;
        for (int i = 0; i < graph.getV(); i++) {
            if (visited.get(i) > 0) {
                count++;
            }
        }
        r.r1 = count;
    }


    private Runnable createBfsRunner() {
        return () -> {
            Integer u = queue.poll();
            while (u != null) {
                for (int v : graph.getAdjList()[u]) {
                    boolean firstVisited = visited.compareAndSet(v, 0, 1);
                    if (firstVisited) {
                        queue.add(v);
                    }
                }
                u = queue.poll();
            }
        };
    }
}
