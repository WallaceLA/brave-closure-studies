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




























