(ns closure-studies.core
   (:require (closure-studies.exercises [chapters1-3 :as chapter-one])))

(defn run-exercises
   "Expects a number to a matching chapter/exercise.
    Valid numbers: 1"
   [chapter]
   (cond
      (= 1 chapter) (chapter-one/run-exercises-chapter-one-to-three)
      :else "Exercise not found"))
