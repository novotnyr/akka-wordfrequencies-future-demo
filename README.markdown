Akka Word Frequency Counting
============================

Demonstrates the following concepts:

*   composable futures, especially conversion of list of futures to
future of lists.
*   `ask` pattern to retrieve result as a Future
*   synchronized distribution of workload and waiting for result
*   piping Futures to actors

General idea
------------

Let's have a collection of sentences. We will submit them
to the Master actor. This actor maintains a pool of word frequency
counters, represented as actors.

Master blocks until word frequency counters return their corresponding
results and pipes the result to the requesting actor (outer world, in our case).

