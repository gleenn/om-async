(ns om-async.core
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.edn :as edn]
            [datomic.api :as d]
            [clojure.java [jdbc :as jdbc]]
            [ring.adapter.jetty :as jetty]
            [compojure.handler :refer [site]]
            [environ.core :refer [env]]
            [om-async.classes :as classes]
            [om-async.util :as util]
            )
  (:gen-class))

;(def uri "datomic:free://localhost:4334/om_async")
;(def conn (d/connect uri))

(def uri "postgresql://localhost:5432/clojure")

(defn index []
  (file-response "public/html/index.html" {:root "resources"}))

;(defn update-class [id params]
;  (let [db (d/db conn)
;        title (:class/title params)
;        eid (ffirst
;              (d/q '[:find ?class
;                     :in $ ?id
;                     :where
;                     [?class :class/id ?id]]
;                   db id))]
;    (d/transact conn [[:db/add eid :class/title title]])
;    (generate-response {:status :ok})))
;
;(defn classes []
;  (let [db (d/db conn)
;        classes
;        (vec (map #(d/touch (d/entity db (first %)))
;                  (d/q '[:find ?class
;                         :where
;                         [?class :class/id]]
;                       db)))]
;    (generate-response classes)))

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
