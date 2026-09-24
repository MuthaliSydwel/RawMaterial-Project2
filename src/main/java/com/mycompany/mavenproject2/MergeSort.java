/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author callm
 */
import java.util.Comparator;

/**
 * MERGE SORT for a DoublyLinkedList, implemented from scratch by relinking nodes.
 */
public class MergeSort {

    private static long comparisons;

    private MergeSort() {
    }

    /**
     * Sorts the list in place.
     *
     * @return the number of key comparisons performed
     */
    public static <T> long sort(DoublyLinkedList<T> list, Comparator<T> cmp) {
        comparisons = 0;
        if (list.size() < 2) return 0;

        // Sort using only the 'next' chain (simpler), then repair 'prev' links and tail.
        list.head = mergeSort(list.head, cmp);

        DoublyLinkedList.Node<T> prev = null;
        DoublyLinkedList.Node<T> current = list.head;
        while (current != null) {
            current.prev = prev;
            prev = current;
            current = current.next;
        }
        list.tail = prev;
        return comparisons;
    }

    private static <T> DoublyLinkedList.Node<T> mergeSort(DoublyLinkedList.Node<T> head, Comparator<T> cmp) {
        if (head == null || head.next == null) return head;   // 0 or 1 node: already sorted

        DoublyLinkedList.Node<T> middle = findMiddle(head);
        DoublyLinkedList.Node<T> rightHalf = middle.next;
        middle.next = null;                                   // cut the list in two

        DoublyLinkedList.Node<T> left = mergeSort(head, cmp);
        DoublyLinkedList.Node<T> right = mergeSort(rightHalf, cmp);
        return merge(left, right, cmp);
    }

    /** Slow/fast pointer technique: 'slow' ends at the last node of the first half. */
    private static <T> DoublyLinkedList.Node<T> findMiddle(DoublyLinkedList.Node<T> head) {
        DoublyLinkedList.Node<T> slow = head;
        DoublyLinkedList.Node<T> fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private static <T> DoublyLinkedList.Node<T> merge(DoublyLinkedList.Node<T> a,
                                                      DoublyLinkedList.Node<T> b,
                                                      Comparator<T> cmp) {
        DoublyLinkedList.Node<T> dummy = new DoublyLinkedList.Node<>(null);
        DoublyLinkedList.Node<T> last = dummy;

        while (a != null && b != null) {
            comparisons++;
            if (cmp.compare(a.data, b.data) <= 0) {           // '<=' keeps the sort stable
                last.next = a;
                a = a.next;
            } else {
                last.next = b;
                b = b.next;
            }
            last = last.next;
        }
        last.next = (a != null) ? a : b;                       // append the remaining run
        return dummy.next;
    }
}