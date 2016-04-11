/**
 * Copyright 2016 Semen A Martynov <semen.martynov@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import javax.swing.SwingUtilities;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.remote.WebServer;
import ru.spbstu.icc.kspt.architecture.martynov.representation.AuthorizationDialog;
import ru.spbstu.icc.kspt.architecture.martynov.service.ExpirationCheckService;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 */
public class Main {

	/**
	 * Entry point
	 * 
	 * @param args
	 *            the first one parameter specifies source file, the second -
	 *            receiver file (in case of its absence the result is output in
	 *            the console).
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			private Market market;

			public void run() {
				this.market = new Market();
				MarketService API = new MarketService(market);
				new Thread(new ExpirationCheckService(market)).start();
				new Thread(new WebServer(market)).start();

				AuthorizationDialog authorizationDialog = new AuthorizationDialog(API);
				authorizationDialog.setVisible(true);
			}
		});
	}

}
