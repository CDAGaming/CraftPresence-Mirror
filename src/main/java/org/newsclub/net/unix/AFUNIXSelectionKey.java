/*
 * junixsocket
 *
 * Copyright 2009-2021 Christian Kohlschütter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.newsclub.net.unix;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.concurrent.atomic.AtomicBoolean;

final class AFUNIXSelectionKey extends SelectionKey {
  private final AFUNIXSelector sel;
  private final AFUNIXSocketCore core;
  private int ops;
  private final SelectableChannel chann;
  private final AtomicBoolean cancelled = new AtomicBoolean();
  private int opsReady;

  AFUNIXSelectionKey(AFUNIXSelector selector, AbstractSelectableChannel ch, int ops, Object att) {
    super();
    this.chann = ch;
    this.sel = selector;
    this.ops = ops; // FIXME check

    if (ch instanceof AFUNIXDatagramChannel) {
      this.core = ((AFUNIXDatagramChannel) ch).getAFCore();
    } else if (ch instanceof AFUNIXSocketChannel) {
      this.core = ((AFUNIXSocketChannel) ch).getAFCore();
    } else if (ch instanceof AFUNIXServerSocketChannel) {
      this.core = ((AFUNIXServerSocketChannel) ch).getAFCore();
    } else {
      throw new UnsupportedOperationException();
    }

    attach(att);
  }

  @Override
  public SelectableChannel channel() {
    return chann;
  }

  @Override
  public Selector selector() {
    return sel;
  }

  @Override
  public boolean isValid() {
    return !cancelled.get() && chann.isOpen() && sel.isOpen();
  }

  @Override
  public void cancel() {
    sel.remove(this);
    cancelNoRemove();
  }

  void cancelNoRemove() {
    if (!cancelled.compareAndSet(false, true) || !chann.isOpen()) {
      return;
    }

    cancel1();
  }

  private void cancel1() {
    // FIXME
  }

  @Override
  public int interestOps() {
    return ops;
  }

  @Override
  public SelectionKey interestOps(int interestOps) {
    this.ops = interestOps; // FIXME check
    return this;
  }

  @Override
  public int readyOps() {
    return opsReady;
  }

  AFUNIXSocketCore getAFCore() {
    return core;
  }

  void setOpsReady(int opsReady) {
    this.opsReady = opsReady;
  }
}