/*
 * noc-monitor-rmi-client - RMI Client for Network Operations Center Monitoring.
 * Copyright (C) 2008-2012, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import java.rmi.RemoteException;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class TableMultiResultNodeClient<R extends TableMultiResult> extends NodeClient implements TableMultiResultNode<R> {

  private final TableMultiResultNode<R> wrapped;

  TableMultiResultNodeClient(TableMultiResultNode<R> wrapped) {
    super(wrapped);
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    this.wrapped = wrapped;
  }

  @Override
  public void addTableMultiResultListener(TableMultiResultListener<? super R> tableMultiResultListener) throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    wrapped.addTableMultiResultListener(tableMultiResultListener);
  }

  @Override
  public void removeTableMultiResultListener(TableMultiResultListener<? super R> tableMultiResultListener) throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    wrapped.removeTableMultiResultListener(tableMultiResultListener);
  }

  @Override
  public List<?> getColumnHeaders() throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    return wrapped.getColumnHeaders();
  }

  @Override
  public List<? extends R> getResults() throws RemoteException {
    assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

    return wrapped.getResults();
  }
}
