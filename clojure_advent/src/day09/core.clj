(ns day09.core
  (:require [file-reader :as fr]))

(defn split-string-to-chars [s]
  (vec (seq s)))

(defn repeat-n-times [item n]
  (vec (repeat n item)))

(defn process-memory-size [acc-map memory-size]
  (let [index (:index acc-map)
        id (:id acc-map)
        disk-map (:disk-map acc-map)
        isEven? (even? index)
        repeat-item (if isEven? id \.)
        repeated-items (repeat-n-times repeat-item (Character/getNumericValue memory-size))]
    {:disk-map (conj disk-map repeated-items)
     :id (if isEven? (inc id) id)
     :index (inc index)}))

(defn disk-map [seq]
  (let [initial-state {:disk-map [] :index 0 :id 0}
        final-state (reduce process-memory-size initial-state seq)
        joined-disk-map (vec (apply concat (:disk-map final-state)))]
    joined-disk-map))

(defn find-first-empty-space [disk-map]
  (.indexOf disk-map \.))

(defn defrag-disk [disk-map]
  (let [map-length (count disk-map)
        clean-map (filter #(not= \. %) disk-map)
        clean-map-length (count clean-map)
        empty-spaces (- map-length clean-map-length)
        spaces-to-move  (vec (take empty-spaces (reverse clean-map)))
        
        initial-defrag (reduce (fn [acc space]
                                 (let [dot-index (find-first-empty-space acc)]
                                   (assoc acc dot-index space)))
                               disk-map
                               spaces-to-move) 
        defragged (vec (take clean-map-length initial-defrag))]
    defragged))

(defn checksum [disk-map]
  (reduce + (map-indexed (fn [idx itm] (* idx itm)) disk-map)))
  

(defn -main []
  (let [input (split-string-to-chars(first (fr/read-lines "src/day09/input.txt")))
        disk-map (disk-map input)
        defragged-disk (defrag-disk disk-map)
        checksum (checksum defragged-disk)]
    (println checksum)))