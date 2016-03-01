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

package io.baratine.io;

import java.util.Objects;

/**
 * {@code InPipe} controls values
 */
abstract public class InPipeBase<T> implements InPipe<T>, InFlow, ResultInPipe<T>
{
  private InFlow _inFlow;
  
  @Override
  public void inFlow(InFlow inFlow)
  {
    Objects.requireNonNull(inFlow);
    
    _inFlow = inFlow;
  }
  
  public InFlow inFlow()
  {
    return _inFlow;
  }
  
  @Override
  public void fail(Throwable exn)
  {
    handle(null, exn, false);
  }

  @Override
  public void credit(int newCredits)
  {
    inFlow().credit(newCredits);
  }
  
  @Override
  public void pause()
  {
    inFlow().pause();
  }
  
  @Override
  public void resume()
  {
    inFlow().resume();
  }
}
