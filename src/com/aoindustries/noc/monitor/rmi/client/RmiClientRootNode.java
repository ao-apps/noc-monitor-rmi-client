/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.wrapper.WrappedRootNode;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientRootNode extends WrappedRootNode {

    RmiClientRootNode(RmiClientMonitor monitor, RootNode wrapped) {
        super(monitor, wrapped);
    }
}
