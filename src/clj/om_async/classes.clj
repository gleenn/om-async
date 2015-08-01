(ns om-async.classes
  (:require
    [clojure.java.jdbc :as sql]
    [om-async.util :as util]
    ;[clojure.java.io :as io]
    ;[clojure.edn :as edn]
    ))

(def url "postgresql://localhost:5432/clojure")

(defn update-class [id params]
  (let [id (Integer/parseInt id)]
    (if-let [class (first (sql/query url ["select * from classes where id = ?" id]))]
      (if (sql/update! url :classes (dissoc (merge class params) :id) ["id = ?" id])
        (util/generate-response {:status :ok})
        (util/generate-response {:status 400}))
      (util/generate-response {:status 404}))))

(defn all []
  (let [classes (sql/query url ["select * from classes"])]
    (println classes)
    (vec classes)))