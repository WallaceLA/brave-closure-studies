(ns closure-studies.exercises.chapter4-core-in-depth
  (:require [clojure.string]))

(defn hii
  []
  (println "hii"))

(comment
  "Chapter 4 - Core Functions in Depth:"
  "https://www.braveclojure.com/core-functions-in-depth/")
(comment "The 'map' command")

;;If the core sequence functions first, rest, and cons work on a data structure,
;; you can say the data structure implements the sequence abstraction.
;;
;;Lists, vectors, sets, and maps all implement the sequence abstraction,
;; so they all work with map.
(comment
  (defn titleize
    [topic]
    (str topic " for the Brave and True"))

  (map titleize ["Hamsters" "Ragnarok"])
  ; => ("Hamsters for the Brave and True" "Ragnarok for the Brave and True")

  (map titleize '("Empathy" "Decorating"))
  ; => ("Empathy for the Brave and True" "Decorating for the Brave and True")

  (map titleize #{"Elbows" "Soap Carving"})
  ; => ("Elbows for the Brave and True" "Soap Carving for the Brave and True")

  (map #(titleize (second %)) {:uncomfortable-thing "Winking"}))
  ; => ("Winking for the Brave and True"))

;; _________________________________________________________________

(comment "first, rest, and cons")

;; “Can I first, rest, and cons it?”
;; If the answer is yes, you can use the seq library with that data structure.

;; _________________________________________________________________
(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(map unify-diet-data human-consumption critter-consumption)
;; => ({:human 8.1, :critter 0.0}
;; {:human 7.3, :critter 0.2}
;; {:human 6.6, :critter 0.3}
;; {:human 5.0, :critter 1.1})


;; _________________________________________________________________

(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))

;; This function 'stats' works by:
;; 1 - Creates an anonymous functions that receives a parameter and use it as a function to the 'numbers' argument
;; 2 - A vector of functions to be used by the previous anon-func
;; 3 - The 'map' is what iterates over the vector, passing each value as a parameter to the anon-func,
;; fusing all the result into a single list that is returned
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

;; The previous function, 'stats', operates like this if it were to be written in the imperative way
(defn stats-in-steps
  [numbers]
  (let [sum-result (sum numbers)
        count-result (count numbers)
        avg-result (avg numbers)]
    (list sum-result count-result avg-result)))

(stats [3 4 10])
; => (17 3 17/3)
(stats-in-steps [3 4 10])
; => (17 3 17/3)

(stats [80 1 44 13 6])
; => (144 5 144/5)

(stats-in-steps [80 1 44 13 6])
; => (144 5 144/5)

;; _________________________________________________________________

;; Use map to retrieve the value associated with a keyword from a collection of map data structures.
;; Because keywords can be used as functions, you can do this succinctly.
(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities)
; => ("Bruce Wayne" "Peter Parker" "Your mom" "Your dad")

;; _________________________________________________________________
(comment "The 'reduce' command")
;;This section shows a couple of other ways to use it that might not be obvious.


(comment
  "The first use is to transform a map's values, producing a new map with the same keys but with updated values."

  "In this example, reduce treats the argument {:max 30 :min 10} as a sequence of vectors, like ([:max 30] [:min 10]).
   Then, it starts with an empty map (the second argument) and builds it up using the first argument, an anonymous function. "
  (reduce (fn [new-map [key val]]
            (assoc new-map key (inc val)))
          {}
          {:max 30 :min 10})
  ; => {:max 31, :min 11}

  "Same result:"
  (assoc
    (assoc {} :max (inc 30))
    :min (inc 10))
  ; => {:max 31, :min 11}


  "Another use for reduce is to filter out keys from a map based on their value."

  "In the following example, the anonymous function checks whether the value of a key-value pair is
  greater than 4. If it isn't, then the key-value pair is filtered out."
  (reduce (fn [new-map [key val]]
            (if (> val 4)
              (assoc new-map key val)
              new-map))
          {}
          {:human 4.1
           :critter 3.9})
  ; => {:human 4.1}

  "The takeaway here is that reduce is a more flexible function than it first appears.
  Whenever you want to derive a new value from a seqable data structure, reduce will usually be able to do what you need.")


;; _________________________________________________________________
(comment "take, drop, take-while, and drop-while")

(comment
  "take and drop both take two arguments: a number and a sequence.

  take returns the first n elements of the sequence,
  drop returns the sequence with the first n elements removed"
  (take 3 [1 2 3 4 5 6 7 8 9 10])
  ; => (1 2 3)

  (drop 3 [1 2 3 4 5 6 7 8 9 10]))
  ; => (4 5 6 7 8 9 10)

(comment
  "Their cousins take-while and drop-while are a bit more interesting.
  Each takes a predicate function (a function whose return value is evaluated for truth or falsity)
  to determine when it should stop taking or dropping."

  "Suppose, for example, that you had a vector representing entries in your “food” journal.
  Each entry has the month and day, along with what you ate:"
  (def food-journal
    [{:month 1 :day 1 :human 5.3 :critter 2.3}
     {:month 1 :day 2 :human 5.1 :critter 2.0}
     {:month 2 :day 1 :human 4.9 :critter 2.1}
     {:month 2 :day 2 :human 5.0 :critter 2.5}
     {:month 3 :day 1 :human 4.2 :critter 3.3}
     {:month 3 :day 2 :human 4.0 :critter 3.8}
     {:month 4 :day 1 :human 3.7 :critter 3.9}
     {:month 4 :day 2 :human 3.7 :critter 3.6}])

  "With take-while, you can retrieve just January's and February's data.
  take-while traverses the given sequence (in this case, food-journal),
  applying the predicate function to each element."

  "This example uses the anonymous function #(< (:month %) 3) to test whether the journal entry's month is out of range:"
  (take-while #(< (:month %) 3) food-journal)
; => ({:month 1, :day 1, :human 5.3, :critter 2.3}
; {:month 1, :day 2, :human 5.1, :critter 2.0}
; {:month 2, :day 1, :human 4.9, :critter 2.1}
; {:month 2, :day 2, :human 5.0, :critter 2.5})
  "When take-while reaches the first March entry, the anonymous function returns false,
  and take-while returns a sequence of every element it tested until that point."

  "The same idea applies with drop-while except that it keeps dropping elements until one tests true:"
  (drop-while #(< (:month %) 3) food-journal)
  ;=> ({:month 3, :day 1, :human 4.2, :critter 3.3}
  ; {:month 3, :day 2, :human 4.0, :critter 3.8}
  ; {:month 4, :day 1, :human 3.7, :critter 3.9}
  ; {:month 4, :day 2, :human 3.7, :critter 3.6})

  "By using take-while and drop-while together, you can get data for just February and March:"
  (take-while
    #(< (:month %) 4)
    (drop-while #(< (:month %) 2) food-journal))

  "This example uses drop-while to get rid of the January entries, and then it uses take-while on the result
  to keep taking entries until it reaches the first April entry.")


;; _________________________________________________________________
(comment "filter and some")

(comment
  "Use filter to return all elements of a sequence that test true for a predicate function."

  "Here are the journal entries where human consumption is less than five liters:"
  (filter #(< (:human %) 5) food-journal)
  ;=> ({:month 2, :day 1, :human 4.9, :critter 2.1}
  ; {:month 3, :day 1, :human 4.2, :critter 3.3}
  ; {:month 3, :day 2, :human 4.0, :critter 3.8}
  ; {:month 4, :day 1, :human 3.7, :critter 3.9}
  ; {:month 4, :day 2, :human 3.7, :critter 3.6})

  "Why we didn't just use filter in the take-while and drop-while examples earlier?"
  "filter can end up processing all of your data, which isn't always necessary.
  Because the food journal is already sorted by date, we know that take-while will return the data we want without
  having to examine any of the data we won't need. Therefore, take-while can be more efficient.")

(comment
  "Often, you want to know whether a collection contains any values that test true for a predicate function.
  The 'some' function does that, returning the first truthy value (any value that's not false or nil) returned by a predicate function:"
  (some #(> (:critter %) 5) food-journal)
  ; => nil

  (some #(> (:critter %) 3) food-journal)
  ; => true

  "Notice that the return value in the second example is true and not the actual entry that produced the true value.
   The reason is that the anonymous function #(> (:critter %) 3) returns true or false.
   Here's how you could return the entry:"

  (some #(and (> (:critter %) 3) %) food-journal)
  ; => {:month 3 :day 1 :human 4.2 :critter 3.3}

  "Here, a slightly different anonymous function uses 'and' to first check whether the condition (> (:critter %) 3) is true,
   'and' then returns the entry when the condition is indeed true.")


;; _________________________________________________________________
(comment "sort and sort-by")

(comment
  "You can sort elements in ascending order with sort"
  (sort [3 1 2])
  ; => (1 2 3)

  (sort ["aaa" "c" "bb"])
  ; => ("aaa" "bb" "c")

  "If your sorting needs are more complicated, you can use sort-by, which allows you to apply a function (sometimes called a key function)
  to the elements of a sequence and use the values it returns to determine the sort order."
  (sort-by count ["aaa" "c" "bb"]))
  ; => ("c" "bb" "aaa")


;; _________________________________________________________________
(comment "concat")

(comment
  "Finally, concat simply appends the members of one sequence to the end of another:"
  (concat [1 2] [3 4]))
  ; => (1 2 3 4)


;; _________________________________________________________________
(comment "Lazy Seqs")
;A lazy seq is a seq whose members aren't computed until you try to access them.
;Computing a seq's members is called realizing the seq.

(comment
  "Let's take a look at an example"
  (def vampire-database
   {0 {:makes-blood-puns? false, :has-pulse? true  :name "McFishwich"}
    1 {:makes-blood-puns? false, :has-pulse? true  :name "McMackson"}
    2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
    3 {:makes-blood-puns? true,  :has-pulse? true  :name "Mickey Mouse"}})

  "You have a function, vampire-related-details, which takes one second to look up an entry from the database."
  (defn vampire-related-details
   [social-security-number]
   (Thread/sleep 1000)
   (get vampire-database social-security-number))


  "Next, you have a function, vampire?, which returns a record if it passes the vampire test;
  otherwise, it returns false."
  (defn vampire?
   [record]
   (and (:makes-blood-puns? record)
        (not (:has-pulse? record))
        record))


  "Finally, identify-vampire maps Social Security numbers to database records and
  then returns the first record that indicates vampirism."
  (defn identify-vampire
   [social-security-numbers]
   (first (filter vampire?
                  (map vampire-related-details social-security-numbers))))


  "We can use the 'time' operation to measure and print a report of the elapsed time of the code executed"
  (time (vampire-related-details 0))
  ; => "Elapsed time: 1001.042 msecs"
  ; => {:name "McFishwich", :makes-blood-puns? false, :has-pulse? true}

  "A non-lazy implementation of map would first have to apply vampire-related-details to every member of
  social-security-numbers before passing the result to filter. Because you have one million suspects,
  this would take one million seconds, or 12 days, and half your city would be dead by then! Of course,
  if it turns out that the only vampire is the last suspect in the record, it will still take that much
  time with the lazy version, but at least there's a good chance that it won't."


  "Because map is lazy, it doesn't actually apply vampire-related-details to Social Security numbers until
  you try to access the mapped element. In fact, map returns a value almost instantly:"
  (time (def mapped-details (map vampire-related-details (range 0 1000000))))
  ; => "Elapsed time: 0.049 msecs"
  ; => #'user/mapped-details

  "In this example, range returns a lazy sequence consisting of the integers from 0 to 999,999. Then, map
  returns a lazy sequence that is associated with the name mapped-details. Because map didn't actually apply
  vampire-related-details to any of the elements returned by range, the entire operation took barely
  any time—certainly less than 12 days."

  "You can think of a lazy seq as consisting of two parts: a recipe for how to realize the elements of a
  sequence and the elements that have been realized so far.
  When you use map, the lazy seq it returns doesn't include any realized elements yet, but it does have the
  recipe for generating its elements. Every time you try to access an unrealized element, the lazy seq will
  use its recipe to generate the requested element."

  "In the previous example, mapped-details is unrealized. Once you try to access a member of mapped-details,
  it will use its recipe to generate the element you’ve requested, and you’ll incur the
  one-second-per-database-lookup cost:"
  (time (first mapped-details))
  ; => "Elapsed time: 32030.767 msecs"
  ; => {:name "McFishwich", :makes-blood-puns? false, :has-pulse? true}

  "This operation took about 32 seconds. That’s much better than one million seconds, but it’s still 31 seconds
  more than we would have expected."
  "The reason it took 32 seconds is that Clojure chunks its lazy sequences, which just means that whenever
  Clojure has to realize an element, it preemptively realizes some of the next elements as well."

  "Thankfully, lazy seq elements need to be realized only once. Accessing the first element of mapped-details
  again takes almost no time:"
  (time (first mapped-details))
  ; => "Elapsed time: 0.022 msecs"
  ; => {:name "McFishwich", :makes-blood-puns? false, :has-pulse? true}

  ;------------------
  "With all this newfound knowledge, you can efficiently mine the vampire database to find the fanged culprit:"
  (time (identify-vampire (range 0 1000000)))
  "Elapsed time: 32019.912 msecs"
  ; => {:name "Damon Salvatore", :makes-blood-puns? true, :has-pulse? false}
  )


;; _________________________________________________________________
(comment "Infinite Sequences")

(comment
  "Lazy Seqs gives you the ability to construct infinite sequences.
  So far, you’ve only worked with lazy sequences generated from vectors or lists that terminated. However,
  Clojure comes with a few functions to create infinite sequences."

 "One easy way to create an infinite sequence is with repeat, which creates a sequence whose every member is
 the argument you pass:"
 (concat (take 8 (repeat "na")) ["Batman!"])
 ; => ("na" "na" "na" "na" "na" "na" "na" "na" "Batman!")

 "You can also use repeatedly, which will call the provided function to generate each element in the sequence:"
 (take 3 (repeatedly (fn [] (rand-int 10))))
 ; => (1 4 0)

 "Here, the lazy sequence returned by repeatedly generates every new element by calling the anonymous function
 (fn [] (rand-int 10)), which returns a random integer between 0 and 9."
 "A lazy seq’s recipe doesn’t have to specify an endpoint. Functions like first and take, which realize the
 lazy seq, have no way of knowing what will come next in a seq, and if the seq keeps providing elements, well,
 they’ll just keep taking them. You can see this if you construct your own infinite sequence:"
 (defn even-numbers
   ([] (even-numbers 0))
   ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

 (take 10 (even-numbers))
 ; => (0 2 4 6 8 10 12 14 16 18)

 "This example is a bit mind-bending because of its use of recursion. It helps to remember that cons returns a
 new list with an element appended to the given list:"
 (cons 0 '(2 4 6))
 ; => (0 2 4 6)
 "(Incidentally, Lisp programmers call it consing when they use the cons function.)
 In even-numbers, you’re consing to a lazy list, which includes a recipe (a function) for the next element
 (as opposed to consing to a fully realized list)."
 )


;; _________________________________________________________________
(comment "The Collection Abstraction")

(comment
  "The sequence abstraction is about operating on members individually, whereas the collection abstraction is about
  the data structure as a whole. For example, the collection functions count, empty?, and every? aren’t about any
  individual element; they’re about the whole:"
  (empty? [])
  ; => true

  (empty? ["no!"])
  ; => false

  "Now we’ll examine two common collection functions—into and conj—whose similarities can be a bit confusing."
  )

(comment "The 'into' command")
(comment
  "One of the most important collection functions is into. As you now know, many seq functions return a seq rather
  than the original data structure. You’ll probably want to convert the return value back into the original value,
  and into lets you do that:"
  (map identity {:sunlight-reaction "Glitter!"})
  ; => ([:sunlight-reaction "Glitter!"])

  (into {} (map identity {:sunlight-reaction "Glitter!"}))
  ; => {:sunlight-reaction "Glitter!"}
  "Here, the map function returns a sequential data structure after being given a map data structure, and into converts
  the seq back into a map."

  "Other data structures example, with vector"
  (map identity [:garlic :sesame-oil :fried-eggs])
  ; => (:garlic :sesame-oil :fried-eggs)

  (into [] (map identity [:garlic :sesame-oil :fried-eggs]))
  ; => [:garlic :sesame-oil :fried-eggs]


  "In the following example, we start with a vector with two identical entries, map converts it to a list, and then we
  use into to stick the values into a set."
  (map identity [:garlic-clove :garlic-clove])
  ; => (:garlic-clove :garlic-clove)

  (into #{} (map identity [:garlic-clove :garlic-clove]))
  ; => #{:garlic-clove}
  "Because sets only contain unique values, the set ends up with just one value in it."


  "The first argument of into doesn’t have to be empty. Here, the first example shows how you can use into to add
  elements to a map, and the second shows how you can add elements to a vector."
  (into {:favorite-emotion "gloomy"} [[:sunlight-reaction "Glitter!"]])
  ; => {:favorite-emotion "gloomy" :sunlight-reaction "Glitter!"}

  (into ["cherry"] '("pine" "spruce"))
  ; => ["cherry" "pine" "spruce"]

  "And, of course, both arguments can be the same type."
  (into {:favorite-animal "kitty"} {:least-favorite-smell "dog"
                                    :relationship-with-teenager "creepy"})
  ; => {:favorite-animal "kitty"
  ;     :relationship-with-teenager "creepy"
  ;     :least-favorite-smell "dog"}

  "If into were asked to describe its strengths at a job interview, it would say,
  “I’m great at taking two collections and adding all the elements from the second to the first.”")

(comment "The 'conj' command")
(comment
  "conj also adds elements to a collection, but it does it in a slightly different way:"
  (conj [0] [1])
  ; => [0 [1]]

  "It added the entire vector [1] to [0]. Compare this with into:"
  (into [0] [1])
  ; => [0 1]

  "Here’s how we’d do the same with conj:"
  (conj [0] 1)
  ; => [0 1]

  "Notice that the number 1 is passed as a scalar (singular, non-collection) value, whereas into’s second argument
  must be a collection.

  You can supply as many elements to add with conj as you want, and you can also add to other collections like maps:"
  (conj [0] 1 2 3 4)
  ; => [0 1 2 3 4]

  (conj {:time "midnight"} [:place "ye olde cemetarium"])
  ; => {:place "ye olde cemetarium" :time "midnight"}


  "conj and into are so similar that you could even define conj in terms of into:"
  (defn my-conj
    [target & additions]
    (into target additions))

  (my-conj [0] 1 2 3)
  ; => [0 1 2 3]

  "You’ll often see two functions that do the same thing, except one takes a rest parameter (conj) and one takes a
  seqable data structure (into)."
  )


;; _________________________________________________________________
(comment "Function Functions")
(comment "Two of Clojure’s functions, apply and partial, might seem especially weird because they both accept and
return functions. Let’s unweird them.")

(comment "The 'apply' command")
(comment
  "apply explodes a seqable data structure so it can be passed to a function that expects a rest parameter.

  For example, max takes any number of arguments and returns the greatest of all the arguments.
  Here’s how you’d find the greatest number:"
  (max 0 1 2)
  ; => 2

  "But what if you want to find the greatest element of a vector? You can’t just pass the vector to max:"
  #_(max [0 1 2])
  ; => [0 1 2]

  "This doesn’t return the greatest element in the vector because max returns the greatest of all the arguments
  passed to it, and in this case you’re only passing it a vector containing all the numbers you want to compare,
  rather than passing in the numbers as separate arguments.

  'apply' is perfect for this situation:"
  (apply max [0 1 2])
  ; => 2
  "By using apply, it’s as if you called (max 0 1 2).

  You’ll often use apply like this, exploding the elements of a collection so that they get passed to a function as
  separate arguments."


  "Remember how we defined conj in terms of into earlier? Well, we can also define into in terms of conj by using apply:"
  (defn my-into
    [target additions]
    (apply conj target additions))

  (my-into [0] [1 2 3])
  ; => [0 1 2 3]
  "This call to my-into is equivalent to calling (conj [0] 1 2 3)."
  )


(comment "The 'partial' command")
(comment
  "partial takes a function and any number of arguments. It then returns a new function. When you call the returned
  function, it calls the original function with the original arguments you supplied it along with the new arguments."

  (def add10 (partial + 10))
  (add10 3)
  ; == (partial + 10 3)
  ; => 13
  (add10 5)
  ; => 15

  (def add-missing-elements
    (partial conj ["water" "earth" "air"]))

  (add-missing-elements "unobtainium" "adamantium")
  ; => ["water" "earth" "air" "unobtainium" "adamantium"]

  "To help clarify how partial works, here’s how you might define it:"
  (defn my-partial
    [partialized-fn & args]
    (fn [& more-args]
      (apply partialized-fn (into args more-args))))

  (def add20 (my-partial + 20))
  (add20 3)
  ; => 23


  "In this example, the value of add20 is the anonymous function returned by my-partial.
  The anonymous function is defined like this:"
  (fn [& more-args]
    (apply + (into [20] more-args)))

  "In general, you want to use partials when you find you’re repeating the same combination of function and arguments
  in many different contexts. This toy example shows how you could use partial to specialize a logger, creating a warn
  function:"
  (defn lousy-logger
    [log-level message]
    (condp = log-level
      :warn (clojure.string/lower-case message)
      :emergency (clojure.string/upper-case message)))

  (def warn (partial lousy-logger :warn))

  (warn "Red light ahead")
  ; => "red light ahead"
  "This is identical to calling ."
  (lousy-logger :warn "Red light ahead"))


(comment "The 'complement' command")

(comment
  "Earlier you created the identify-vampire function to find one vampire amid a million people. What if you wanted to
  create a function to find all humans? Perhaps you want to send them thank-you cards for not being an undead predator.
  Here’s how you could do it:"
  (defn identify-humans
    [social-security-numbers]
    (filter #(not (vampire? %))
            (map vampire-related-details social-security-numbers)))

  "Look at the first argument to filter, #(not (vampire? %)). It’s so common to want the complement (the negation) of a
  Boolean function that there’s a function, complement, for that:"
  (def not-vampire? (complement vampire?))
  (defn identify-humans
    [social-security-numbers]
    (filter not-vampire?
            (map vampire-related-details social-security-numbers)))

  "Here’s how you might implement complement:"
  (defn my-complement
    [fun]
    (fn [& args]
      (not (apply fun args))))

  (def my-pos? (complement neg?))
  (my-pos? 1)
  ; => true

  (my-pos? -1)
  ; => false

  "As you can see, complement is a humble function. It does one little thing and does it well. complement made it
  trivial to create a not-vampire? function, and anyone reading the code could understand the code’s intention.")
