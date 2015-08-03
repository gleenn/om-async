(ns om-async.classes
  (:require
    [clojure.java.jdbc :as sql]
    [om-async.util :as util]
    [environ.core :refer [env]]
    ;[clojure.java.io :as io]
    ;[clojure.edn :as edn]
    ))

(def db-url (or (env :database-url) "postgresql://localhost:5432/glenn"))

(defn add-class [params]
  (do
    (println (str "params for add-class " params))
    (if-let [result (first (sql/insert! db-url :classes params))]
      (util/generate-response {:status :ok :id (:id result)})
      (util/generate-response {:status 400}))))

(defn update-class [id params]
  (do
    (println (str "params for update-class " params))
    (let [id (Integer/parseInt id)]
      (if-let [class (first (sql/query db-url ["select * from classes where id = ?" id]))]
        (if-let [id (sql/update! db-url :classes (dissoc (merge class params) :id) ["id = ?" id])]
          (util/generate-response {:status :ok :id id})
          (util/generate-response {:status 400}))
        (util/generate-response {:status 404})))))

(defn all []
  (let [classes (sql/query db-url ["select id, title from classes"])]
    (println classes)
    (vec classes)))