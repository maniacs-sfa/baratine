/*
 * Copyright (c) 1998-2015 Caucho Technology -- all rights reserved
 *
 * This file is part of Baratine(TM)
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Baratine is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Baratine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Baratine; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.v5.ramp.events;

import io.baratine.service.OnDestroy;
import io.baratine.service.OnInit;
import io.baratine.service.OnLookup;
import io.baratine.service.Service;
import io.baratine.service.ServiceRef;

import java.util.HashMap;
import java.util.Objects;

import com.caucho.v5.bartender.ServerBartender;
import com.caucho.v5.bartender.pod.NodePodAmp;
import com.caucho.v5.bartender.pod.PodBartender;

/**
 * Implementation of the event bus.
 */
@Service
public class EventSchemeImpl
{
//  private ServiceManagerAmp _rampManager;
  
  private HashMap<String,EventNodeActor> _pubSubNodeMap
    = new HashMap<>();
    
  private String _address = "event:";
  
  private EventServerImpl _podServer;
  
  public EventSchemeImpl()
  {
    this("event:");
  }
  
  public EventSchemeImpl(String address)
  {
    Objects.requireNonNull(address);
    
    _address = address;
    
    _podServer = new EventServerImpl(this);
  }
  
  @OnInit
  public void init()
  {
    _podServer.init();
  }
  
  @OnDestroy
  public void destroy()
  {
    _podServer.destroy();
  }
  
  EventServerImpl getEventServer()
  {
    return _podServer;
  }

  /*
  ServiceManager getManager()
  {
    return _rampManager;
  }
  */
  
  public String getName()
  {
    return _address;
  }

  @OnLookup
  public Object onLookup(String path)
  {
    Object value = lookupPath(_address + path);

    return value;
  }

  public Object lookupPath(String path)
  {
    return lookupPubSubNode(path);
  }

  public void subscribeImpl(String address, ServiceRef serviceRef)
  {
    EventNodeActor node = lookupPubSubNode(address);
    node.subscribe(serviceRef);
  }

  EventNodeActor lookupPubSubNode(String address)
  {
    String podName = getPodName(address);
    String path = getSubPath(address);
    
    /*
    if (isLocalPod(podName)) {
      address = "event://" + path;
    }
    */
    
    EventNodeActor actor = _pubSubNodeMap.get(address);

    if (actor == null) {
      if (podName.isEmpty()) {
        actor = new EventNodeActor(this, address);
      }
      else if (isLocalPod(podName)) {
        actor = new EventNodeActorServer(this, podName, address);
      }
      else {
        actor = new EventNodeActorClient(this, podName, address);
      }

      _pubSubNodeMap.put(address, actor);
    }
    
    return actor;
  }
  
  private boolean isLocalPod(String podName)
  {
    NodePodAmp node = _podServer.getPodNode();
    PodBartender pod = _podServer.getPod();

    if (podName.isEmpty()) {
      return true;
    }
    else if (node == null) {
      return true;
    }
    else if (node.index() != 0) {
      return false;
    }
    else {
      return podName.equals(pod.name());
    }
  }

  void onServerUpdate(ServerBartender server)
  {
    for (EventNodeActor node : _pubSubNodeMap.values()) {
      node.onServerUpdate(server);
    }
  }

  /*
  public void subscribeRemote(String podName, 
                              String path,
                              ServiceRef serviceRef)
  {
    getPodServer().subscribeClient(podName, path, serviceRef);

    // TODO Auto-generated method stub
    
  }
  */

  public String getSubPath(String address)
  {
    int p = address.indexOf("://");
    
    if (p < 0) {
      return address;
    }
    
    int q = address.indexOf('/', p + 3);
    
    return address.substring(q);
  }

  public String getPodName(String address)
  {
    int p = address.indexOf("://");
    
    if (p < 0) {
      return "";
    }
    
    int q = address.indexOf('/', p + 3);
    
    if (q > 0) {
      return address.substring(p + 3, q);
    }
    else {
      return address.substring(p + 3);
    }
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[]";
  }
}
