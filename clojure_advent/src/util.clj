(ns util
  (:import [java.text NumberFormat]))

(defn format-number [num]
  (let [formatter (NumberFormat/getInstance)]
    (.format formatter num)))

(defn average-runtime [f n]
  (let [times (repeatedly n #(let [start (System/nanoTime)]
                               (f)
                               (- (System/nanoTime) start)))]
    (format-number (/ (reduce + times) (double n)))))

(defn sum-of-list [lst]
  (reduce + lst))