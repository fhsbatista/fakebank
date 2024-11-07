(ns transactions.test-pedestal
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn response-hello [request]
  {
   :status 200
   :body   "Hello world"
   })

(def routes
  (route/expand-routes
    #{["/hello" :get (fn [_] {:status 200 :body "Hello alkdjflkasdf"}) :route-name :hello]}))

(defn create-server []
  (http/create-server
    {
     ::http/routes routes
     ::http/type   :jetty
     ::http/port   8890
     })
  )

(defn start []
  (http/start (create-server))
  )
