(ns om-async.core
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [clojure.edn :as edn]
            [ring.adapter.jetty :as jetty]
            [compojure.handler :refer [site]]
            [environ.core :refer [env]]
            [om-async.classes :as classes]
            [om-async.util :as util]
            )
  (:gen-class))

(def uri "postgresql://localhost:5432/clojure")

(defn index []
  (file-response "public/html/index.html" {:root "resources"}))

(defroutes routes
           (GET "/" [] (index))
           (GET "/classes" [] (util/generate-response (classes/all)))
           (POST "/classes"
                 {params :params edn-body :edn-body}
             (try
               (classes/add-class edn-body)
               (catch Exception e (println (str e)))))
           (PUT "/classes/:id"
                {params :params edn-body :edn-body}
             (try
               (classes/update-class (:id params) edn-body)
               (catch Exception e (println (str e)))))
           (route/files "/" {:root "resources/public"}))

(defn read-inputstream-edn [input]
  (edn/read
    {:eof nil}
    (java.io.PushbackReader.
      (java.io.InputStreamReader. input "UTF-8"))))

(defn parse-edn-body [handler]
  (fn [request]
    (handler (if-let [body (:body request)]
               (assoc request
                 :edn-body (read-inputstream-edn body))
               request))))

(def handler
  (-> routes
      parse-edn-body))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'routes) {:port port :join? false})))
