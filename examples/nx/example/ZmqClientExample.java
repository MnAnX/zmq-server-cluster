package nx.example;

import nx.server.zmq.ZmqClient;
import nx.server.zmq.ClientRequest;

public class ZmqClientExample
{
	public static void main(String[] args)
	{
		String clientId = "test_client2";		
		ZmqClient client = new ZmqClient(clientId, "localhost", 15000, 1000);
		
		String service = "service2";
		String request = "request2";		
		
		ClientRequest req = new ClientRequest();
		req.setService(service);
		req.setRequest(request);
		
		client.send(req);
		String ret = client.receive();
		
		System.out.println(ret);
		
		client.close();
	}
}
