(ns day13.core
  (:require [file-reader :as fr]
            [clojure.string :as str]))

(defn parse-button-data [input]
  (let [input-str (if (string? input) input (str/join "\n" input))]
    (->> input-str
         (re-seq #"Button A: X\+(\d+), Y\+(\d+)\nButton B: X\+(\d+), Y\+(\d+)\nPrize: X=(\d+), Y=(\d+)")
         (map (fn [[_ ax ay bx by px py]]
                (let [px-long (Long/parseLong px)
                      py-long (Long/parseLong py)]
                  {:button-a {:x (Long/parseLong ax)
                              :y (Long/parseLong ay)}
                   :button-b {:x (Long/parseLong bx)
                              :y (Long/parseLong by)}
                   :prize    {:x px-long
                              :y py-long}
                   :far-prize {:x (+ px-long 10000000000000)
                               :y (+ py-long 10000000000000)}}))))))

(defn solve-button-equation [data use-far-prize?]
  (let [ax (:x (:button-a data))
        ay (:y (:button-a data))
        bx (:x (:button-b data))
        by (:y (:button-b data))
        prize (if use-far-prize? (:far-prize data) (:prize data))
        prize-x (:x prize)
        prize-y (:y prize)
        det (- (* ax by) (* ay bx))]
    (if (zero? det)
      nil
      (let [a (/ (- (* prize-x by) (* prize-y bx)) det)
            b (/ (- (* prize-y ax) (* prize-x ay)) det)]
        (when (and (pos? a) (pos? b) (integer? a) (integer? b))
          {:a (long a) :b (long b)})))))

(defn calculate-cost [solution]
  (if solution
    (+ (* 3 (:a solution)) (:b solution))
    0))

(defn analyze-solutions [data use-far-prize?]
  (let [solution (solve-button-equation data use-far-prize?)
        cost (calculate-cost solution)]
    {:solution solution :cost cost}))

(defn -main []
  (let [input (fr/read-lines "src/day13/input.txt")
        parsed-data (parse-button-data input)
        near-results (map #(analyze-solutions % false) parsed-data)
        far-results (map #(analyze-solutions % true) parsed-data)
        near-total-cost (reduce + (map :cost near-results))
        far-total-cost (reduce + (map :cost far-results))]
    (println near-total-cost)
    (println far-total-cost)))

