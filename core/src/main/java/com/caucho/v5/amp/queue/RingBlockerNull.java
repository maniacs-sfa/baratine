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

import java.util.concurrent.TimeUnit;

import com.caucho.v5.amp.spi.ShutdownModeAmp;

/**
 * Ring blocking algorithm.
 */
public final class RingBlockerNull implements RingBlocker {
  @Override
  public final long nextOfferSequence()
  {
    return 0;
  }
  
  @Override
  public final boolean offerWait(long sequence,
                                 long timeout,
                                 TimeUnit unit)
  {
    return false;
  }

  @Override
  public final void offerWake()
  {
  }

  @Override
  public final boolean wake()
  {
    return true;
  }

  @Override
  public final void wakeAll()
  {
    wake();
  }
  
  @Override
  public final long nextPollSequence()
  {
    return 0;
  }
  

  @Override
  public final boolean pollWait(long sequence,
                                long timeout, 
                                TimeUnit unit)
  {
    return false;
  }
  
  @Override
  public final boolean isPollWait()
  {
    return false;
  }

  @Override
  public final void pollWake()
  {
  }
    
  @Override
  public final void onActive()
  {
  }
  
  @Override
  public final void onInit()
  {
  }
  
  @Override
  public final void shutdown(ShutdownModeAmp mode)
  {
  }

  /*
  private void waitForAvailable(long headAlloc, long tail)
  {
    synchronized (_isWait) {
      if (_headAlloc.get() == headAlloc
          && _tail.get() == tail) {
        _isWait.set(true);

        try {
          Thread.interrupted();
          _isWait.wait(1000);
        } catch (Exception e) {
          log.log(Level.FINER, e.toString(), e);
        }
      }
    }
  }
  
  private void wakeAvailable()
  {
    if (_isWait.get()) {
      synchronized (_isWait) {
        long tail = _tail.get();
        long headAlloc = _headAlloc.get();
        
        if (headAlloc - tail < _ringLength || _isClosed) {
          _isWait.set(false);
          _isWait.notifyAll();
        }
      }
    }
  }
  */
}
