package nx.server.zmq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nx.server.zmq.components.IProxy;
import nx.server.zmq.components.IZmqWorker;
import nx.server.zmq.components.ZmqProxy;
import nx.server.zmq.components.ZmqServiceRegistration;
import nx.server.zmq.components.ZmqWorker;

import org.apache.log4j.Logger;

public class ZmqServer
{
	private final static Logger logger = Logger.getLogger(ZmqServer.class);

	private IProxy proxy;
	private int workerResponsePort;
	private List<IZmqWorker> workerList;

	private ExecutorService executor;

	/**
	 * @param clientRequestPort
	 * @param workerResponsePort
	 */
	public ZmqServer(int clientRequestPort, int workerResponsePort)
	{
		this(clientRequestPort, workerResponsePort,
				new ZmqProxy(clientRequestPort, workerResponsePort, new ZmqServiceRegistration()));
	}

	/**
	 * @param clientRequestPort
	 * @param workerResponsePort
	 * @param proxy
	 */
	public ZmqServer(int clientRequestPort, int workerResponsePort, IProxy proxy)
	{
		this.proxy = proxy;
		this.workerResponsePort = workerResponsePort;
		workerList = new ArrayList<IZmqWorker>();
		executor = Executors.newCachedThreadPool();
	}

	/**
	 * @param handler
	 * @param scale
	 * @throws Exception
	 */
	public void addHandler(IHandler handler, int scale) throws Exception
	{
		if (scale < 1)
		{
			throw new Exception("Invalid scale number: " + scale);
		}
		for (int i = 0; i < scale; i++)
		{
			IZmqWorker worker = createWorker(workerResponsePort, handler, i);
			workerList.add(worker);
		}
	}

	/**
	 * @throws Exception
	 */
	public void start() throws Exception
	{
		// run the proxy
		executor.execute(proxy);
		Thread.sleep(10);
		// run all the workers
		for (IZmqWorker w : workerList)
		{
			executor.execute(w);
		}
	}

	public void stop()
	{
		proxy.stop();
		for (IZmqWorker w : workerList)
		{
			w.stop();
		}
	}

	// For Expandability

	protected IZmqWorker createWorker(int workerResponsePort, IHandler handler, int i)
	{
		return new ZmqWorker(workerResponsePort, handler, i);
	}

	protected IProxy getProxy()
	{
		return proxy;
	}
}
