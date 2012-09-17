/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.lang.NullArgumentException;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.noc.monitor.callable.CallableMonitor;
import com.aoindustries.noc.monitor.common.Monitor;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableResultListener;
import com.aoindustries.noc.monitor.common.TreeListener;
import com.aoindustries.rmi.RMIClientSocketFactorySSL;
import com.aoindustries.rmi.RMIServerSocketFactorySSL;
import com.aoindustries.rmi.RegistryManager;
import java.rmi.ConnectException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RMI server for wrapping and exposing monitors to the network.
 *
 * Exports the monitor and all nodes.  The wrapped monitor is not exported directly,
 * but rather this wrapper of it is exported.
 *
 * // TODO: Re-add callbacks on reconnect?
 *
 * @author  AO Industries, Inc.
 */
public class RmiClientMonitor extends CallableMonitor {

    private static final Logger logger = Logger.getLogger(RmiClientMonitor.class.getName());

    private static class CacheKey {

        private final String publicAddress;
        private final String listenAddress;
        private final int    listenPort;
        private final String serverAddress;
        private final int    serverPort;

        private CacheKey(
            String publicAddress,
            String listenAddress,
            int listenPort,
            String serverAddress,
            int serverPort
        ) {
            this.publicAddress = publicAddress;
            this.listenAddress = listenAddress;
            this.listenPort    = listenPort;
            this.serverAddress = NullArgumentException.checkNotNull(serverAddress, "serverAddress");
            this.serverPort    = serverPort;
        }

        @Override
        public boolean equals(Object O) {
            if(O==null) return false;
            if(!(O instanceof CacheKey)) return false;
            CacheKey other = (CacheKey)O;
            return
                listenPort==other.listenPort
                && serverPort==other.serverPort
                && ObjectUtils.equals(publicAddress, other.publicAddress)
                && ObjectUtils.equals(listenAddress, other.listenAddress)
                && serverAddress.equals(other.serverAddress)
            ;
        }

        @Override
        public int hashCode() {
            return
                ObjectUtils.hashCode(publicAddress)
                ^ (ObjectUtils.hashCode(listenAddress)*7)
                ^ (listenPort*11)
                ^ (serverAddress.hashCode()*13)
                ^ (serverPort*17)
            ;
        }
    }

    private static final Map<CacheKey,RmiClientMonitor> cache = new HashMap<CacheKey,RmiClientMonitor>();

    /**
     * One unique RmiClientMonitor is created for each set addresses and ports.
     */
    public static RmiClientMonitor getInstance(
        String publicAddress,
        String listenAddress,
        int listenPort,
        String serverAddress,
        int serverPort
    ) throws RemoteException {
        CacheKey key = new CacheKey(publicAddress, listenAddress, listenPort, serverAddress, serverPort);
        synchronized(cache) {
            RmiClientMonitor client = cache.get(key);
            if(client==null) {
                client = new RmiClientMonitor(publicAddress, listenAddress, listenPort, serverAddress, serverPort);
                cache.put(key, client);
            }
            return client;
        }
    }

    final int listenPort;
    final RMIClientSocketFactory csf;
    final RMIServerSocketFactory ssf;
    private final String serverAddress;
    private final int serverPort;

    private RmiClientMonitor(
        String publicAddress,
        String listenAddress,
        int listenPort,
        String serverAddress,
        int serverPort
    ) throws RemoteException {
        // Setup the RMI system properties
        if(publicAddress!=null && publicAddress.length()>0) {
            System.setProperty("java.rmi.server.hostname", publicAddress);
        } else if(listenAddress!=null && listenAddress.length()>0) {
            System.setProperty("java.rmi.server.hostname", listenAddress);
        } else {
            System.clearProperty("java.rmi.server.hostname");
        }
        System.setProperty("java.rmi.server.randomIDs", "true");
        System.setProperty("java.rmi.server.useCodebaseOnly", "true");
        System.clearProperty("java.rmi.server.codebase");
        System.setProperty("java.rmi.server.disableHttp", "true");
        // System.setProperty("sun.rmi.server.suppressStackTraces", "true");

        // RMI socket factories
        if(listenAddress!=null && listenAddress.length()>0) {
            csf = new RMIClientSocketFactorySSL(listenAddress); // csf = new RMIClientSocketFactorySSL();
            ssf = new RMIServerSocketFactorySSL(listenAddress);
        } else {
            csf = new RMIClientSocketFactorySSL();
            ssf = new RMIServerSocketFactorySSL();
        }

        this.listenPort    = listenPort;
        this.serverAddress = serverAddress;
        this.serverPort    = serverPort;

        // Connect immediately in order to have the chance to throw exceptions that will occur during connection
        getWrapped();
    }

    @Override
    protected Monitor connect() throws RemoteException {
        try {
            // Connect to the remote registry and get each of the stubs
            Registry remoteRegistry = LocateRegistry.getRegistry(serverAddress, serverPort, csf);
            return (Monitor)remoteRegistry.lookup(Monitor.class.getName()+"_Stub");
        } catch(NotBoundException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }

    /**
     * Creates the local registry if not yet created, then exports the object.
     */
    Remote exportObject(Remote obj) throws RemoteException {
        RegistryManager.createRegistry(listenPort, csf, ssf);
        return UnicastRemoteObject.exportObject(obj, listenPort, csf, ssf);
    }

    /**
     * Tries for up to ten seconds to gracefully unexport an object.  If still not successful, logs a warning and forcefully unexports.
     *
     * TODO: Should we unexport listeners after they are removed, or just leave it to the garbage collector?
     * TODO: Same question about objects exported from server.
     */
    /*
    void unexportObject(Remote remote) {
        assert !SwingUtilities.isEventDispatchThread() : "Running in Swing event dispatch thread";
        try {
            boolean unexported = false;
            for(int c=0;c<100;c++) {
                if(UnicastRemoteObject.unexportObject(remote, false)) {
                    unexported = true;
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch(InterruptedException err) {
                    logger.log(Level.WARNING, null, err);
                }
            }
            if(!unexported) {
                logger.log(Level.WARNING, null, new RuntimeException("Unable to unexport Object, now being forceful"));
                UnicastRemoteObject.unexportObject(remote, true);
            }
        } catch(NoSuchObjectException err) {
            logger.log(Level.WARNING, null, err);
        }
    }*/

    /**
     * Disconnects on RMI exceptions that indicate RMI connection problem.
     */
    @Override
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException {
        try {
            return super.call(callable, allowRetry);
        } catch(NoSuchObjectException err) {
            try {
                disconnect();
            } catch(RemoteException err2) {
                logger.log(Level.SEVERE, null, err2);
            }
            throw err;
        } catch(ConnectException err) {
            try {
                disconnect();
            } catch(RemoteException err2) {
                logger.log(Level.SEVERE, null, err2);
            }
            throw err;
        } catch(MarshalException err) {
            try {
                disconnect();
            } catch(RemoteException err2) {
                logger.log(Level.SEVERE, null, err2);
            }
            throw err;
        }
    }

    @Override
    protected RmiClientTreeListener newWrappedTreeListener(TreeListener treeListener) throws RemoteException {
        return new RmiClientTreeListener(this, treeListener);
    }

    @Override
    protected RmiClientSingleResultListener newWrappedSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        return new RmiClientSingleResultListener(this, singleResultListener);
    }

    @Override
    protected <R extends TableMultiResult> RmiClientTableMultiResultListener<R> newWrappedTableMultiResultListener(TableMultiResultListener<R> tableMultiResultListener) throws RemoteException {
        return new RmiClientTableMultiResultListener<R>(this, tableMultiResultListener);
    }

    @Override
    protected RmiClientTableResultListener newWrappedTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        return new RmiClientTableResultListener(this, tableResultListener);
    }
}
