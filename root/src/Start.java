import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Start extends UnicastRemoteObject implements PolygonalChain
{
    private PolygonalChainProcessor stub = null;
    ThreadPoolExecutor executor = null;

    public Map<String, Container> chains;

    private class Container
    {
        private List<Position2D> list;
        private List<Line> lines;
        private Position2D stop;
        private Integer result;
        private Future<?> future = null;

        public Container(Position2D firstPoint, Position2D lastPoint)
        {
            this.lines = new ArrayList <Line>();
            this.list = new ArrayList <Position2D>();

            this.result = null;

            this.list.add(firstPoint);
            this.stop = lastPoint;
        }

        private class Line
        {
            Position2D A;
            Position2D B;

            private Line(Position2D firstPoint, Position2D lastPoint)
            {
                A = firstPoint;
                B = lastPoint;
            }

            Position2D getA() { return this.A; }
            Position2D getB() { return this.B; }
        }


        private void setResult(Integer res) { this.result = res;    }
        private Integer getResult()         { return this.result;   }
        private List<Position2D> getList()  { return this.list;     }

        private boolean sort()
        {
            //get current last element
            Position2D endPoint = this.list.get(this.list.size() - 1);
            //find all unprocessed lines
            int limit = this.lines.size();

            //iterate over all unprocessed lines
            for(int i=0; i<limit; i++)
            {
                //iterate over current line size in descending order
                for(int j=this.lines.size()-1; j>-1; j--)
                {
                    //check if end of previous line matches start of current one
                    if( endPoint.equals( this.lines.get(j).getA() ) )
                    {
                        //remove line -> add new endpoint -> leave nested loop
                        Line ln = this.lines.remove(j);
                        this.list.add(ln.getB());
                        endPoint = ln.getB();

                        break;
                    }
                }
            }

            return endPoint.equals(this.stop) ? true : false;
        }

        public boolean addSegment(Position2D firstPoint, Position2D lastPoint)
        {
            this.lines.add(new Line(firstPoint, lastPoint));

            return this.sort();
        }

        public boolean getStatus() 
        { 
            if( null != future )
            {
                return future.isDone();
            }
            else
            {
                return false;
            }
        }

        public void setFuture(Future<?> obj) 
        { 
            this.result = -1;
            this.future = obj;
        }
    }

    public Start() throws RemoteException
    {
        super();

        try
        {
            LocateRegistry.getRegistry().bind("POLYGONAL_CHAIN", this);
        } 
        catch (AlreadyBoundException e)
        {
            e.printStackTrace();
        }

        this.chains = new HashMap<String, Container>();
    }

    @Override
    public void newPolygonalChain(String name, Position2D firstPoint, Position2D lastPoint) throws RemoteException
    {
        synchronized(this.chains)
        {
            this.chains.put(name, new Container(firstPoint, lastPoint));
        }
        
    }

    @Override
    public void addLineSegment(String name, Position2D firstPoint, Position2D lastPoint) throws RemoteException
    {
        Container cont = this.chains.get(name);
        boolean readyToCalculate = false;

        if( null != cont )
        {
            synchronized(cont)
            {
                readyToCalculate = cont.addSegment(firstPoint, lastPoint);
            }

            if(readyToCalculate) this.getResult(name); 
        }

    }

    public class RemoteResult implements Runnable
    {
        private PolygonalChainProcessor stub;
        private String name;
        private Container container;
        
        public RemoteResult(PolygonalChainProcessor stb, String nme, Container cont)
        {
            this.stub = stb;
            this.name = nme;
            this.container = cont;
        }
        
        public void run() 
        {
            try
            {
                synchronized(this.container)
                {
                    this.container.setResult(this.stub.process(this.name, this.container.getList()));
                }
            } 
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer getResult(String name) throws RemoteException
    {
        Integer res = null;
        Container cont = this.chains.get(name);

        if(null != cont)
        {
            if(cont.getStatus())
            {
                res = cont.getResult();
            }
            else
            {
                if( null == cont.getResult() )
                {
                    cont.setFuture(this.executor.submit(new RemoteResult(this.stub, name, cont)));
                }
            }
        }

        return res;
    }

    @Override
    public void setPolygonalChainProcessorName(String uri) throws RemoteException
    {
        try
        {
            this.stub = (PolygonalChainProcessor) Naming.lookup(uri);

            this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.stub.getConcurrentTasksLimit());
        }
        catch (RemoteException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            System.err.println("setPolygonalChainProcessorName exception: " + e.toString()); 
            e.printStackTrace(); 
        }
    }


    public static void main(String[] args)
    {
        try
        {
            new Start();

            System.err.println("Proxy ready"); 
        } 
        catch (Exception e)
        {
            System.err.println("Proxy exception: " + e.toString()); 
            e.printStackTrace(); 
        } 
    }
}
