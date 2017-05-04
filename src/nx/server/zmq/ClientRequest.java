package nx.server.zmq;

import com.google.gson.GsonBuilder;

public class ClientRequest
{
	private String service;
	private String request;

	public String getService()
	{
		return service;
	}

	public ClientRequest setService(String service)
	{
		this.service = service;
		return this;
	}

	public String getRequest()
	{
		return request;
	}

	public ClientRequest setRequest(String request)
	{
		this.request = request;
		return this;
	}

	public String toJson()
	{
		return new GsonBuilder().create().toJson(this);
	}
}
