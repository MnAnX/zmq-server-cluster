package nx.server.zmq;

import nx.server.zmq.components.ZmqDealer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ZmqClient
{
	private Gson gson;
	private ZmqDealer client;

	/**
	 * @param clientId
	 * @param host
	 * @param port
	 * @param timeout
	 */
	public ZmqClient(String clientId, String host, int port, Integer timeout)
	{
		gson = new GsonBuilder().create();
		client = new ZmqDealer(clientId.getBytes(), host, port, timeout);
	}

	/**
	 * @param client request
	 */
	public void send(ClientRequest req)
	{
		client.send(gson.toJson(req));
	}

	/**
	 * @return received response
	 */
	public String receive()
	{
		return client.receiveStr();
	}

	public void close()
	{
		client.close();
	}
}
