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

package com.caucho.v5.amp.stub;

import java.lang.reflect.Modifier;

import com.caucho.v5.amp.ServiceManagerAmp;
import com.caucho.v5.amp.service.ActorFactoryAmp;
import com.caucho.v5.amp.service.ServiceConfig;
import com.caucho.v5.config.Priority;
import com.caucho.v5.inject.impl.ServiceImpl;
import com.caucho.v5.util.L10N;

import io.baratine.inject.Key;

/**
 * Creates an stub factory for a service class.
 */
@Priority(-1000)
public class StubGeneratorService implements StubGenerator
{
  private static final L10N L = new L10N(StubGeneratorService.class);
  
  @Override
  public ActorFactoryAmp factory(Class<?> serviceClass,
                                 ServiceManagerAmp ampManager,
                                 ServiceConfig config)
  {
    if (Modifier.isAbstract(serviceClass.getModifiers())) {
      throw new IllegalArgumentException(L.l("'{0}' is an invalid service because it's abstract",
                                             serviceClass.getSimpleName()));
    }
      
    return createFactory(ampManager, serviceClass, config);
  }
  
  private <T> ActorFactoryAmp createFactory(ServiceManagerAmp ampManager,
                                            Class<T> serviceClass,
                                            ServiceConfig config)
  {
    // XXX: clean up
    Key<T> key = Key.of(serviceClass, ServiceImpl.class);
    
    return new StubFactoryImpl(()->createStub(ampManager, key, config),
                                config);
  }
  
  private static <T> StubAmp createStub(ServiceManagerAmp ampManager,
                                         Key<T> key,
                                         ServiceConfig config)
  {
    T bean = ampManager.inject().instance(key);
    
    return ampManager.createActor(bean, config);
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[]";
  }
}