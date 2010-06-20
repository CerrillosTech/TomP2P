/*
 * Copyright 2009 Thomas Bocek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.tomp2p.p2p;
import java.util.Collection;
import java.util.Map;

import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.jboss.netty.buffer.ChannelBuffer;


public interface EvaluatingSchemeDHT
{
	public Collection<Number160> evaluate(Map<PeerAddress, Collection<Number160>> rawKeys);

	public Map<Number160, Data> evaluate(Map<PeerAddress, Map<Number160, Data>> rawData);

	public Object evaluate(Map<PeerAddress, Object> rawObjects);

	public ChannelBuffer evaluate(Map<PeerAddress, ChannelBuffer> rawChannels);
}
