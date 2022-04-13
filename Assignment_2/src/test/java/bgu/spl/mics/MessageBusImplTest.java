package bgu.spl.mics;
import bgu.spl.mics.application.objects.Student;
import junit.framework.TestCase;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import com.sun.media.sound.ModelByteBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.net.www.protocol.file.FileURLConnection;
import java.lang.Thread;
import static java.lang.Thread.sleep;

public class MessageBusImplTest extends TestCase {
    MessageBusImpl bus;
    StudentService ms;
    ExampleEvent e;
    ExampleBroadcast b;
    Future<Integer> f;
    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getInstance();
        Student student= new Student("student", "Computer" , "msc" );
        ms = new StudentService("studentService", student);
        e = new ExampleEvent("test");
        b = new ExampleBroadcast("test2");
        f = new Future<>();
    }

    @Test
    public void testSubscribeEvent() {
        bus.register(ms);
        assertFalse(bus.isSubscribedEvent(e.getClass() , ms));
        bus.subscribeEvent(e.getClass(), ms);
        assertTrue(bus.isSubscribedEvent(e.getClass(),ms));
        bus.unregister(ms);
    }
    @Test
    public void testSubscribeBroadcast() {
        bus.register(ms);
        assertFalse(bus.isSubscribedBroadcast(b.getClass() , ms));
        bus.subscribeBroadcast(b.getClass(), ms);
        assertTrue(bus.isSubscribedBroadcast(b.getClass(),ms));
        bus.unregister(ms);
    }
    @Test
    public void testComplete() {
        bus.register(ms);
        bus.subscribeEvent(e.getClass() , ms);
        Future test = bus.sendEvent(e);
        assertFalse(test.isDone());
        bus.complete(e,"ok" );
        assertTrue(test.isDone());
        assertEquals(test.get(), "ok");
        bus.unregister(ms);
    }
    @Test
    public void testSendBroadcast() {
        bus.register(ms);
        bus.subscribeBroadcast(b.getClass(), ms);
        bus.sendBroadcast(b);
        Message message = null;
        try{
            message = bus.awaitMessage(ms);
        }catch (InterruptedException t){
            System.err.println(t.getMessage());
        }
        assertEquals(b, message);
        bus.unregister(ms);
    }
    @Test
    public void testSendEvent() {
        bus.register(ms);
        bus.subscribeEvent(e.getClass(), ms);
        bus.sendEvent(e);
        Message message = null;
        try{
            message = bus.awaitMessage(ms);
        }catch (InterruptedException t){
            System.err.println(t.getMessage());
        }
        assertEquals(e, message);
    }
    @Test
    public void testRegister() {
        assertFalse(bus.isRegister(ms));
        bus.register(ms);
        assertTrue(bus.isRegister(ms));
        bus.unregister(ms);
    }
    @Test
    public void testUnregister() {
        bus.register(ms);
        assertTrue(bus.isRegister(ms));
        bus.unregister(ms);
        assertFalse(bus.isRegister(ms));
    }
    @Test
    public void testAwaitMessage() {
        bus.register(ms);
        bus.subscribeBroadcast(b.getClass(),ms);
        Thread t1 = new Thread (() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bus.sendBroadcast(b);
        });
        Message message = null;
        t1.start();

        long updateTime = System.currentTimeMillis();
        try{
            message = bus.awaitMessage(ms);
        }catch(InterruptedException error) {
            System.err.println(error.getMessage());
        }
        assertTrue(System.currentTimeMillis()-updateTime >= 1000);
        assertEquals(message, b);
        bus.unregister(ms);
    }

    }


