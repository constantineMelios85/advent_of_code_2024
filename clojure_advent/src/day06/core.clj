(ns day06.core
  (:require [file-reader :as fr]
            [clojure.string :as str]))

(def guard-position (atom nil))
(def current-direction (atom nil))
(def visited-positions (atom #{}))
(def directions {"^" [-1 0] ">" [0 1] "v" [1 0]  "<" [0 -1]})
(def move-seq (vec (keys directions)))

(defn find-coordinates [grid]
  (let [rows (count grid)
        cols (count (first grid))]
    (reduce (fn [acc row]
              (reduce (fn [acc col]
                        (let [char (get-in grid [row col])]
                          (cond
                            (= char \^) (assoc acc :guard [row col])
                            (= char \#) (update acc :obstacles conj [row col])
                            :else acc)))
                      acc
                      (range cols)))
            {:guard nil :obstacles [] :rows rows :cols cols}
            (range rows))))

(defn change-direction []
  (let [current @current-direction
        idx (.indexOf move-seq current)
        next-idx (mod (inc idx) (count move-seq))]
    (reset! current-direction (nth move-seq next-idx))))

(defn in-grid? [pos rows cols]
  (let [[x y] pos]
    (and (>= x 0) (< x cols) (>= y 0) (< y rows))))

(defn patrol [obstacles rows cols]
  (loop []
    (let [[dx dy] (directions @current-direction)
          [x y] @guard-position
          new-pos [(+ x dx) (+ y dy)]]
      (if (not (in-grid? new-pos rows cols))
        ()
        (if (some #{new-pos} obstacles)
          (do 
            (change-direction)
            (recur))
          (do
            (reset! guard-position new-pos)
            (swap! visited-positions conj new-pos)
            (recur)))))))

(defn -main []
  (let [input (fr/read-lines "src/day06/input.txt")
        grid (mapv str/trim input)
        coordinates (find-coordinates grid)]
    (reset! guard-position (:guard coordinates))
    (reset! current-direction (first move-seq))
    (reset! visited-positions #{@guard-position}) 
    (patrol (:obstacles coordinates) (:rows coordinates) (:cols coordinates))
    (println "Unique Spaces Counter:" (count @visited-positions))))