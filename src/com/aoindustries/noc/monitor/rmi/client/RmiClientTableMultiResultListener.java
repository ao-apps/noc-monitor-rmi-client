/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.noc.monitor.callable.CallableTableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class RmiClientTableMultiResultListener<R extends TableMultiResult> extends CallableTableMultiResultListener<R> {

    RmiClientTableMultiResultListener(RmiClientMonitor monitor, TableMultiResultListener<R> wrapped) throws RemoteException {
        super(monitor, wrapped);
        monitor.exportObject(this);
    }
}
