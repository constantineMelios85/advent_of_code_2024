(ns day11.core
  (:require [file-reader :as fr]
            [clojure.string :as str])
  (:import (java.math BigInteger)))

(def multiplier (BigInteger. "2024"))

(defn parse-input [input]
  (frequencies (map #(BigInteger. %) (str/split input #" "))))

(defn count-digits [^BigInteger number]
  (count (.toString number)))

(defn split-number [^BigInteger number]
  (let [str-num (.toString number)
        half (quot (count str-num) 2)]
    [(BigInteger. (subs str-num 0 half))
     (BigInteger. (subs str-num half))]))

(defn apply-rule [^BigInteger number count]
  (cond
    (.equals number BigInteger/ZERO) {BigInteger/ONE count}
    (even? (count-digits number)) (let [[a b] (split-number number)]
                                    (merge-with + {a count} {b count}))
    :else {(.multiply number multiplier) count}))

(defn apply-rules [number-freq]
  (reduce-kv (fn [acc number count]
               (merge-with + acc (apply-rule number count)))
             {}
             number-freq))

(defn apply-rules-x-times [input x]
  (loop [current input
         iteration 0]
    (if (= iteration x)
      current
      (recur (apply-rules current) (inc iteration)))))

(defn count-stones [input times]
  (reduce + (vals (apply-rules-x-times input times))))

(defn -main []
  (let [input (fr/read-lines "src/day11/input.txt")
        int-input (parse-input (first input))
        start-time-25 (System/nanoTime)
        result-25 (count-stones int-input 25)
        end-time-25 (System/nanoTime)
        start-time-75 (System/nanoTime)
        result-75 (count-stones int-input 75)
        end-time-75 (System/nanoTime)]
    (println "Result for 25 times:" result-25 "Time taken (ns):" (- end-time-25 start-time-25))
    (println "Result for 75 times:" result-75 "Time taken (ns):" (- end-time-75 start-time-75))))

