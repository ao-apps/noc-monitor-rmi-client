/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.common.TableResultNode;
import com.aoindustries.noc.monitor.wrapper.WrappedTableResultNode;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientTableResultNode extends WrappedTableResultNode {

    RmiClientTableResultNode(RmiClientMonitor monitor, TableResultNode wrapped) {
        super(monitor, wrapped);
    }
}
