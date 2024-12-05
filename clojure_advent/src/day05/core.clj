(ns day05.core 
  (:require [file-reader :as fr]
            [clojure.string :as str]
            [util :as util]))

(defn split-input [lines]
  (let [[rules orders] (split-with (complement str/blank?) lines)]
    [rules (rest orders)]))

; map of rules where key is the rule number and value is a set of rules that must be executed before the key rule
(defn parse-rules [rules]
  (reduce (fn [acc rule]
            (let [[left right] (mapv #(Integer/parseInt %) (str/split rule #"\|"))]
              (update acc right (fnil conj (sorted-set)) left)))
          {}
          rules))
; orders to vector of vectors of integers
(defn parse-orders [orders]
  (mapv #(mapv (fn [x] (Integer/parseInt x)) (str/split % #",")) orders))


(def check-rule
  (memoize 
    (fn [rules-map order-vec current]
      (let [required (get rules-map current (sorted-set))
            current-index (.indexOf order-vec current)]
        (every? #(or (not (.contains order-vec %))
                     (< (.indexOf order-vec %) current-index))
                required)))))

(defn can-execute-orders? [rules-map order-vec]
  (every? #(check-rule rules-map order-vec %) order-vec))

(defn find-middle [order-vec]
  (nth order-vec (quot (count order-vec) 2)))

(defn rearrange-order [rules-map order-vec]
  (loop [remaining order-vec
         executed []]
    (if (empty? remaining)
      executed
      (let [current (first remaining)
            valid? (check-rule rules-map (into executed remaining) current)]
        (if valid?
          (recur (rest remaining) (conj executed current))
          (let [required (get rules-map current (sorted-set))
                missing (filter #(and (not (contains? (set executed) %))
                                      (contains? (set remaining) %))
                                required)]
            (if (empty? missing) 
              (let [next-item (first missing)
                    new-remaining (cons next-item (remove #{next-item} remaining))]
                (recur new-remaining executed)))))))))

(defn process-valid-orders [rules-map orders]
  (let [valid-orders (filter #(can-execute-orders? rules-map %) orders)
        middle-sum (reduce + (map find-middle valid-orders))]
    middle-sum))

(defn process-rearranged-orders [rules-map orders]
  (let [invalid-orders (remove #(can-execute-orders? rules-map %) orders)
        rearranged-orders (map #(rearrange-order rules-map %) invalid-orders)
        rearranged-middle-sum (reduce + (map find-middle rearranged-orders))]
    rearranged-middle-sum))

(defn -main []
  (let [input (fr/read-lines "src/day05/input.txt")
        [rules raw-orders] (split-input input)
        rules-map (parse-rules rules)
        orders (parse-orders raw-orders)
        valid-result (process-valid-orders rules-map orders)
        rearranged-result (process-rearranged-orders rules-map orders)]
    (println "Valid orders: Sum of middle numbers:" valid-result)
    (println "Rearranged orders: Sum of middle numbers:" rearranged-result)
    (println "Performance:")
    (println "  Valid orders processing time:" 
             (util/average-runtime #(process-valid-orders rules-map orders) 100) 
             "ns")
    (println "  Rearranged orders processing time:" 
             (util/average-runtime #(process-rearranged-orders rules-map orders) 100) 
             "ns")))
