/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.callable.CallableSingleResultListener;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientSingleResultListener extends CallableSingleResultListener {

    RmiClientSingleResultListener(RmiClientMonitor monitor, SingleResultListener wrapped) throws RemoteException {
        super(monitor, wrapped);
        monitor.exportObject(this);
    }
}
