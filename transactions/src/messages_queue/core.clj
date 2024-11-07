(ns messages-queue.core
  (:require [clojure.java.io :as io]))

(def filepath "messages.txt")

(defn write [message]
  (with-open [writer (io/writer filepath :append true)]
    (.write writer (str message "\n"))))
