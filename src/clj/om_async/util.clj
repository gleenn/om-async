(ns om-async.util
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn generate-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body    (pr-str data)})