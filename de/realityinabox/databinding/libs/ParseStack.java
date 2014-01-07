package de.realityinabox.databinding.libs;

/**
 * A stack which keeps track of the recipients of SAX events during parsing.
 * After creation, the parse stack must be assigned a DelegateHandler object.
 * Now every time an element is pushed onto or popped off the stack, the new
 * top of stack is made delegate of the DelegateHandler object. The events may
 * be intercepted by providing a subclass of DelegateHandler, but in the end
 * the events should reach the delegate, which is the innermost element in
 * scope during parsing.
 */
public class ParseStack {
    private java.util.Stack<XMLElement> stack = new java.util.Stack<XMLElement>();
    private DelegateHandler handler = null;

    /**
     * Returns the current delegate handler, or null if none is set.
     */
    public DelegateHandler getHandler() {
        return handler;
    }

    /**
     * Sets a new delegate handler.
     */
    public void setHandler(DelegateHandler handler) {
        this.handler = handler;
    }

    /**
     * Pushes an element onto the top of the stack and makes its parse handler
     * the delegate.
     */
    public void push(XMLElement e) {
        stack.push(e);
        if (handler != null) handler.setDelegate(e.getParseHandler());
    }

    /**
     * Pops an element off the top of the stack, returns it, and makes the parse
     * handler of the element below it the delegate. If the stack is empty, the
     * null delegate is set.
     */
    public XMLElement pop() {
        XMLElement e = stack.pop();
        if (handler != null) {
            if (stack.empty()) handler.setDelegate(null); else
            handler.setDelegate(stack.peek().getParseHandler());
        }
        return e;
    }
}
