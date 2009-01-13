/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.noc.common.TableMultiResult;
import com.aoindustries.noc.common.TableMultiResultListener;
import com.aoindustries.noc.common.TableMultiResultNode;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class TableMultiResultNodeClient extends NodeClient implements TableMultiResultNode {

    final private TableMultiResultNode wrapped;

    TableMultiResultNodeClient(TableMultiResultNode wrapped) {
        super(wrapped);
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        this.wrapped = wrapped;
    }

    @Override
    public void addTableMultiResultListener(TableMultiResultListener tableMultiResultListener) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        wrapped.addTableMultiResultListener(tableMultiResultListener);
    }

    @Override
    public void removeTableMultiResultListener(TableMultiResultListener tableMultiResultListener) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        wrapped.removeTableMultiResultListener(tableMultiResultListener);
    }

    @Override
    public List<?> getColumnHeaders(Locale locale) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        return wrapped.getColumnHeaders(locale);
    }

    @Override
    public List<? extends TableMultiResult> getResults() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
        
        return wrapped.getResults();
    }
}
