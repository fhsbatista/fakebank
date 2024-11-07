(ns messages_queue.test_core
  (:require [clojure.test :refer :all]
            [messages-queue.core :refer :all]
            [clojure.java.io :as io]))

(defn read-file [filepath]
  (with-open [reader (io/reader filepath)]
    (doall (line-seq reader))))

(defn delete-file [filepath]
  (try
    (io/delete-file filepath)
    (catch Exception _ nil)))

(deftest test-write
  (let [test-file "test-messages.txt"]
    (with-open [writer (io/writer test-file :append true)]
      (.write writer ""))
    (write "Test message" test-file)
    (let [content (read-file test-file)]
      (is (= 1 (count content)))
      (is (= "Test message" (first content))))
    (delete-file test-file)))



