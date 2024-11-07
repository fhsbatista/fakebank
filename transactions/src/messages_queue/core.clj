(ns messages-queue.core
  (:require [clojure.java.io :as io])
  (:import [java.util UUID]))

(defn uuid []
  (str (UUID/randomUUID)))

(def default-filepath "messages.txt")

(defn write
  ([message] (write message default-filepath))
  ([message filepath]
   (let [id (uuid)
         csv-line (str message "," id "\n")]
     (with-open [writer (io/writer filepath :append true)]
       (.write writer csv-line)))))

(defn process-message
  ([] (process-message default-filepath))
  ([filepath]
   (let [content (with-open [reader (io/reader filepath)]
                   (doall (line-seq reader)))
         next-on-queue (first content)]
     (println next-on-queue)
     next-on-queue)))
