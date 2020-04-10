/*
 * noc-monitor-rmi-client - RMI Client for Network Operations Center Monitoring.
 * Copyright (C) 2008, 2009, 2016, 2020  AO Industries, Inc.
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
 * along with noc-monitor-rmi-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.noc.monitor.common.SingleResult;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import com.aoindustries.noc.monitor.common.SingleResultNode;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class SingleResultNodeClient extends NodeClient implements SingleResultNode {

	final private SingleResultNode wrapped;

	SingleResultNodeClient(SingleResultNode wrapped) {
		super(wrapped);
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		this.wrapped = wrapped;
	}

	@Override
	public void addSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		wrapped.addSingleResultListener(singleResultListener);
	}

	@Override
	public void removeSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		wrapped.removeSingleResultListener(singleResultListener);
	}

	@Override
	public SingleResult getLastResult() throws RemoteException {
		assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

		return wrapped.getLastResult();
	}
}
