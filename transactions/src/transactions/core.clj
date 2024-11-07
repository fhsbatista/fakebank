(ns transactions.core
  (:require [io.pedestal.http.route :as route]
            [java-time.local :as time]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :refer [body-params]]
            [io.pedestal.interceptor :as i]
            [clojure.data.json :as json]))

(def transactions (atom []))

(defn kafka-send [message]
  (println "Mensagem enviada para o kafka: " message))

(defn create-transaction [id value date]
  {:id    id
   :value value
   :date  date})

(defn register-transaction [id value]
  (let [date (time/local-date-time)]
    (let [transaction (create-transaction id value date)]
      (swap! transactions conj transaction)
      (kafka-send transaction))))

(def interceptor
  (i/interceptor
    {:name  :interceptor
     :enter (fn [context]
              (let [params (get-in context [:request :json-params])]
                (let [id (Integer. (get params "id"))
                      value (Double. (get params "value"))]
                  (register-transaction id value)
                  (assoc-in context [:response :status] 200)
                  (assoc-in context [:response :body]
                            (json/write-str {:status  "success"
                                             :id      id
                                             :value   value
                                             :message "Transaction created successfully"})))))}))

(defn post-transaction [context]
  (println context)
  (let [params (get-in context [:json-params])]
    (let [id (Integer. (get params :id))
          value (Double. (get params :value))]
      (register-transaction id value)
      (assoc-in context [:response :status] 200)
      (assoc-in context [:response :body]
                (json/write-str {:status  "success"
                                 :id      id
                                 :value   value
                                 :message "Transaction created successfully"})))))

(def routes
  (route/expand-routes
    #{["/hello" :get (fn [request] {:status 200 :body "Hello world"}) :route-name :hello]
      ["/transactions" :post [(body-params) post-transaction] :route-name :post-transaction]}))

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


(restart-server)