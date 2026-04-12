import java.io.IOException;

public class HookTest {


    public void start(){
        System.out.println("hook test started ...");
        ShutdownHook shutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public static void main(String[] args) {

        HookTest hookTest = new HookTest();
        hookTest.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ShutdownHook extends Thread{
    @Override
    public void run() {
        System.out.println("ShutdownHook invoked ...");
    }
}

