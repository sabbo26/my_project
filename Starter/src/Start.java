import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Scanner;

public class Start {

    /*

    Starter program that starts a server thread and client threads.

    note: it keeps a refernce to graph solver to prevent erasing object by garbage collection

     */
    public static void main(String[] args) {

        Server my_server = new Server();

        System.out.println("Starting server thread");

        Thread t_1 = new Thread( my_server );

        t_1.start();

        // waits for initializing the graph and setting registry and stub objects

        try {
            t_1.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        IGraphSolver x = my_server.reference_to_graph ;

        System.out.println("Starting clients threads");

        ArrayDeque<Thread> qu = new ArrayDeque<>();

        // getting number of nodes from system configuration

        Scanner s ;

        try {
            s = new Scanner(new File("System_configuration.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int num_of_nodes = s.nextInt();

        for ( int i = 0 ; i < num_of_nodes ; i++ ){
            qu.addLast( new Thread( new Client(i) ) );
            qu.getLast().start();
        }

        while ( ! qu.isEmpty() ){
            try {
                qu.pollFirst().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        System.exit(0);

    }

}
