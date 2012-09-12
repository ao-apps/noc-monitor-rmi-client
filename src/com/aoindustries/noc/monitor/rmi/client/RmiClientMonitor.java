/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.rmi.client;

import com.aoindustries.lang.NullArgumentException;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.noc.monitor.common.Monitor;
import com.aoindustries.noc.monitor.common.Node;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.SingleResultNode;
import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import com.aoindustries.noc.monitor.common.TableResultNode;
import com.aoindustries.noc.monitor.wrapper.WrappedMonitor;
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
 * // TODO: Wrap and export callbacks
 *
 * @author  AO Industries, Inc.
 */
public class RmiClientMonitor extends WrappedMonitor {

    private static final Logger logger = Logger.getLogger(RmiClientMonitor.class.getName());

    private static class CacheKey {

        private final String publicAddress;
        private final String listenAddress;
        private final int    port;
        private final String serverAddress;
        private final int    serverPort;

        private CacheKey(
            String publicAddress,
            String listenAddress,
            int port,
            String serverAddress,
            int serverPort
        ) {
            this.publicAddress = publicAddress;
            this.listenAddress = listenAddress;
            this.port          = port;
            this.serverAddress = NullArgumentException.checkNotNull(serverAddress, "serverAddress");
            this.serverPort    = serverPort;
        }

        @Override
        public boolean equals(Object O) {
            if(O==null) return false;
            if(!(O instanceof CacheKey)) return false;
            CacheKey other = (CacheKey)O;
            return
                port==other.port
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
                ^ (port*11)
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
        int port,
        String serverAddress,
        int serverPort
    ) throws RemoteException {
        CacheKey key = new CacheKey(publicAddress, listenAddress, port, serverAddress, serverPort);
        synchronized(cache) {
            RmiClientMonitor client = cache.get(key);
            if(client==null) {
                client = new RmiClientMonitor(publicAddress, listenAddress, port, serverAddress, serverPort);
                cache.put(key, client);
            }
            return client;
        }
    }

    final int port;
    final RMIClientSocketFactory csf;
    final RMIServerSocketFactory ssf;
    private final String serverAddress;
    private final int serverPort;

    private RmiClientMonitor(
        String publicAddress,
        String listenAddress,
        int port,
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

        this.port          = port;
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
        RegistryManager.createRegistry(port, csf, ssf);
        return UnicastRemoteObject.exportObject(obj, port, csf, ssf);
    }

    /**
     * Disconnects on RMI exceptions that indicate RMI connection problem.
     */
    @Override
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException {
        try {
            return callable.call();
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
        } catch(RemoteException err) {
            throw err;
        } catch(RuntimeException err) {
            throw err;
        } catch(Exception err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }

    @Override
    protected RmiClientNode newWrappedNode(Node node) {
        return new RmiClientNode(this, node);
    }

    @Override
    protected RmiClientRootNode newWrappedRootNode(RootNode node) {
        return new RmiClientRootNode(this, node);
    }

    @Override
    protected RmiClientSingleResultNode newWrappedSingleResultNode(SingleResultNode node) {
        return new RmiClientSingleResultNode(this, node);
    }

    @Override
    protected <R extends TableMultiResult> RmiClientTableMultiResultNode<R> newWrappedTableMultiResultNode(TableMultiResultNode<R> node) {
        return new RmiClientTableMultiResultNode<R>(this, node);
    }

    @Override
    protected RmiClientTableResultNode newWrappedTableResultNode(TableResultNode node) {
        return new RmiClientTableResultNode(this, node);
    }
}
