import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Runnable{

    IGraphSolver reference_to_graph ;

    public Server( ){
        super();
    }


    // server code to setup rmi registry and stub object

    @Override
    public void run() {

        GraphSolver my_solver = new GraphSolver();

        IGraphSolver my_obj = my_solver ;

        reference_to_graph = my_obj ;

        try {
            String name = "Update";
            IGraphSolver stub = (IGraphSolver) UnicastRemoteObject.exportObject(my_obj,0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(name,stub);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("R");

    }

}
