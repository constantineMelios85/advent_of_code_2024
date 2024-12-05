(ns file-reader
  (:require [clojure.java.io :as io]))

(defn read-lines
  "Reads all lines from the given file and returns them as a sequence."
  [file-path]
  (with-open [reader (io/reader file-path)]
    (doall (line-seq reader)))) ; doall ensures all lines are read before closing the file.
