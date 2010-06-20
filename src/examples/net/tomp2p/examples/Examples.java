/*
 * This file is part of TomP2P.
 * 
 * TomP2P is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * TomP2P is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with TomP2P. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 * TomP2P - A distributed multi map
 * 
 * TomP2P is a Java-based P2P network, which implements a distributed multi map.
 * It implements besides DHT operations (get and put), also add, remove and size
 * operations and further extensions. TomP2P was developed at the University of
 * Zurich, IFI, Communication Systems Group.
 * 
 * Copyright (C) 2009 University of Zurich, Thomas Bocek
 * 
 * @author Thomas Bocek
 */
package net.tomp2p.examples;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.tomp2p.connection.Bindings;
import net.tomp2p.connection.Bindings.Protocol;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;


/**
 * This simple example creates 10 nodes, bootstraps to the first and put and get
 * data from those 10 nodes.
 * 
 * @author draft
 * 
 */
public class Examples
{
	final private static Random rnd = new Random(42L);

	public static void main(String[] args) throws Exception
	{
		exampleDHT();
		exampleNAT();
	}

	public static void exampleDHT() throws Exception
	{
		Peer master = null;
		try
		{
			master = new Peer(new Number160(rnd));
			master.listen(4001, 4001);
			Peer[] nodes = createAndAttachNodes(master, 100);
			bootstrap(master, nodes);
			examplePutGet(nodes);
			exampleAddGet(nodes);
		}
		finally
		{
			master.shutdown();
		}
	}

	public static void exampleNAT() throws Exception
	{
		Peer master = new Peer(new Number160(rnd));
		Bindings b = new Bindings(false);
		b.addInterface("eth0");
		b.addProtocol(Protocol.IPv4);
		b.setOutsideAddress(Inet4Address.getByName("127.0.0.1"), 4001, 4001);
		System.out.println("Listening to: " + b.discoverLocalInterfaces());
		master.listen(4001, 4001, b);
		System.out.println("address visible to outside is " + master.getPeerAddress());
		master.shutdown();
	}

	private static void bootstrap(Peer master, Peer[] nodes)
	{
		List<FutureBootstrap> futures = new ArrayList<FutureBootstrap>();
		for (int i = 1; i < nodes.length; i++)
		{
			FutureBootstrap tmp = nodes[i].bootstrap(master.getPeerAddress());
			futures.add(tmp);
		}
		for (FutureBootstrap future : futures)
			future.awaitUninterruptibly();
	}

	public static void examplePutGet(Peer[] nodes) throws IOException
	{
		Number160 nr = new Number160(rnd);
		String toStore = "hallo";
		Data data = new Data(toStore.getBytes());
		FutureDHT futureDHT = nodes[30].put(nr, data);
		futureDHT.awaitUninterruptibly();
		System.out.println("stored: " + toStore + " (" + futureDHT.isSuccess() + ")");
		futureDHT = nodes[77].get(nr);
		futureDHT.awaitUninterruptibly();
		System.out.println("got: "
				+ new String(futureDHT.getRawData().values().iterator().next().values().iterator()
						.next().getData()) + " (" + futureDHT.isSuccess() + ")");
	}

	private static void exampleAddGet(Peer[] nodes) throws IOException
	{
		Number160 nr = new Number160(rnd);
		String toStore1 = "hallo1";
		String toStore2 = "hallo2";
		Data data1 = new Data(toStore1);
		Data data2 = new Data(toStore2);
		FutureDHT futureDHT = nodes[30].add(nr, data1);
		futureDHT.awaitUninterruptibly();
		System.out.println("added: " + toStore1 + " (" + futureDHT.isSuccess() + ")");
		futureDHT = nodes[50].add(nr, data2);
		futureDHT.awaitUninterruptibly();
		System.out.println("added: " + toStore2 + " (" + futureDHT.isSuccess() + ")");
		futureDHT = nodes[77].getAll(nr);
		futureDHT.awaitUninterruptibly();
		System.out.println("size" + futureDHT.getData().size());
		Iterator<Data> iterator = futureDHT.getData().values().iterator();
		System.out.println("got: " + new String(iterator.next().getData()) + " ("
				+ futureDHT.isSuccess() + ")");
		System.out.println("got: " + new String(iterator.next().getData()) + " ("
				+ futureDHT.isSuccess() + ")");
	}

	private static Peer[] createAndAttachNodes(Peer master, int nr) throws Exception
	{
		Peer[] nodes = new Peer[nr];
		nodes[0] = master;
		for (int i = 1; i < nr; i++)
		{
			nodes[i] = new Peer(new Number160(rnd));
			nodes[i].listen(master);
		}
		return nodes;
	}
	/*
	 * public static void examplePutGet() throws IOException { Node[] n =
	 * createNodes(); int data = 1234567890; // store the data under the key 6
	 * n[2].put(new Number160("6"), data); // get the result, this is a blocking
	 * operation, you may want to use the // non-blocking method int result =
	 * (Integer) n[8].get(new Number160("6"));
	 * System.out.println("Node 2  stored " + data + " and node 8 back " +
	 * result); shutdown(n); }
	 * 
	 * public static void exampleAddGet() throws IOException { Node[] n =
	 * createNodes(); int data = 1234567890; int data2 = 234567890; // store the
	 * data under the key 6 n[2].add(new Number160("6"), data); n[4].add(new
	 * Number160("6"), data2); // get the result, this is a blocking operation,
	 * you may want to use the // non-blocking method Collection<Object> results
	 * = n[8].getAll(new Number160("6")); for (Object o : results)
	 * System.out.println("Node 2 and 4  stored " + data + " and " + data2 +
	 * ". Node 8 returns " + o); shutdown(n); }
	 * 
	 * private static Node[] createNodes() throws IOException { Node[] n = new
	 * Node[10]; for (int i = 0; i < n.length; i++) { n[i] = new
	 * Node(Utils.createRandomNodeID()); // listen on UDP and TCP port 4000
	 * n[i].listen(4000, 4000); // the bootstrap node does not want to bootstrap
	 * to itself if (i != 0) { FutureRouting futureBootstrap =
	 * n[i].bootstrap(n[0]); // wait until the bootstrap is done
	 * futureBootstrap.awaitUninterruptibly(); } } return n; }
	 * 
	 * private static void shutdown(Node[] n) { for (int i = 0; i < n.length;
	 * i++) n[i].shutdown(); // release all resources. There are some executors
	 * still active. }
	 */
}
