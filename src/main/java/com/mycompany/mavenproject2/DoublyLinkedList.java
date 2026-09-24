/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author callm
 */
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * A generic DOUBLY LINKED LIST implemented from scratch (no java.util collections used).
 *
 * Why a doubly linked list for Raw Material Inventory?
 *  - Materials are received and used up constantly, so records are inserted and removed
 *    at arbitrary positions. Once a node is located, unlinking it is O(1) because the
 *    node knows both its predecessor and successor (no need to track a "previous" pointer
 *    during traversal as a singly linked list would require).
 *  - The list never needs to be resized or shifted (unlike an array).
 *  - Traversal is possible in both directions, and positional access can start from the
 *    nearer end (head or tail), halving the average traversal cost.
 *  - Merge sort works naturally on linked nodes by relinking (no random access needed).
 *
 * Operation costs (n = number of nodes):
 *   addFirst / addLast / removeFirst / removeLast ..... O(1)
 *   insertAt / removeAt / get (by index) .............. O(n)  (at most n/2 steps)
 *   removeNode (node already known) ................... O(1)
 *   traversal (forward or backward) ................... O(n)
 */
public class DoublyLinkedList<T> implements Iterable<T> {

    /** A node holding one element plus links to its neighbours. */
    static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    // Package-private so MergeSort / LinearSearch (same package) can work on the nodes directly.
    Node<T> head;
    Node<T> tail;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /** O(1) */
    public void addFirst(T data) {
        Node<T> node = new Node<>(data);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    /** O(1) */
    public void addLast(T data) {
        Node<T> node = new Node<>(data);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /** Inserts so that the new element ends up at position 'index' (0-based). O(n) */
    public void insertAt(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + ", size " + size);
        }
        if (index == 0) {
            addFirst(data);
        } else if (index == size) {
            addLast(data);
        } else {
            Node<T> current = nodeAt(index);      // node currently at that position
            Node<T> node = new Node<>(data);
            node.prev = current.prev;
            node.next = current;
            current.prev.next = node;
            current.prev = node;
            size++;
        }
    }

    /** O(1) */
    public T removeFirst() {
        if (head == null) throw new NoSuchElementException("List is empty");
        return removeNode(head);
    }

    /** O(1) */
    public T removeLast() {
        if (tail == null) throw new NoSuchElementException("List is empty");
        return removeNode(tail);
    }

    /** O(n) to find the position, O(1) to unlink. */
    public T removeAt(int index) {
        checkIndex(index);
        return removeNode(nodeAt(index));
    }

    /** Unlinks a node that is already known. O(1) thanks to the prev/next links. */
    T removeNode(Node<T> node) {
        if (node.prev == null) head = node.next;
        else node.prev.next = node.next;

        if (node.next == null) tail = node.prev;
        else node.next.prev = node.prev;

        T data = node.data;
        node.prev = null;
        node.next = null;
        size--;
        return data;
    }

    public T get(int index) {
        checkIndex(index);
        return nodeAt(index).data;
    }

    /** Walks from whichever end is closer to the requested index. */
    private Node<T> nodeAt(int index) {
        Node<T> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) current = current.next;
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) current = current.prev;
        }
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + ", size " + size);
        }
    }

    /** Head-to-tail traversal. */
    public void forEachForward(Consumer<T> action) {
        for (Node<T> n = head; n != null; n = n.next) action.accept(n.data);
    }

    /** Tail-to-head traversal (only cheap because the list is doubly linked). */
    public void forEachBackward(Consumer<T> action) {
        for (Node<T> n = tail; n != null; n = n.prev) action.accept(n.data);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
