                                          __               __
                                   ____ _/ /_       ____ _/ /_
                                  / __ `/ __/______/ __ `/ __/
                                 / /_/ / /_ /_____/ /_/ / /_
                                 \__,_/\__/       \__,_/\__/


### at-at

Simple ahead-of-time function scheduler. Allows you to schedule the execution of an anonymous function for a point in the future.

Works with babashka too.

#### This Fork

This fork was created to combine a few at-at forks and to add some (simple) tests.

* Original repo https://github.com/overtone/at-at
* Updates to add exception handling https://github.com/liftoffio/at-at
* Updates that fixed the printing of dates (to see am vs pm) https://github.com/adamneilson/at-at

Checkout the source to get the low-down -the readme below is somewhat out of date (PRs welcome!)

[![Clojars Project](https://img.shields.io/clojars/v/net.xlfe/at-at.svg)](https://clojars.org/net.xlfe/at-at)

### Basic Usage

First pull in the lib:

```clj
(use 'overtone.at-at)
```

`at-at` uses `ScheduledThreadPoolExecutor`s behind the scenes which use a thread pool to run the scheduled tasks. You therefore need create a pool before you can get going:

```clj
(def my-pool (mk-pool))
```

It is possible to pass in extra options `:cpu-count`, `:stop-delayed?` and `:stop-periodic?` to further configure your pool. See `mk-pool`'s docstring for further info.

Next, schedule the function of your dreams. Here we schedule the function to execute in 1000 ms from now (i.e. 1 second):

```clj
(at (+ 1000 (now)) #(println "hello from the past!") my-pool)
```

You may also specify a description for the scheduled task using an opts map with a `:desc` key.

Another way of achieving the same result is to use `after` which takes a delaty time in ms from now:

```clj
(after 1000 #(println "hello from the past!") my-pool)
```

You can also schedule functions to occur periodically. Here we schedule the function to execute every second:

```clj
(every 1000 #(println "I am cool!") my-pool)
```

This returns a scheduled-fn which may easily be stopped `stop`:

```clj
(stop *1)
```

Or more forcefully killed with `kill`.

It's also possible to start a periodic repeating fn with an inital delay:

```clj
(every 1000 #(println "I am cool!") my-pool :initial-delay 2000)
```

Finally, you can also schedule tasks for a fixed delay (vs a rate):

```clj
(interspaced 1000 #(println "I am cool!") my-pool)
```

This means that it will wait 1000 ms after the task is completed before
starting the next one.

### Resetting a pool.

When necessary it's possible to stop and reset a given pool:

```clj
(stop-and-reset-pool! my-pool)
```

You may forcefully reset the pool using the `:kill` strategy:

```clj
(stop-and-reset-pool! my-pool :strategy :kill)
```

### Viewing running scheduled tasks.

`at-at` keeps an eye on all the tasks you've scheduled. You can get a set of the current jobs (both scheduled and recurring) using `scheduled-jobs` and you can pretty-print a list of these job using `show-schedule`. The ids shown in the output of `show-schedule` are also accepted in `kill` and `stop`, provided you also specify the associated pool. See the `kill` and `stop` docstrings for more information.

```clj
(def tp (mk-pool))
(after 10000 #(println "hello") tp {:desc "Hello printer"})
(every 5000 #(println "I am still alive!") tp {:desc "Alive task"})
(show-schedule tp)
;; [6][RECUR] created: Thu 12:03:35s, period: 5000ms,  desc: "Alive task
;; [5][SCHED] created: Thu 12:03:32s, starts at: Thu 12:03:42s, desc: "Hello printer
```

### History

at-at was extracted from the awesome music making wonder that is Overtone (http://github.com/overtone/overtone)

### Authors

* Sam Aaron
* Jeff Rose
* Michael Neale
