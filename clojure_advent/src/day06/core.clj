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
            {:guard nil :obstacles #{} :rows rows :cols cols}
            (range rows))))

(defn change-direction []
  (let [current @current-direction
        idx (.indexOf move-seq current)
        next-idx (mod (inc idx) (count move-seq))]
    (reset! current-direction (nth move-seq next-idx))))

(defn in-grid? [pos rows cols]
  (let [[x y] pos]
    (and (>= x 0) (< x rows) (>= y 0) (< y cols))))

(defn patrol [obstacles rows cols]
  (loop [visited-with-dir #{}]
    (when-let [current-pos @guard-position]
      (let [[dx dy] (directions @current-direction)
            [x y] current-pos
            new-pos [(+ x dx) (+ y dy)]]
        (cond
          (not (in-grid? new-pos rows cols))
          {:result :out-of-grid :visited @visited-positions}
          
          (contains? obstacles new-pos)
          (do 
            (change-direction)
            (recur (conj visited-with-dir [current-pos @current-direction])))
          
          (contains? visited-with-dir [new-pos @current-direction])
          {:result :loop :visited @visited-positions}
          
          :else
          (do
            (reset! guard-position new-pos)
            (swap! visited-positions conj new-pos)
            (recur (conj visited-with-dir [new-pos @current-direction]))))))))

(defn check-obstacle-causes-loop [grid pos]
  (let [[row col] pos
        modified-grid (update grid row #(str (subs % 0 col) "#" (subs % (inc col))))
        coordinates (find-coordinates modified-grid)]
    (reset! guard-position (:guard coordinates))
    (reset! current-direction (first move-seq))
    (reset! visited-positions #{@guard-position})
    (= :loop (:result (patrol (:obstacles coordinates) (:rows coordinates) (:cols coordinates))))))

(defn position-causes-loop? [coordinates grid pos]
  (and (not (contains? (:obstacles coordinates) pos))
       (check-obstacle-causes-loop grid pos)))

(defn -main []
  (let [input (fr/read-lines "src/day06/input.txt")
        grid (mapv str/trim input)
        coordinates (find-coordinates grid)] 
    (reset! guard-position (:guard coordinates))
    (reset! current-direction (first move-seq))
    (reset! visited-positions #{@guard-position})
    (let [initial-patrol (patrol (:obstacles coordinates) (:rows coordinates) (:cols coordinates))]
      (println "Unique Spaces Counter:" (count (:visited initial-patrol))) 
      (let [initial-positions (:visited initial-patrol)
            loop-causing-positions (filter #(position-causes-loop? coordinates grid %) initial-positions)]
        (println "Positions causing infinite loop:" (count loop-causing-positions))))))

