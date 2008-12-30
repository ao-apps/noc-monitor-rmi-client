/*
 * Copyright 2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.noc.common.SingleResultListener;
import com.aoindustries.noc.common.SingleResultNode;
import com.aoindustries.noc.common.SingleResult;
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

    public void addSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        wrapped.addSingleResultListener(singleResultListener);
    }

    public void removeSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        wrapped.removeSingleResultListener(singleResultListener);
    }

    public SingleResult getLastResult() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
        
        return wrapped.getLastResult();
    }
}
