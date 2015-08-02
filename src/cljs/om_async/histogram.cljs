(ns om-async.histogram
  (:require [om.core :as om]
            [om.dom :as dom]
            [clojure.string :as string]
    ;[sablono.core :as html :refer-macros [html]]
            ))

(defn letter-row [letter count]
  (dom/tr nil
          (dom/th nil letter)
          (dom/td nil (str count))
          ))

(defn generate-histogram [classes]
  (let [histogram (into (sorted-map) (map
                                       (fn [[letter letters]] [letter (count letters)])
                                       (group-by identity
                                                 (string/split (apply str (map :title classes)) #"")))
                        )]
    (as-> histogram $
         (into (sorted-map) $)
         (dissoc $ " " "")
         (map (fn [[letter count]] (letter-row letter count)) $))))

(defn histogram-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "right minWidth"}
               (dom/h2 nil "Histogram")
               (dom/table nil
                          (apply dom/tbody nil
                                 (generate-histogram (:classes data))))))))