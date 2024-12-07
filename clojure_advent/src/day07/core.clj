(ns day07.core
  (:require [clojure.string :as str]
            [file-reader :as fr]
            [clojure.math.combinatorics :as combo]
            [util :refer [sum-of-list]]))

(defn generate-operator-combinations [n operators]
  (apply combo/cartesian-product (repeat n operators)))

(defn parse-input [input operators]
  (let [[left right] (map str/trim (str/split input #":"))
        numbers (map #(Long/parseLong %) (str/split right #" "))
        operator-combinations (generate-operator-combinations (dec (count numbers)) operators)]
    {:output (Long/parseLong left)
     :input numbers
     :operator-combinations operator-combinations}))

(defn evaluate-expression [numbers operators]
  (reduce (fn [acc [num op]]
            (case op
              "+" (+ acc num)
              "*" (* acc num)
              "-" (- acc num)
              "/" (/ acc num)
              "||" (Long/parseLong (str acc num))))
          (first numbers)
          (map vector (rest numbers) operators)))

(defn has-valid-combinations? [parsed-input operators]
  (let [{:keys [input output]} parsed-input
        operator-combinations (generate-operator-combinations (dec (count input)) operators)]
    (some #(= output (evaluate-expression input %)) operator-combinations)))

(defn -main []
  (let [input (fr/read-lines "src/day07/input.txt")
        parsed-inputs (map #(parse-input % ["+", "*", "||"]) input)
        valid-inputst (filter #(has-valid-combinations? % ["+", "*"]) parsed-inputs)
        valid-inputs-with-conc (filter #(has-valid-combinations? % ["+", "*", "||"]) parsed-inputs)]
    (println "Sum of outputs with + and *:" (sum-of-list (map :output valid-inputst)))
    (println "Sum of outputs with +, *, and ||:" (sum-of-list (map :output valid-inputs-with-conc)))))