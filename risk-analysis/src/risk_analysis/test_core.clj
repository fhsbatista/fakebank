(ns risk-analysis.test-core
  (:require [clojure.test :refer :all]
            [risk-analysis.core :refer :all]
            [messages-queue.core :as messages]))

(deftest test-suspicious
  (testing "Ensure suspicious? returns always false"
    (is (false? (suspicious? {})))))

(deftest test-legitimate
  (testing "Ensure legitimate? is a complement of suspicious"
    (is (= (legitimate? {}) (not (suspicious? {}))))))

(deftest test-commit-transaction
  (testing "Ensure commit-transaction sends correct id to message queue"
    (let [message-id "12345"
          transaction "transaction"]
      (with-redefs [messages/commit (fn [id] (is (= id message-id)))]
        (commit-transaction {:id message-id :transaction transaction})))))

(deftest test-check-message-asks-for-message-on-queue
  (testing "Ensure check-message calls message queue correctly to get message"
    (let [message-str "transaction1,123e4567-e89b-12d3-a456-426614174000"
          call-count (atom 0)]
      (with-redefs [messages-queue.core/process-message (fn []
                                                          (swap! call-count inc)
                                                          message-str)]
        (check-message)
        (is (= @call-count 1))))))

(deftest test-check-message-commits
  (testing "Ensure check-message calls message queue to commit correct message"
    (let [message-str "transaction1,123e4567-e89b-12d3-a456-426614174000"]
      (with-redefs [messages-queue.core/process-message (fn [] message-str)
                    messages-queue.core/commit (fn [id] (is (= id "123e4567-e89b-12d3-a456-426614174000")))]
        (check-message)))))

