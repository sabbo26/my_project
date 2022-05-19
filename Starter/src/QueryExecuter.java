import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

public class QueryExecuter implements Runnable{

    GraphSolver solver ;

    int source ;

    int dest ;

    int result ;

    public QueryExecuter(GraphSolver solver , int source , int dest ){
        this.solver = solver;
        this.source = source;
        this.dest = dest;
    }

    // implement BFS to find shortest path between two nodes

    @Override
    public void run() {

        HashMap<Integer, HashSet<Integer>> adj = solver.getAdj_lst();

        if ( adj.containsKey(source ) && adj.containsKey(dest) ){

            HashSet<Integer> visited = new HashSet<>();

            visited.add(source);

            ArrayDeque<Integer> qu = new ArrayDeque<>();

            qu.addLast(source);

            int steps = 0 ;

            while ( ! qu.isEmpty() ){
                int size = qu.size();
                for ( int i = 0 ; i < size ; i++ ){
                    int temp = qu.pollFirst();
                    if ( temp == dest ){
                        result = steps;
                        return;
                    }
                    HashSet<Integer> lst = adj.get(temp);
                    for ( int k : lst ){
                        if ( !visited.contains(k) ){
                            visited.add(k);
                            qu.addLast(k);
                        }
                    }
                }
                steps++;
            }
        }
        result = -1 ;
    }

}
