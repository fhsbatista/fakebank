(ns risk-analysis.core
  (:require [messages-queue.core :as messages]))

(defn suspicious? [message]
  ;mocked
  false)

(def legitimate? (complement suspicious?))

(defn commit-transaction [message]
  (messages/commit (get message :id)))

(defn check-message []
  (let [message-str (messages/process-message)]
    (if message-str
      (let [[_ transaction uuid] (re-matches #"(.*),([0-9a-fA-F-]{36})$" message-str)
            message {:id          uuid
                     :transaction transaction}]
        (if (legitimate? message)
          (commit-transaction message))))))


