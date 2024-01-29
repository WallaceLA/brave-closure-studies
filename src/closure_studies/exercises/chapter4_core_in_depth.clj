(ns closure-studies.exercises.chapter4-core-in-depth)

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
  "The first use is to transform a map’s values, producing a new map with the same keys but with updated values."

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
  greater than 4. If it isn’t, then the key-value pair is filtered out."
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

  "With take-while, you can retrieve just January’s and February’s data.
  take-while traverses the given sequence (in this case, food-journal),
  applying the predicate function to each element."

  "This example uses the anonymous function #(< (:month %) 3) to test whether the journal entry’s month is out of range:"
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

  "Why we didn’t just use filter in the take-while and drop-while examples earlier?"
  "filter can end up processing all of your data, which isn’t always necessary.
  Because the food journal is already sorted by date, we know that take-while will return the data we want without
  having to examine any of the data we won’t need. Therefore, take-while can be more efficient.")

(comment
  "Often, you want to know whether a collection contains any values that test true for a predicate function.
  The 'some' function does that, returning the first truthy value (any value that’s not false or nil) returned by a predicate function:"
  (some #(> (:critter %) 5) food-journal)
  ; => nil

  (some #(> (:critter %) 3) food-journal)
  ; => true

  "Notice that the return value in the second example is true and not the actual entry that produced the true value.
   The reason is that the anonymous function #(> (:critter %) 3) returns true or false.
   Here’s how you could return the entry:"

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






































