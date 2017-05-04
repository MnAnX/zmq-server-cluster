package nx.server.zmq.components;

public class ZmqServerUtils
{
	public String generateWorkerIdStr(String service, int index)
	{
		return service + "_" + index;
	}
	
	public byte[] generateWorkerIdByte(String service, int index)
	{
		return generateWorkerIdStr(service, index).getBytes();
	}	
}
