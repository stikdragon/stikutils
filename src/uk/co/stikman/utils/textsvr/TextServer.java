package uk.co.stikman.utils.textsvr;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.stikman.utils.StikDataInputStream;
import uk.co.stikman.utils.StikDataOutputStream;

public abstract class TextServer {
	public static final int								SOCKET_READ_TIMEOUT	= 5000;

	protected static final Charset						CHARSET				= Charset.forName("UTF-8");

	private int											port;
	private ServerSocket								socket;
	private Set<Socket>									open				= new HashSet<Socket>();
	private Thread										mainthread;
	private ExecutorService								exec				= new ThreadPoolExecutor(0, 32, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	private boolean										stopping;
	private ConvoLog									convolog			= null;

	private HashMap<String, SocketAddressUseTracker>	ipTracker;

	private static class SocketAddressUseTracker {
		private SocketAddress	addr;
		private int				count;
		private int				id;

		public SocketAddressUseTracker(int id, SocketAddress addr) {
			this.id = id;
			this.addr = addr;
		}

		public int getCount() {
			return count;
		}

		public int inc() {
			return ++count;
		}

		public int getId() {
			return id;
		}
	}

	public TextServer(int port) {
		this.port = port;
	}

	public void enableNetworkLogging(String filename) {
		if (filename == null) {
			if (convolog != null)
				try {
					convolog.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			convolog = null;
		} else {
			convolog = new ConvoLog(new File(filename));
		}
	}

	public void disableNetworkLogging() {
		enableNetworkLogging(null);
	}

	public void start() throws IOException {
		socket = new ServerSocket();
		socket.bind(new InetSocketAddress(port));
		mainthread = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					try {
						handleConnection(socket.accept());
					} catch (IOException e) {
						if (!stopping)
							e.printStackTrace(); // TODO: logging
					}
				} while (!socket.isClosed());
			}
		});
		mainthread.setDaemon(true);
		mainthread.setName("TextServer Accept Thread");
		mainthread.start();
	}

	protected void handleConnection(final Socket sock) throws SocketException {
		synchronized (open) {
			open.add(sock);
		}
		sock.setSoTimeout(0);
		Runnable t = new Runnable() {
			@Override
			public void run() {
				InputStream inputStream = null;
				OutputStream outputStream = null;
				try {
					try {
						inputStream = sock.getInputStream();
						outputStream = sock.getOutputStream();
						try (StikDataInputStream dis = new StikDataInputStream(inputStream, CHARSET); StikDataOutputStream dos = new StikDataOutputStream(outputStream, CHARSET)) {
							while (sock.isConnected()) {
								Request req = new Request(dis.readString());
								//System.out.println(req.getData());
								String resp = handleRequest(req);
								if (convolog != null) {
									SocketAddressUseTracker id = getIpId(sock.getRemoteSocketAddress());
									if (id.getCount() == 1)
										convolog.logNoSize("New IP", id.getId() + " = " + id.addr.toString());
									id.inc();
									convolog.log(id.getId() + ": SEND", req.getData());
									convolog.log(id.getId() + ": RECV", resp);
								}
								dos.writeString(resp);
								dos.flush();
								outputStream.flush();
							}
						}
					} catch (EOFException eof) {
						// connection closed
					} catch (IOException e) {
						log(e);
					}
				} finally {
					close(inputStream);
					close(outputStream);
					close(sock);
					synchronized (open) {
						open.remove(sock);
					}
				}
			}
		};
		exec.execute(t);
	}

	protected synchronized SocketAddressUseTracker getIpId(SocketAddress addr) {
		if (ipTracker == null)
			ipTracker = new HashMap<String, SocketAddressUseTracker>();
		SocketAddressUseTracker i = ipTracker.get(addr.toString());
		if (i == null) {
			SocketAddressUseTracker x = new SocketAddressUseTracker(ipTracker.size() + 1, addr);
			x.inc();
			ipTracker.put(addr.toString(), x);
			return x;
		}
		return i;
	}

	protected abstract String handleRequest(Request req);

	protected void close(Closeable x) {
		if (x != null) {
			try {
				x.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void log(String msg) {
		System.out.println(msg);
	}

	protected void log(Throwable e) {
		e.printStackTrace();
	}

	public void stop() {
		try {
			stopping = true;
			close(socket);
			closeAllConnections();
			exec.shutdown();
			if (mainthread != null)
				mainthread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void closeAllConnections() {
		synchronized (open) {
			List<Socket> lst = new ArrayList<>(open);
			for (Socket socket : lst)
				close(socket);
		}
	}

}
