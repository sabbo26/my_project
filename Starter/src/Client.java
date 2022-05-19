import java.io.File;
import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Client implements Runnable {

    int file_num ;

    public Client( int file_num ){
        this.file_num = file_num;
    }

    @Override
    public void run() {
        try {
            String name = "Update";

            Registry registry = LocateRegistry.getRegistry();

            IGraphSolver my_solver = (IGraphSolver) registry.lookup(name);

            Random r = new Random(System.currentTimeMillis());

            File my_file = new File("log_client_" + file_num + ".txt");

            if ( my_file.exists() )
                my_file.delete();

            my_file.createNewFile();

            FileWriter writer = new FileWriter("log_client_" + file_num + ".txt");

            int num_of_batches = r.nextInt(6) + 10 ;

            for ( int u = 0 ; u < num_of_batches ; u++ ){
                // operations number ranges from 1 to 10

                // node id ranges from 1 to 10

                // operation id ( operation[i][0] ) takes values 0 query ,1 add or 2 delete

                // generating number of operations and the operations themselves randomly and write them to log

                int[][] operations = new int[ r.nextInt(6) + 5 ][3];

                for ( int i = 0 ; i < operations.length ; i++ ){
                    operations[i][0] = r.nextInt(3);
                    operations[i][1] = r.nextInt(10)+1;
                    operations[i][2] = r.nextInt(10)+1;
                    if ( operations[i][0] == 0 )
                        writer.write("Query ");
                    else if ( operations[i][0] == 1 )
                        writer.write("Add ");
                    else
                        writer.write("Delete ");
                    writer.write(operations[i][1] + " " + operations[i][2]);
                    writer.write("\n");
                }

                long elapsed = System.currentTimeMillis();

                int[] answers = my_solver.execute_batch(operations);

                elapsed = System.currentTimeMillis() - elapsed;

                if ( answers.length > 0 ){

                    writer.write("\nAnswers: " + answers[0]);

                    for ( int i = 1 ; i < answers.length ; i++ )
                        writer.write(", " + answers[i]  );
                }
                writer.write("\n\nTime taken for this batch : " + elapsed + " ms\n");
                /*
                int sleep = r.nextInt(10001);
                writer.write( "Sleeping " + sleep + " ms" );
                Thread.sleep( sleep );
                */
                writer.write("\n\n\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
