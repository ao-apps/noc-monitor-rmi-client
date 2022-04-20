/*
 * noc-monitor-rmi-client - RMI Client for Network Operations Center Monitoring.
 * Copyright (C) 2008, 2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of noc-monitor-rmi-client.
 *
 * noc-monitor-rmi-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * noc-monitor-rmi-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with noc-monitor-rmi-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.noc.monitor.client;

import com.aoindustries.aoserv.client.account.User;
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
// TODO: Implement serialization filters to prevent malicious loading of new classes
public class MonitorClient implements Monitor {

  private final Monitor wrapped;

  public MonitorClient(String server, int port, RMIClientSocketFactory csf) throws RemoteException, NotBoundException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    // Setup RMI
    Registry registry = LocateRegistry.getRegistry(server, port, csf);
    wrapped = (Monitor)registry.lookup("com.aoindustries.noc.monitor.server.MonitorServer");
  }

  @Override
  public RootNodeClient login(Locale locale, User.Name username, String password) throws RemoteException, IOException, SQLException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    return new RootNodeClient(wrapped.login(locale, username, password));
  }
}
