(ns messages-queue.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.util UUID]))

(defn uuid []
  (str (UUID/randomUUID)))

(def default-filepath "/Users/user/development/fakebank/transactions/messages.txt")

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
     next-on-queue)))

(defn commit
  ([message-id] (commit message-id default-filepath))
  ([message-id filepath]
   (let [lines (with-open [reader (io/reader filepath)]
                 (doall (line-seq reader)))
         filtered-lines (filter #(not (str/includes? % message-id)) lines)]
     (with-open [writer (io/writer filepath)]
       (doseq [line filtered-lines]
         (.write writer (str line "\n")))))))
