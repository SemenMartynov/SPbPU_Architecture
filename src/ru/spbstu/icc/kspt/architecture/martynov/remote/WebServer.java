package ru.spbstu.icc.kspt.architecture.martynov.remote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Anonymous;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.service.InstrumentService;

/**
 * @author Semen Martynov
 *
 *         Access to the market data for the anonymous users.
 */
public class WebServer implements Runnable {
	/**
	 * Socket for new connections
	 */
	private ServerSocket serverSocket;
	/**
	 * Access to the market
	 */
	private Market market;

	/**
	 * Constructor
	 * 
	 * @param market
	 *            for subscribe
	 */
	public WebServer(Market market) {
		this.market = market;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			serverSocket = new ServerSocket(8080);
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new AnonymousUser(socket, market)).start();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Anonymous user
	 */
	static class AnonymousUser extends Anonymous implements Runnable {

		/**
		 * Communication socket
		 */
		private Socket socket;
		/**
		 * Read from stream
		 */
		private InputStream inputStream;
		/**
		 * write to stream
		 */
		private OutputStream outputStream;
		/**
		 * Market data
		 */
		private String log;
		/**
		 * Access to the market for subscription and instrument list update
		 */
		private Market market;

		/**
		 * Constructor
		 * 
		 * @param socket
		 *            for communication
		 * @param market
		 *            for subscription
		 * @throws Throwable
		 *             if an I/O error occurs when creating the I/O streams
		 */
		private AnonymousUser(Socket socket, Market market) throws Throwable {
			this.socket = socket;
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
			market.addConsumer(this);
			this.market = market;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				Integer task = readInputHeaders();
				if (task == 1) {
					InstrumentService instrumentService = new InstrumentService();
					writeResponse(
							"<html><body onload=\"setInterval(function() {window.location.reload();}, 5000);\"><pre>"
									+ instrumentService.getEnabledInstruments(market).toString()
									+ "</pre></body></html>");
				}
				writeResponse("<html><body onload=\"setInterval(function() {window.location.reload();}, 5000);\"><pre>"
						+ log + "</pre></body></html>");
			} catch (Throwable t) {
				/* do nothing */
			} finally {
				try {
					socket.close();
				} catch (Throwable t) {
					/* do nothing */
				}
			}
		}

		/**
		 * Write response to the user.
		 * 
		 * @param data
		 *            from the market
		 * @throws Throwable
		 *             if an I/O error occurs
		 */
		private void writeResponse(String data) throws Throwable {
			String response = "HTTP/1.1 200 OK\r\n" + "Server: SimpleMarketServer/2016-03-10\r\n"
					+ "Content-Type: text/html\r\n" + "Content-Length: " + data.length() + "\r\n"
					+ "Connection: close\r\n\r\n";
			String result = response + data;
			outputStream.write(result.getBytes());
			outputStream.flush();
		}

		/**
		 * Read request from user
		 * 
		 * @return 1 if requested instruments list or 0, if requested market
		 *         data
		 * @throws Throwable
		 *             if an I/O error occurs
		 */
		private Integer readInputHeaders() throws Throwable {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while (true) {
				String reqest = bufferedReader.readLine();
				if (reqest == null || reqest.trim().length() == 0) {
					return 0;
				}
				String[] parts = reqest.split(" ");
				if (parts[1].equalsIgnoreCase("/Instruments")) {
					return 1;
				} else
					return 0;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ru.spbstu.icc.kspt.edu_arh.martynov.domain.IMarketDataConsumer#
		 * sendMarketData(java.lang.String)
		 */
		@Override
		public Boolean sendMarketData(String marketData) {
			log += marketData;
			return true;
		}
	}
}
