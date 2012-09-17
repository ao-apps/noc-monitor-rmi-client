/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.callable.CallableTableResultListener;
import com.aoindustries.noc.monitor.common.TableResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientTableResultListener extends CallableTableResultListener {

    RmiClientTableResultListener(RmiClientMonitor monitor, TableResultListener wrapped) throws RemoteException {
        super(monitor, wrapped);
        monitor.exportObject(this);
    }
}
