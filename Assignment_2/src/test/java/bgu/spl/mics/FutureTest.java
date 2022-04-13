package bgu.spl.mics;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import java.lang.Thread;
import static java.lang.Thread.sleep;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class FutureTest extends TestCase {
    private Future<Integer> future;
    @Before
    public void setUp() throws Exception {
        future = new Future<>();
    }

    @Test
    public void testGet() {
        assertFalse(future.isDone());
        Thread t1 = new Thread (() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.resolve(1);
        });
        t1.start();
        long updateTime = System.currentTimeMillis();
        Integer x = future.get();
        assertTrue(System.currentTimeMillis()-updateTime >= 1000);
	    assertTrue(future.isDone());
        assertEquals(x, new Integer(1));
    }

    @Test
    public void testResolve() {
        Integer result = 1;
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(future.get(),result);
        future.resolve(2);
        assertEquals(new Integer(1) , future.get());
    }
    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        future.resolve(new Integer(1));
        assertEquals(future.get(),new Integer(1));
        assertTrue(future.isDone());
    }
    @Test
    public void testTestGet() {
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        Thread t1 = new Thread (() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.resolve(1);
        });
        t1.start();
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.get(1500, TimeUnit.MILLISECONDS);
        assertTrue(future.isDone());
        assertEquals(new Integer(1),future.get());
    }
}