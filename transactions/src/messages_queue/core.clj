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
