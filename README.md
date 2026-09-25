# Raw Material Inventory

Java console application that manages raw materials used in production: add/remove materials, receive stock,
issue stock to production, search, sort, and display low-stock reports.

## Run

Use JDK 24 or newer. Open the Maven project in NetBeans and run
`com.mycompany.mavenproject2.Main`, or run these commands from the project folder
with Maven installed:

```sh
mvn compile
java -cp target/classes com.mycompany.mavenproject2.Main
```

## Implementation

- **Doubly linked list:** implemented from scratch; supports insertion, removal,
  and traversal in both directions. Inventory insertion includes an O(n) duplicate-ID check.
- **Linear search:** supports ID, name, category, and supplier searches. First-match
  search is O(1) best case and O(n) average/worst case; finding all matches scans every record.
- **Merge sort:** relinks nodes without random access; O(n log n) time for best,
  average, and worst cases. Supports ascending and descending order.

Stock quantities use whole numbers. Stock movements must be positive; insufficient
stock and quantities exceeding the integer limit are rejected. Records are stored
in memory only and are lost when the application exits.
