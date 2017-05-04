# ZmqServer
Socket server communicates with ZMQ

It's a lightweight, easily embedded master-slave server that communicates through ZeroMQ. You can add workers either on the localhost or remote hosts. Server dispatches workload evenly amongst all available workers.

Single server

An example of a single host server:

public static void main(String[] args) throws Exception {
  	ZmqServer server = new ZmqServer(clientPort, workerPort);

  	server.addHandler(new ExampleMsgHandler1(), 2);	// handler1 serves as 'service1', scales by 2.
  	server.addHandler(new ExampleMsgHandler2(), 3);	// hanlder2 serves as 'service2', scales by 3.

  	server.start();
  }
And to implement a handler, you focus on business logic:

class ExampleMsgHandler1 implements IHandler {
	@Override
	public String getServiceName(){
		return "service1";
	}
	@Override
	public String process(String request) throws Exception{
		return "handler1: " + request;
	}
}
As you add the handler to the server, it gets wrapped to a proper worker and interacts with the server.

Distributed

You can also create workers on remote server and distribute the system:

Firstly start the server (and maybe a couple workers) on one host:

public void startServer() throws Exception {
	ZmqServer server = new ZmqServer(clientPort, workerPort);
	server.start();
}
Then create more workers on remote hosts:

public void startWorker(int index) {
	ZmqWorker worker = new ZmqWorker(serverHost, workerPort, new ExampleBasicHandler(), index);
	new Thread(worker).start();
}
