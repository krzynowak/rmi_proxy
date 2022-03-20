import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Server  extends UnicastRemoteObject implements PolygonalChainProcessor
{

    protected Server() throws RemoteException {
        super();
        try 
        {
            LocateRegistry.getRegistry().bind("RMI_SERVER", this);
        } 
        catch (AlreadyBoundException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int getConcurrentTasksLimit() throws RemoteException { return 3; }

    @Override
    public int process(String name, List<Position2D> polygonalChain) throws RemoteException
    {
        Integer a = (int)Math.pow(2, Double.valueOf(name));
        StringBuilder str = new StringBuilder();
        try { Thread.sleep(3000); } catch (Exception e) { }

        for (Position2D elem : polygonalChain) { str.append("SRV["+name+"]---> ["+elem.getCol()+"]["+elem.getRow()+"]\n" ); }


        System.out.println(str);
        System.out.printf("RETURNING %03d FOR %s\n", a, name);
        return a;
    }
    
   public void printMsg()
   {
        System.out.println("SERVER - start exec"); 
        try { Thread.sleep(4000); } catch (Exception e) { }
        System.out.println("This is an example RMI program");  
   }  


    public static void main(String[] args)
    {
        try
        {
            new Server();
            System.out.println("Server ready"); 
        } 
        catch (RemoteException e) 
        {
            System.err.println("Server exception: " + e.toString()); 
            e.printStackTrace();
        } 
    }
}
