package nx.server.zmq.components;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class ZmqServiceRegistration implements IServiceRegistration
{
	private final static Logger logger = Logger.getLogger(ZmqServiceRegistration.class);

	private final Object lock;
	private ConcurrentHashMap<String, ConcurrentHashMap<byte[], Boolean>> serviceReg;

	public ZmqServiceRegistration()
	{
		lock = new Object();
		serviceReg = new ConcurrentHashMap<String, ConcurrentHashMap<byte[], Boolean>>();
	}

	@Override
	public boolean isServiceRegistered(String service)
	{
		synchronized (getLock())
		{
			return getServiceReg().containsKey(service);
		}
	}

	@Override
	public byte[] getWorker(String service)
	{
		synchronized (getLock())
		{
			// get free worker
			ConcurrentHashMap<byte[], Boolean> workerReg = getServiceReg().get(service);
			for (byte[] id : workerReg.keySet())
			{
				if (workerReg.get(id))
				{
					getServiceReg().get(service).put(id, false);
					return id;
				}
			}
			return null;
		}
	}

	@Override
	public void onWorkerResponse(String service, byte[] workerId)
	{
		synchronized (getLock())
		{
			if (!getServiceReg().containsKey(service))
			{
				// register the worker under service and mark it as free
				ConcurrentHashMap<byte[], Boolean> workerReg = new ConcurrentHashMap<byte[], Boolean>();
				workerReg.put(workerId, true);
				getServiceReg().put(service, workerReg);
				logger.info("Worker [" + new String(workerId) + "] register to service [" + service + "]");
			}
			else
			{
				if (!getServiceReg().get(service).containsKey(workerId))
				{
					logger.info("Worker [" + new String(workerId) + "] register to service [" + service + "]");
				}
				// if first time register, mark it as free
				// in any case, if a worker is responding, mark it as free
				getServiceReg().get(service).put(workerId, true);
			}
		}
	}

	protected ConcurrentHashMap<String, ConcurrentHashMap<byte[], Boolean>> getServiceReg()
	{
		return serviceReg;
	}

	protected Object getLock()
	{
		return lock;
	}

}
