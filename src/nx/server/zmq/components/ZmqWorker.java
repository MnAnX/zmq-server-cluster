package nx.server.zmq.components;

import nx.server.zmq.IHandler;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class ZmqWorker implements IZmqWorker
{
	private final static Logger logger = Logger.getLogger(ZmqWorker.class);

	private Gson gson;

	private String service;
	private IHandler handler;
	private boolean isStop;

	private ZmqDealer dealer;
	private String workerId;

	public ZmqWorker(int workerResponsePort, IHandler handler, int index)
	{
		this("localhost", workerResponsePort, handler, index);
	}
	
	public ZmqWorker(String host, int workerResponsePort, IHandler handler, int index)
	{
		this.handler = handler;
		this.service = handler.getServiceName();
		isStop = false;
		gson = new GsonBuilder().create();
		workerId = new ZmqServerUtils().generateWorkerIdStr(handler.getServiceName(), index);
		dealer = new ZmqDealer(workerId.getBytes(), host, workerResponsePort, 100);
	}

	public void stop()
	{
		isStop = true;
	}

	@Override
	public void run()
	{
		try
		{
			logger.info("Zmq worker (" + getWorkerId() + ") started.");

			register();
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e1)
			{
				//
			}

			while (!isStop && !Thread.currentThread().isInterrupted())
			{
				try
				{
					process();
				}
				catch (Exception e)
				{
					logger.error("Worker (" + getWorkerId() + "): Error processing request. Reason: " + e.getMessage());
				}
			}
		}
		finally
		{
			getDealer().close();
			logger.info("Zmq worker (" + getWorkerId() + ") stopped.");
		}
	}

	private void register()
	{
		ZmqWorkerResponse resp = new ZmqWorkerResponse();
		resp.setService(service);
		getDealer().send(gson.toJson(resp));
	}

	protected void process() throws Exception
	{
		String msg = getDealer().receiveStr();

		if (msg == null || msg.isEmpty())
		{
			Thread.sleep(100);
			return;
		}

		logger.debug("Worker [" + getWorkerId() + "] received request: " + msg);

		ZmqWorkerRequest req = convertRequest(msg);

		try
		{
			String ret = getHandler().process(req.getData());
			sendResponse(req.getClientId(), ret);
		}
		catch (Exception e)
		{
			sendError(req.getClientId(), e.getMessage());
		}
	}

	protected ZmqWorkerRequest convertRequest(String msg) throws Exception
	{
		ZmqWorkerRequest req;
		try
		{
			req = gson.fromJson(msg, ZmqWorkerRequest.class);
		}
		catch (JsonSyntaxException e)
		{
			throw new Exception("Invalid worker request format. Request: " + msg);
		}
		return req;
	}

	// For Expandability

	protected void sendResponse(byte[] clientId, String response)
	{
		ZmqWorkerResponse resp = new ZmqWorkerResponse();
		resp.setService(service);
		resp.setClientId(clientId);
		resp.setData(response);
		getDealer().send(gson.toJson(resp));
	}

	protected void sendError(byte[] clientId, String error)
	{
		ZmqWorkerResponse resp = new ZmqWorkerResponse();
		resp.setService(service);
		resp.setClientId(clientId);
		resp.setError(error);
		getDealer().send(gson.toJson(resp));
	}

	protected IHandler getHandler()
	{
		return handler;
	}

	protected ZmqDealer getDealer()
	{
		return dealer;
	}
	
	protected String getWorkerId()
	{
		return workerId;
	}
}
