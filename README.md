# wally

Wally is a library to find functions by structure (inputs and outputs) and not by name or documentation like `doc` or `find-doc`.
Wally is aimed at beginners but it can be useful to seasoned clojure programmers too.

## Usage

The main function on the lib is `find-by-sample`. It'll try to find functions that satisfy the supplied output and inputs and will print the doc for every matching function.
`find-by-sample` will only search for functions on namespaces listed on `white-list-namespaces`:

```clojure
#{"clojure.core"
  "clojure.set"
  "clojure.uuid"
  "clojure.walk"
  "clojure.repl"
  "clojure.string"}
```

If you want to supply your own namespaces you can use `find-by-sample-for-namespaces` but take care as this function will execute *every* function in the supplied namespaces (except for functions terminated on `!`).

Some examples:

```clojure
user=> (use 'wally.core)
nil
user=> (find-by-sample {1 1, 2 3, 3 1, 4 2} [1 2 3 4 4 2 2])
-------------------------
clojure.core/frequencies
([coll])
  Returns a map from distinct items in coll to the number of times
  they appear.
```

```clojure
user=> (find-by-sample #{4} #{1 2 3 4} #{4 5 6 7})
-------------------------
clojure.set/select
([pred xset])
  Returns a set of the elements for which pred is true
-------------------------
clojure.set/intersection
([s1] [s1 s2] [s1 s2 & sets])
  Return a set that is the intersection of the input sets
```

```clojure
user=> (find-by-sample 4 2 2)
-------------------------
clojure.core/unchecked-multiply
([x y])
  Returns the product of x and y, both long.
  Note - uses a primitive operator subject to overflow.
-------------------------
clojure.core/+
([] [x] [x y] [x y & more])
  Returns the sum of nums. (+) returns 0. Does not auto-promote
  longs, will throw on overflow. See also: +'
-------------------------
clojure.core/*
([] [x] [x y] [x y & more])
  Returns the product of nums. (*) returns 1. Does not auto-promote
  longs, will throw on overflow. See also: *'
-------------------------
clojure.core/unchecked-add
([x y])
  Returns the sum of x and y, both long.
  Note - uses a primitive operator subject to overflow.
-------------------------
clojure.core/+'
([] [x] [x y] [x y & more])
  Returns the sum of nums. (+) returns 0. Supports arbitrary precision.
  See also: +
-------------------------
clojure.core/unchecked-multiply-int
([x y])
  Returns the product of x and y, both int.
  Note - uses a primitive operator subject to overflow.
-------------------------
clojure.core/*'
([] [x] [x y] [x y & more])
  Returns the product of nums. (*) returns 1. Supports arbitrary precision.
  See also: *
-------------------------
clojure.core/unchecked-add-int
([x y])
  Returns the sum of x and y, both int.
  Note - uses a primitive operator subject to overflow.
```

## License

Copyright © 2013 Islon Scherer

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
