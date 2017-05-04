package nx.server.zmq;

public interface IHandler
{
	String getServiceName();

	String process(String request) throws Exception;
}
