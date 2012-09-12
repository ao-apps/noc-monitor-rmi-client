/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.common.SingleResultNode;
import com.aoindustries.noc.monitor.wrapper.WrappedSingleResultNode;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientSingleResultNode extends WrappedSingleResultNode {

    RmiClientSingleResultNode(RmiClientMonitor monitor, SingleResultNode wrapped) {
        super(monitor, wrapped);
    }
}
