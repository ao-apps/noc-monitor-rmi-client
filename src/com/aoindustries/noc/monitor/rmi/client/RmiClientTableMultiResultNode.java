/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import com.aoindustries.noc.monitor.wrapper.WrappedTableMultiResultNode;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientTableMultiResultNode<R extends TableMultiResult> extends WrappedTableMultiResultNode<R> {

    RmiClientTableMultiResultNode(RmiClientMonitor monitor, TableMultiResultNode<R> wrapped) {
        super(monitor, wrapped);
    }
}
