package example.counter2;

import io.baratine.core.Result;

public interface CounterServiceSync extends CounterService
{
  long getCount();

  long incrementAndGet();

  long decrementAndGet();

  long addAndGet(long value);
}
