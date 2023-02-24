/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.impl;

/**
 * Thread Locker, used for Tasks that need to wait for other Tasks
 *
 * @author CDAGaming, wagyourtail
 */
public class LockObject {
    /**
     * Whether this object is currently unlocked
     */
    boolean unlocked = false;

    /**
     * The last thread that was last used to lock the object
     */
    Thread lockThread;

    /**
     * Unlock this object, triggering {@link Object#notifyAll()}
     */
    public synchronized void unlock() {
        unlocked = true;
        this.notifyAll();
    }

    /**
     * Lock this object, storing the current thread as well
     */
    public synchronized void lock() {
        unlocked = false;
        lockThread = Thread.currentThread();
    }

    /**
     * Set the specified {@link Runnable} to be executed when the object is unlocked
     *
     * @param then The target {@link Runnable} to execute
     * @throws InterruptedException if unable to process event
     */
    public synchronized void waitForUnlock(Runnable then) throws InterruptedException {
        if (unlocked) {
            then.run();
            return;
        }
        if (Thread.currentThread() == lockThread) {
            throw new RuntimeException("Attempted to wait on thread that's locking.");
        }
        while (!unlocked) this.wait();
        then.run();
    }
}
