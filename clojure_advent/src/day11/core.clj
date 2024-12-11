(ns day11.core
  (:require [file-reader :as fr]
            [clojure.string :as str]))

(def multiplier 2024)

(defn parse-input [input]
  (mapv #(Integer/parseInt %) (str/split input #" ")))

(defn count-digits [number]
  (count (str number)))

(defn split-number [number]
  (let [digits (str number)
        half (quot (count digits) 2)]
    [(Integer/parseInt (subs digits 0 half))
     (Integer/parseInt (subs digits half))]))

(def apply-rule
  (memoize
   (fn [number]
     (if (zero? number)
       [1]
       (if (even? (count-digits number))
         (split-number number)
         [(*' multiplier number)])))))

(def apply-rules-xf
  (mapcat apply-rule))

(defn apply-rules-x-times [input x]
  (reduce (fn [acc _] (into [] apply-rules-xf acc))
          input
          (range x)))

(defn -main
  []
  (let [input (fr/read-lines "src/day11/input.txt")
        int-input (parse-input (first input))]
    (println (count (apply-rules-x-times int-input 25)))
    (println (count (apply-rules-x-times int-input 75)))))

