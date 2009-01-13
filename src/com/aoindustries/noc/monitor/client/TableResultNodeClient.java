/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.noc.common.TableResult;
import com.aoindustries.noc.common.TableResultListener;
import com.aoindustries.noc.common.TableResultNode;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class TableResultNodeClient extends NodeClient implements TableResultNode {

    final private TableResultNode wrapped;

    TableResultNodeClient(TableResultNode wrapped) {
        super(wrapped);
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        this.wrapped = wrapped;
    }

    @Override
    public void addTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        wrapped.addTableResultListener(tableResultListener);
    }

    @Override
    public void removeTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        wrapped.removeTableResultListener(tableResultListener);
    }

    @Override
    public TableResult getLastResult() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
        
        return wrapped.getLastResult();
    }
}
