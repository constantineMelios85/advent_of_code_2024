(ns day12.core
  (:require [file-reader :as fr]))

(defn neighbors [[x y]]
  [[(dec x) y] [(inc x) y] [x (dec y)] [x (inc y)]])

(defn valid-pos? [grid [x y]]
  (and (< -1 x (count grid))
       (< -1 y (count (first grid)))))

(defn find-region [grid start-pos]
  (let [type (get-in grid start-pos)]
    (loop [queue (conj clojure.lang.PersistentQueue/EMPTY start-pos)
           visited #{start-pos}]
      (if (empty? queue)
        visited
        (let [current (peek queue)
              new-neighbors (->> (neighbors current)
                                 (filter #(and (valid-pos? grid %)
                                               (= type (get-in grid %))
                                               (not (visited %)))))]
          (recur (into (pop queue) new-neighbors)
                 (into visited new-neighbors)))))))

(defn find-fields [grid]
  (let [all-positions (for [x (range (count grid))
                            y (range (count (first grid)))]
                        [x y])]
    (->> all-positions
         (reduce (fn [regions pos]
                   (if (some #(% pos) regions)
                     regions
                     (conj regions (find-region grid pos))))
                 [])
         (remove empty?))))

(defn calculate-perimeter [grid region]
  (->> region
       (mapcat neighbors)
       (filter #(or (not (valid-pos? grid %))
                    (not= (get-in grid %) (get-in grid (first region)))))
       count))

(defn analyze-map [input]
  (let [grid (mapv vec input)
        regions (find-fields grid)]
    (map (fn [region]
           (let [type (get-in grid (first region))
                 area (count region)
                 perimeter (calculate-perimeter grid region)
                 fence-price (* perimeter area)]
             {:type type
              :area area
              :perimeter perimeter
              :fence-price fence-price
              :coordinates region}))
         regions)))

(defn -main []
  (let [input (fr/read-lines "src/day12/input.txt")
        analysis (analyze-map input)
        total-fence-price (reduce + (map :fence-price analysis))]
    (println (str "Total Fence Price: " total-fence-price))))

