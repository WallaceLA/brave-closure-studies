(ns closure-studies.core
   (:require (closure-studies.exercises
                [chapters1-3 :as chapter-one]
                [chapter4-core-in-depth :as chapter-four])))



(defn run-exercises
   "Expects a number to a matching chapter/exercise.
    Valid numbers: 1"
   [chapter]
   (cond
      (<= chapter 3) (chapter-one/run-exercises-chapter-one-to-three)
      (= chapter 4) (chapter-four/hii)
      :else "Exercise not found"))