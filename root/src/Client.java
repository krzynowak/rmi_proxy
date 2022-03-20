import java.rmi.registry.LocateRegistry;

public class Client
{
    public static void main(String[] args)
    {
        try
        {
            PolygonalChain inst  = (PolygonalChain) (LocateRegistry.getRegistry(null)).lookup("POLYGONAL_CHAIN");

            System.out.println("Client ready"); 

            inst.setPolygonalChainProcessorName("rmi://localhost/RMI_SERVER");

            inst.newPolygonalChain("1", new Position2D(0,1), new Position2D(10,11));
            inst.newPolygonalChain("2", new Position2D(3,4), new Position2D(4,5));
            inst.newPolygonalChain("3", new Position2D(5,6), new Position2D(6,7));
            inst.newPolygonalChain("4", new Position2D(7,8), new Position2D(8,9));
            inst.newPolygonalChain("5", new Position2D(9,10), new Position2D(10,11));

            inst.getResult("2");
            inst.getResult("3");
            inst.getResult("4");
            inst.getResult("5");

            System.out.println("Adding Segments"); 
            inst.addLineSegment("1", new Position2D(8,9), new Position2D(10,11));
            inst.addLineSegment("1", new Position2D(4,5), new Position2D(6,7));
            inst.addLineSegment("1", new Position2D(2,3), new Position2D(4,5));
            inst.addLineSegment("1", new Position2D(6,7), new Position2D(8,9));
            inst.addLineSegment("1", new Position2D(0,1), new Position2D(2,3));


            Thread.sleep(1000);

            System.out.println("Results"); 

            System.out.println("END OF TESTS"); 
        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString()); 
            e.printStackTrace(); 
        }
    }
}
