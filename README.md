Simple log record analyzer which groups log records in case only one word is changed.

Main class LogRecordGrouper

Program arguments:

 -i,--input <arg>    Input file path

 -o,--output <arg>   Output file path


Algorithm complexity is O(n).

The scalability of this algorithm is not good. To improve scalability it is a good idea to create not simple HashMap, 
but the tree of maps on the base of words from log record payload. In this case, we will have the ability to move tree
node calculation to separate computers/application. And memory for each tree node data structure (for example separate 
HashMap for each node) will be less than memory usage for current single HashMap.  
