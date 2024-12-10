(ns day10.core
  (:require [file-reader :as fr]))

(defn create-map [input]
  (map (fn [line] (map #(Character/digit % 10) line))
       input))

(defn find-trail-heads-in-row [y row]
  (keep-indexed (fn [x height]
                  (when (zero? height) (list y x)))
                row))

(defn find-trail-heads [grid]
  (apply concat
         (map-indexed (fn [y row]
                        (find-trail-heads-in-row y row))
                      grid)))

(defn neighbors [[y x]]
  (list (list (dec y) x) (list (inc y) x) (list y (dec x)) (list y (inc x))))

(defn valid-move? [grid [y x] height]
  (and (< -1 y (count grid))
       (< -1 x (count (first grid)))
       (= (nth (nth grid y) x) (inc height))))

(defn reachable-positions [grid start]
  (loop [queue (conj clojure.lang.PersistentQueue/EMPTY [start 0])
         visited #{}
         nines #{}]
    (if (empty? queue)
      nines
      (let [[pos height] (peek queue)
            new-queue (pop queue)]
        (if (= height 9)
          (recur new-queue visited (conj nines pos))
          (let [next-positions (filter #(and (not (visited %))
                                             (valid-move? grid % height))
                                       (neighbors pos))]
            (recur (into new-queue (map #(vector % (inc height)) next-positions))
                   (into visited next-positions)
                   nines)))))))

(defn trail-head-score [grid start]
  (count (reachable-positions grid start)))

(defn count-distinct-paths [grid start]
  (loop [stack (list [start 0 []])
         paths #{}]
    (if (empty? stack)
      (count paths)
      (let [[pos height path] (first stack)
            new-stack (rest stack)]
        (if (= height 9)
          (recur new-stack (conj paths path))
          (let [next-positions (filter #(valid-move? grid % height) (neighbors pos))
                new-paths (map #(vector % (inc height) (conj path %)) next-positions)]
            (recur (concat new-paths new-stack) paths)))))))

(defn trail-head-rating [grid start]
  (count-distinct-paths grid start))

(defn -main []
  (let [input (fr/read-lines "src/day10/input.txt")
        trail-map (create-map input)
        trail-heads (find-trail-heads trail-map)
        part1-result (reduce + (map #(trail-head-score trail-map %) trail-heads))
        part2-result (reduce + (map #(trail-head-rating trail-map %) trail-heads))]
    (println "Part 1 answer:" part1-result)
    (println "Part 2 answer:" part2-result)))