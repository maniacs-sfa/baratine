/*
 * Copyright (c) 1998-2015 Caucho Technology -- all rights reserved
 *
 * This file is part of Baratine(TM)(TM)
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

package com.caucho.v5.amp.queue;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.caucho.v5.amp.spi.ShutdownModeAmp;


/**
 * Interface for the transaction log.
 */
final class WorkerDeliverSingleThread<M extends MessageDeliver>
  extends WorkerDeliverBase<M>
{
  private final QueueDeliver<M> _queue;
  private final Deliver<M> _deliver;
 
  WorkerDeliverSingleThread(Deliver<M> deliver,
                            Supplier<OutboxDeliver<M>> outboxFactory,
                            OutboxContext<M> outboxContext,
                            Executor executor,
                            ClassLoader loader,
                            QueueDeliver<M> queue)
  {
    super(deliver, outboxFactory, outboxContext, executor, loader);
    
    _queue = queue;
    _deliver = deliver;
  }
  
  protected Deliver<M> getDeliver()
  {
    return _deliver;
  }
  
  @Override
  protected boolean isEmpty()
  {
    return _queue.isEmpty();
  }

  @Override
  public void runImpl(OutboxDeliver<M> outbox, M tailMsg)
    throws Exception
  {
    Deliver<M> deliver = getDeliver();
    QueueDeliver<M> queue = _queue;
    
    try {
      deliver.beforeBatch();
      
      if (tailMsg != null
          && (queue.isEmpty() || ! queue.offer(tailMsg))) {
        // deliver tail message from outbox directly, bypassing queue
        deliver.deliver(tailMsg, outbox);
      }
      
      queue.deliver(deliver, outbox);
    } finally {
      deliver.afterBatch();
    }
  }
  
  @Override
  protected boolean isRunOneValid()
  {
    return _queue.isEmpty();
  }

  @Override
  protected void runOneImpl(OutboxDeliver<M> outbox, M tailMsg)
    throws Exception
  {
    Deliver<M> deliver = getDeliver();
    QueueDeliver<M> queue = _queue;
    
    try {
      deliver.beforeBatch();
      
      if (queue.isEmpty() || ! queue.offer(tailMsg)) {
        // deliver tail message from outbox directly, bypassing queue
        deliver.deliver(tailMsg, outbox);
      }
      else {
        //System.out.println("UNDELIVER: " + tailMsg);
        wake();
      }
    } finally {
      deliver.afterBatch();
    }
  }
  
  /*
  @Override
  public OutboxDeliverMessage<M> getContextOutbox()
  {
    return getOutbox();
  }
  */
 
  @Override
  public void onActive()
  {
    //_deliver.onActive();
    
    super.onActive();
  }
  
  @Override
  public void onInit()
  {
    //_deliver.onInit();
    
    super.onInit();
  }
  
  @Override
  public void shutdown(ShutdownModeAmp mode)
  {
    _deliver.shutdown(mode);
    
    super.shutdown(mode);
  }
  
  public void close()
  {
    //super.shutdown(ShutdownModeAmp.IMMEDIATE);
    shutdown(ShutdownModeAmp.IMMEDIATE);
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + _deliver  + "," + getState() + "]";
  }
}
