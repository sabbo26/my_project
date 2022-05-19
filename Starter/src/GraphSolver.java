import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class GraphSolver implements IGraphSolver {

    private HashMap<Integer, HashSet<Integer>> adj_lst ;

    public GraphSolver(){
        adj_lst = new HashMap<>();
        File my_file = new File("log_server.txt");
        if ( my_file.exists() )
            my_file.delete();
        try {
            my_file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.initialize_graph();
    }

    // synchronized to serve one client at a time
    // RMI dispatches a thread for each invocation so server must be thread safe

    /*
    - each operation has 3 integers --> first one is the type 0 for query 1 for add and 2 for delete
    - query operations are done concurrently by Queryexecuter thread until facing an update operation so we join on all threads
    before executing update operation
    - maintain server log file while execcuting batch
    - return is int[] contains answers for query operations in the batch
    */
    @Override
    public synchronized int[] execute_batch(int[][] batch_of_operations) throws RemoteException {

        ArrayDeque<Thread> qu_1 = new ArrayDeque<>();

        ArrayDeque<QueryExecuter> qu_2 = new ArrayDeque<>();

        ArrayList<Integer> results = new ArrayList<>();

        try ( FileWriter writer = new FileWriter( new File("log_server.txt") , true ) ){
            writer.append("Batch starts...\n\n");
            for ( int[] operation : batch_of_operations ){
                if ( operation[0] == 0 ){
                    writer.append("Query " + operation[1] + " " + operation[2] + "\n");
                    QueryExecuter x = new QueryExecuter(this,operation[1],operation[2]);
                    Thread t = new Thread(x);
                    qu_1.addLast(t);
                    qu_2.addLast(x);
                    t.start();
                }
                else if (operation[0] == 1) {
                    writer.append("Add " + operation[1] + " " + operation[2] + "\n");
                    if ( adj_lst.containsKey(operation[1]) && adj_lst.containsKey(operation[2]) &&
                            adj_lst.get(operation[1]).contains(operation[2]))
                        continue;
                    else {
                        empty_queues(qu_1,qu_2,results);
                        add_edge(operation[1],operation[2]);
                    }
                }
                else if ( operation[0] == 2 ){
                    writer.append("Delete " + operation[1] + " " + operation[2]+"\n");
                    if ( !adj_lst.containsKey(operation[1]) || !adj_lst.containsKey(operation[2]) ||
                            !adj_lst.get(operation[1]).contains(operation[2]))
                        continue;
                    else {
                        empty_queues(qu_1, qu_2, results);
                        adj_lst.get(operation[1]).remove(operation[2]);
                    }
                }
            }
            empty_queues(qu_1,qu_2,results);
            writer.append("\n");
            if ( results.size() > 0 ){
                writer.append("Answers : " + results.get(0));
                for ( int i = 1 ; i < results.size() ; i++ )
                    writer.append(", " + results.get(i));
                writer.append("\n\n");
            }
            writer.append("Batch ended ...\n\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( results.size() == 0 )
            return new int[]{};

        int[] finish = new int[results.size()];

        for ( int i = 0 ; i < results.size() ; i++ )
            finish[i] = results.get(i);


        return finish ;
    }

    private void add_edge( int source , int dest ){
        HashSet<Integer> temp ;
        if ( !adj_lst.containsKey(source) ){
            temp = new HashSet<>();
            adj_lst.put(source,temp);
        }
        else
            temp = adj_lst.get(source);

        temp.add(dest);

        if ( !adj_lst.containsKey(dest) ){
            temp = new HashSet<>();
            adj_lst.put(dest,temp);
        }

    }

    // responsible for joining query threads before executing update operation

    private void empty_queues( ArrayDeque<Thread> qu_1 , ArrayDeque<QueryExecuter> qu_2 , ArrayList<Integer> results ){

        while ( ! qu_1.isEmpty() ){
            try {
                qu_1.pollFirst().join();
                results.add(qu_2.pollFirst().result);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    public void initialize_graph() {

        Scanner s ;

        try {
            s = new Scanner( new File("initial_graph.txt") );
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            return;
        }

        while ( s.hasNextLine() ){

            String line = s.nextLine();

            if ( line.equals("S") )
                break;
            else {
                String[] nodes = line.split(" ");
                add_edge( Integer.parseInt(nodes[0]) , Integer.parseInt(nodes[1]) );
            }

        }


    }

    public HashMap<Integer, HashSet<Integer>> getAdj_lst() {
        return adj_lst;
    }

}
