/*
 * Copyright 2008-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.client;

import com.aoindustries.noc.common.AlertLevel;
import com.aoindustries.noc.common.SingleResultNode;
import com.aoindustries.noc.common.Node;
import com.aoindustries.noc.common.RootNode;
import com.aoindustries.noc.common.TableMultiResultNode;
import com.aoindustries.noc.common.TableResultNode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class NodeClient implements Node {

    final private Node wrapped;

    NodeClient(Node wrapped) {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        this.wrapped = wrapped;
    }

    @Override
    public Node getParent() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
        
        return new NodeClient(wrapped.getParent());
    }

    @Override
    public List<? extends Node> getChildren() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        List<? extends Node> children = wrapped.getChildren();

        // Wrap
        List<Node> localWrapped = new ArrayList<Node>(children.size());
        for(Node child : children) {
            localWrapped.add(wrap(child));
        }
        
        return Collections.unmodifiableList(localWrapped);
    }

    @SuppressWarnings("unchecked")
    static Node wrap(Node node) {
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
    public boolean getAllowsChildren() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        return wrapped.getAllowsChildren();
    }

    @Override
    public String getId() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        return wrapped.getId();
    }

    @Override
    public String getLabel() throws RemoteException {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        return wrapped.getLabel();
    }
    
    @Override
    public boolean equals(Object O) {
        // Does this incur a round-trip to the server? assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
        // The request times were 1-3 ns, I don't think it does
        if(O==null) return false;
        if(!(O instanceof Node)) return false;

        // Unwrap this
        Node thisNode = this;
        while(thisNode instanceof NodeClient) thisNode = ((NodeClient)thisNode).wrapped;

        // Unwrap other
        Node otherNode = (Node)O;
        while(otherNode instanceof NodeClient) otherNode = ((NodeClient)otherNode).wrapped;

        // Check equals
        return thisNode.equals(otherNode);
    }

    @Override
    public int hashCode() {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";

        return wrapped.hashCode();
    }
}
