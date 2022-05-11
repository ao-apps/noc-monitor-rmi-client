/*
 * noc-monitor-rmi-client - RMI Client for Network Operations Center Monitoring.
 * Copyright (C) 2008, 2009, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.noc.monitor.common.NodeSnapshot;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.TreeListener;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

/**
 * RMI client for {@link RootNode}.
 *
 * @author  AO Industries, Inc.
 */
public class RootNodeClient extends NodeClient implements RootNode {

  private final RootNode wrapped;

  RootNodeClient(RootNode wrapped) {
    super(wrapped);
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    this.wrapped = wrapped;
  }

  @Override
  public void addTreeListener(TreeListener treeListener) throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    wrapped.addTreeListener(treeListener);
  }

  @Override
  public void removeTreeListener(TreeListener treeListener) throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    wrapped.removeTreeListener(treeListener);
  }

  @Override
  public NodeSnapshot getSnapshot() throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    NodeSnapshot nodeSnapshot = wrapped.getSnapshot();
    wrapSnapshot(nodeSnapshot);
    return nodeSnapshot;
  }

  /**
   * Recursively wraps the nodes of the snapshot.
   */
  private static void wrapSnapshot(NodeSnapshot snapshot) {
    snapshot.setNode(NodeClient.wrap(snapshot.getNode()));
    for (NodeSnapshot child : snapshot.getChildren()) {
      wrapSnapshot(child);
    }
  }
}
