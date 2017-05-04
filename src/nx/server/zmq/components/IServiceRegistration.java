package nx.server.zmq.components;

public interface IServiceRegistration
{
	public abstract boolean isServiceRegistered(String service);

	public abstract byte[] getWorker(String service);

	public abstract void onWorkerResponse(String service, byte[] workerId);

}