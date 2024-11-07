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

(deftest test-write-many-messages
  (let [test-file "test-messages.txt"]
    (with-open [writer (io/writer test-file :append true)]
      (.write writer ""))
    (write "Test message 1" test-file)
    (write "Test message 2" test-file)
    (write "Test message 3" test-file)
    (write "Test message 4" test-file)
    (let [content (read-file test-file)]
      (is (= 4 (count content)))
      (is (= "Test message 1" (first content)))
      (is (= "Test message 2" (second content)))
      (is (= "Test message 3" (nth content 2)))
      (is (= "Test message 4" (nth content 3)))
      (delete-file test-file))))





