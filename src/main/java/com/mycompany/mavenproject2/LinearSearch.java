/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author callm
 */
import java.util.function.Predicate;

/**
 * LINEAR SEARCH for a DoublyLinkedList, implemented from scratch.
 *
 * Why linear search is appropriate here:
 *  - Data sorted?      The list is frequently re-ordered / modified and users search on many
 *                      different fields (id, name, category, supplier). The list is often NOT
 *                      sorted on the searched field, and binary search requires sorted data.
 *  - Random access?    A linked list has no O(1) indexing, so binary search would cost O(n)
 *                      just to reach the middle element each step, losing its advantage.
 *  - Data size?        A factory's raw material list is small-to-medium (hundreds/thousands of
 *                      records), so an O(n) scan is fast enough.
 *  - Search frequency? Modest compared with insert/issue operations; keeping the list sorted
 *                      after every change just to enable faster search would cost more than it saves.
 *  - Partial matches:  Name/supplier searches use "contains" matching, which binary search
 *                      cannot do at all.
 *
 * Complexity:  best case O(1) (first element),  average O(n/2) ~ O(n),  worst case O(n)
 *              (last element or not found).  Space O(1).
 */
public class LinearSearch {

    private static long comparisons;

    private LinearSearch() {
    }

    /** Number of elements examined by the most recent search. */
    public static long getLastComparisons() {
        return comparisons;
    }

    /** Returns the first node whose data matches, or null. Stops at the first match. */
    public static <T> DoublyLinkedList.Node<T> findFirst(DoublyLinkedList<T> list, Predicate<T> match) {
        comparisons = 0;
        for (DoublyLinkedList.Node<T> n = list.head; n != null; n = n.next) {
            comparisons++;
            if (match.test(n.data)) return n;
        }
        return null;
    }

    /** Returns a new list containing every matching element. Always scans the whole list. */
    public static <T> DoublyLinkedList<T> findAll(DoublyLinkedList<T> list, Predicate<T> match) {
        comparisons = 0;
        DoublyLinkedList<T> results = new DoublyLinkedList<>();
        for (DoublyLinkedList.Node<T> n = list.head; n != null; n = n.next) {
            comparisons++;
            if (match.test(n.data)) results.addLast(n.data);
        }
        return results;
    }
}
