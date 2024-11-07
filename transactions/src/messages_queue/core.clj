(ns messages-queue.core
  (:require [clojure.java.io :as io]))

(def default-filepath "messages.txt")

(defn write
  ([message] (write message default-filepath))
  ([message filepath]
   (with-open [writer (io/writer filepath :append true)]
     (.write writer (str message "\n"))))
  )
