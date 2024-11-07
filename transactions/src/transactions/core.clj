(ns transactions.core
  (:require [io.pedestal.http.route :as route]
            [java-time.local :as time]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :refer [body-params]]
            [messages-queue.core :as messages]))

(def transactions (atom []))

(defn queue-message [message]
  (messages/write message)
  (println "Queued message: " message))

(defn create-transaction [id value date]
  {:id    id
   :value value
   :date  date})

(defn register-transaction [id value]
  (let [date (time/local-date-time)]
    (let [transaction (create-transaction id value date)]
      (swap! transactions conj transaction)
      (queue-message transaction))))

(defn post-transaction [context]
  (println context)
  (let [params (get-in context [:json-params])]
    (let [id (Integer. (get params :id))
          value (Double. (get params :value))]
      (register-transaction id value)
      {:status 200 :body {:message     "Transaction created"
                          :transaction {:id    id
                                        :value value}}})))

(def routes
  (route/expand-routes
    #{["/transactions" :post [(body-params) post-transaction] :route-name :post-transaction]}))

(def service
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8080
   })

(defonce server (atom nil))

(defn start-server []
  (reset! server
          (http/start (http/create-server
                        (assoc service ::http/join? false)))))

(defn stop-server []
  (http/stop @server))

(defn restart-server []
  (stop-server)
  (start-server))

(defn start []
  (start-server)
  (restart-server))