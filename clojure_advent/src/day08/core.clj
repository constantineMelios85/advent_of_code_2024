(ns day08.core
  (:require [file-reader :as fr]))

(defn map-coordinates [grid]
  (reduce (fn [acc [i row]]
            (reduce (fn [row-acc [j char]]
                      (if (not= char \.)
                        (update row-acc char (fnil conj []) [i j])
                        row-acc))
                    acc (map-indexed vector row)))        
          {} (map-indexed vector grid)))

(defn within-map? [x y width height]
  (and (>= x 0) (< x width) (>= y 0) (< y height)))

(defn generate-antinodes [start-coords x-diff y-diff width height]
  (letfn [(generate [x y]
            (when (within-map? x y width height)
              (cons [x y] (lazy-seq (generate (+ x x-diff) (+ y y-diff))))))]
    (generate (first start-coords) (second start-coords))))

(defn find-antinodes-coordinates
  ([first-coords second-coords width height]
   (let [[x1 y1] first-coords
         [x2 y2] second-coords
         x-diff (- x1 x2)
         y-diff (- y1 y2)]
     (concat (generate-antinodes first-coords x-diff y-diff width height )
             (generate-antinodes second-coords (- x-diff) (- y-diff) width height)))))

(defn find-antinodes [coords width height once?]
  (if once?
    (set (mapcat (fn [i]
                   (mapcat (fn [j]
                             (let [[x1 y1] (nth coords i)
                                   [x2 y2] (nth coords j)
                                   x-diff (- x1 x2)
                                   y-diff (- y1 y2)]
                               (filter #(within-map? (first %) (second %) width height)
                                       [[(+ x1 x-diff) (+ y1 y-diff)]
                                        [(+ x2 (* x-diff -1)) (+ y2 (* y-diff -1))]])))
                           (range (inc i) (count coords))))
                 (range (count coords))))
    (set (mapcat (fn [i]
                   (mapcat (fn [j]
                             (find-antinodes-coordinates (nth coords i) (nth coords j) width height))
                           (range (inc i) (count coords))))
                 (range (count coords))))))

(defn count-antinodes 
  ([antenna-map width height once?]
   (count (reduce (fn [acc coords]
                     (into acc (find-antinodes coords width height once?)))
                   #{}
                   (vals antenna-map)))))

(defn -main []
  (let [input (fr/read-lines "src/day08/input.txt")
        width (count (first input))
        height (count input)
        antenna-map (map-coordinates input)]
    (println "Sum of unique antinodes:" (count-antinodes antenna-map width height true))
    (println "Sum of unique resonant antinodes:" (count-antinodes antenna-map width height false))))