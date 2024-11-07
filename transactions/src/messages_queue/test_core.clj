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
    (let [content (read-file test-file)
          expected-prefix "Test message,"]
      (is (= 1 (count content)))
      (is (.startsWith (first content) expected-prefix)))
    (delete-file test-file)))

(deftest test-write-many-messages
  (let [test-file "test-messages.txt"
        message1 "Test message 1"
        message2 "Test message 2"
        message3 "Test message 3"
        message4 "Test message 4"
        ]
    (with-open [writer (io/writer test-file :append true)]
      (.write writer ""))
    (write message1 test-file)
    (write message2 test-file)
    (write message3 test-file)
    (write message4 test-file)
    (let [content (read-file test-file)]
      (is (= 4 (count content)))
      (is (.startsWith (nth content 0) (str message1 ",")))
      (is (.startsWith (nth content 1) (str message2 ",")))
      (is (.startsWith (nth content 2) (str message3 ",")))
      (is (.startsWith (nth content 3) (str message4 ",")))
      (delete-file test-file))))





