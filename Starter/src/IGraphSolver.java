import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGraphSolver extends Remote {
    // batch_of_operations has size [n][3]
    // batch_of_operations[i][0] = 0 if Query
    // batch_of_operations[i][0] = 1 if Add
    // batch_of_operations[i][0] = 2 if Delete
    int[] execute_batch(int[][] batch_of_operations) throws RemoteException;
}
