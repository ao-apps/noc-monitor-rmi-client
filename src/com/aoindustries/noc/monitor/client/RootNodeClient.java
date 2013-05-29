/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.noc.monitor.common.NodeSnapshot;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.TreeListener;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class RootNodeClient extends NodeClient implements RootNode {

    final private RootNode wrapped;

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
        for(NodeSnapshot child : snapshot.getChildren()) wrapSnapshot(child);
    }
}
