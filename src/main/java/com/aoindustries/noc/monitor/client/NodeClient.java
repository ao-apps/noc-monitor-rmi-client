/*
 * noc-monitor-rmi-client - RMI Client for Network Operations Center Monitoring.
 * Copyright (C) 2008, 2009, 2016, 2018, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.noc.monitor.common.AlertCategory;
import com.aoindustries.noc.monitor.common.AlertLevel;
import com.aoindustries.noc.monitor.common.Node;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.SingleResultNode;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import com.aoindustries.noc.monitor.common.TableResultNode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class NodeClient implements Node {

	private final Node wrapped;

	NodeClient(Node wrapped) {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		this.wrapped = wrapped;
	}

	@Override
	public NodeClient getParent() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return new NodeClient(wrapped.getParent());
	}

	@Override
	public List<? extends NodeClient> getChildren() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		List<? extends Node> children = wrapped.getChildren();

		// Wrap
		List<NodeClient> localWrapped = new ArrayList<>(children.size());
		for(Node child : children) {
			localWrapped.add(wrap(child));
		}

		return Collections.unmodifiableList(localWrapped);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static NodeClient wrap(Node node) {
		if(node instanceof SingleResultNode) return new SingleResultNodeClient((SingleResultNode)node);
		if(node instanceof TableResultNode) return new TableResultNodeClient((TableResultNode)node);
		if(node instanceof TableMultiResultNode) return new TableMultiResultNodeClient((TableMultiResultNode)node);
		if(node instanceof RootNode) return new RootNodeClient((RootNode)node);
		return new NodeClient(node);
	}

	@Override
	public AlertLevel getAlertLevel() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.getAlertLevel();
	}

	@Override
	public String getAlertMessage() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.getAlertMessage();
	}

	@Override
	public AlertCategory getAlertCategory() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.getAlertCategory();
	}

	@Override
	public boolean getAllowsChildren() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.getAllowsChildren();
	}

	@Override
	public String getLabel() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.getLabel();
	}

	@Override
	public boolean equals(Object obj) {
		// Does this incur a round-trip to the server? assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
		// The request times were 1-3 ns, I don't think it does
		if(!(obj instanceof Node)) return false;

		// Unwrap this
		Node thisNode = this;
		while(thisNode instanceof NodeClient) {
			thisNode = ((NodeClient)thisNode).wrapped;
		}

		// Unwrap other
		Node otherNode = (Node)obj;
		while(otherNode instanceof NodeClient) {
			otherNode = ((NodeClient)otherNode).wrapped;
		}

		// Check equals
		assert thisNode != null;
		return thisNode.equals(otherNode);
	}

	@Override
	public int hashCode() {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.hashCode();
	}
}
