// import java.rmi.RemoteException;
// import java.util.List;

// public class PCP implements PolygonalChainProcessor
// {
//     @Override
//     public int getConcurrentTasksLimit() throws RemoteException { return 3; }

//     @Override
//     public int process(String name, List<Position2D> polygonalChain) throws RemoteException
//     {
//         Integer a = (int)Math.pow(2, Double.valueOf(name));
//         StringBuilder str = new StringBuilder();
//         // try { Thread.sleep(100); } catch (Exception e) { }

//         for (Position2D elem : polygonalChain) { str.append("SRV["+name+"]---> ["+elem.getCol()+"]["+elem.getRow()+"]\n" ); }


//         System.out.println(str);
//         System.out.printf("RETURNING %03d FOR %s\n", a, name);
//         return a;
//     }
    
//    public void printMsg()
//    {
//         System.out.println("SERVER - start exec"); 
//         try { Thread.sleep(4000); } catch (Exception e) { }
//         System.out.println("This is an example RMI program");  
//    }  
   
// }
