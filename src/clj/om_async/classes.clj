(ns om-async.classes
  (:require
    [clojure.java.jdbc :as sql]
    [om-async.util :as util]
    [environ.core :refer [env]]
    ;[clojure.java.io :as io]
    ;[clojure.edn :as edn]
    ))

(def url (or (env :database-url) "postgresql://localhost:5432/glenn"))

(defn add-class [params]
  (if-let [id (first (sql/insert! url :classes params))]
    (util/generate-response {:status :ok :id (:id id)})
    (util/generate-response {:status 400})))

(defn update-class [id params]
  (let [id (Integer/parseInt id)]
    (if-let [class (first (sql/query url ["select * from classes where id = ?" id]))]
      (if-let [id (sql/update! url :classes (dissoc (merge class params) :id) ["id = ?" id])]
        (util/generate-response {:status :ok :id id})
        (util/generate-response {:status 400}))
      (util/generate-response {:status 404}))))

(defn all []
  (let [classes (sql/query url ["select id, title from classes"])]
    (println classes)
    (vec classes)))