/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.unilib.core.impl.screen;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Represents a stack of {@link ScreenRectangle} objects to manage the scissor areas for rendering.
 * This class provides functionality to push new scissor rectangles onto the stack, which are
 * intersected with the current top of the stack to ensure proper nesting of scissor areas.
 * It also allows popping the top scissor rectangle from the stack, adjusting the current
 * scissor area accordingly.
 *
 * @author CDAGaming
 */
public class ScissorStack {
    /**
     * The stack holding the {@link ScreenRectangle} objects.
     */
    private final Deque<ScreenRectangle> stack = new ArrayDeque<>();

    /**
     * Pushes a new {@link ScreenRectangle} (scissor) onto the stack. If the stack is not empty,
     * this method intersects the new scissor with the rectangle currently at the top of the stack.
     * If an intersection exists, it pushes the intersection onto the stack; otherwise,
     * it pushes an empty {@link ScreenRectangle} to signify no scissor area. When the stack is empty,
     * the provided scissor is pushed directly onto the stack.
     *
     * @param scissor The {@link ScreenRectangle} to be pushed onto the stack. Represents a new
     *                scissor area to be applied.
     * @return The {@link ScreenRectangle} that was actually pushed onto the stack. This is either
     * the intersection of the new scissor with the current top of the stack, the new scissor itself,
     * or an empty {@link ScreenRectangle} if there is no intersection.
     */
    public ScreenRectangle push(final ScreenRectangle scissor) {
        final ScreenRectangle last = this.stack.peekLast();
        if (last != null) {
            final ScreenRectangle intersection = scissor.intersection(last);
            final ScreenRectangle lv2 = intersection != null ? intersection : ScreenRectangle.empty();
            this.stack.addLast(lv2);
            return lv2;
        } else {
            this.stack.addLast(scissor);
            return scissor;
        }
    }

    /**
     * Pops the top {@link ScreenRectangle} (scissor) from the stack, adjusting the current scissor
     * area accordingly. If the stack is empty, this method throws an {@link IllegalStateException}.
     * Otherwise, it removes the top of the stack and returns the new top of the stack, which represents
     * the current scissor area after the pop operation.
     *
     * @return The {@link ScreenRectangle} that is now at the top of the stack after the pop operation,
     * or {@code null} if the stack becomes empty after popping.
     * @throws IllegalStateException if the stack is empty before the pop operation, indicating
     *                               an underflow condition.
     */
    @Nullable
    public ScreenRectangle pop() {
        if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
        } else {
            this.stack.removeLast();
            return this.stack.peekLast();
        }
    }
}
