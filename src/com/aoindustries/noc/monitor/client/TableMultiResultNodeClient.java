/*
 * Copyright 2008-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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

    final private TableMultiResultNode<R> wrapped;

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
