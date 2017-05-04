package nx.example;

import nx.server.zmq.IHandler;
import nx.server.zmq.ZmqServer;
import nx.server.zmq.components.ZmqWorker;

public class ZmqServerDistributedExample
{
	int clientPort = 15000;
	int workerPort = 15001;

	public void startServer() throws Exception
	{
		ZmqServer server = new ZmqServer(clientPort, workerPort);
		server.start();
	}

	public void startWorker(int index)
	{
		String serverHost = "localhost"; // replace it with remote server host
		ZmqWorker worker = new ZmqWorker(serverHost, workerPort, new ExampleBasicHandler(), index);
		new Thread(worker).start();
	}

	public void startMultipleWorkers(int numWorkers)
	{
		for (int i = 0; i < numWorkers; i++)
		{
			startWorker(i);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		ZmqServerDistributedExample exp = new ZmqServerDistributedExample();
		
		exp.startServer();
		
		Thread.sleep(10);
		
		exp.startMultipleWorkers(10);
	}
}

class ExampleBasicHandler implements IHandler
{
	@Override
	public String getServiceName()
	{
		return "test_service";
	}

	@Override
	public String process(String data) throws Exception
	{
		return "handler: " + data;
	}
}