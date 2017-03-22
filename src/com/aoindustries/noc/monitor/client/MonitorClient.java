/*
 * Copyright 2008-2009, 2016, 2017 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.noc.monitor.common.Monitor;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.sql.SQLException;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class MonitorClient implements Monitor {

	final private Monitor wrapped;

	public MonitorClient(String server, int port, RMIClientSocketFactory csf) throws RemoteException, NotBoundException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		// Setup RMI
		Registry registry = LocateRegistry.getRegistry(server, port, csf);
		wrapped = (Monitor)registry.lookup("com.aoindustries.noc.monitor.server.MonitorServer");
	}

	@Override
	public RootNodeClient login(Locale locale, UserId username, String password) throws RemoteException, IOException, SQLException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return new RootNodeClient(wrapped.login(locale, username, password));
	}
}
