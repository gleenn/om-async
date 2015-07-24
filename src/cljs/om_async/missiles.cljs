(ns ^:figwheel-always om-async.missiles
  (:require [cljs.reader :as reader]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn classes-view [data owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (dom/div nil
               (dom/h1 nil "Missiles")
               (dom/h2 nil (str (:missiles data)))
               (dom/div nil
                        (dom/svg #js {:height 300 :width 500 :className "graph"}
                                 (dom/line #js {:className "line" :x1 "0" :y1 "0" :x2 "100" :y2 "150"})))
               (dom/button #js {:onClick #(om/transact! data :missiles (fn [x] (inc x)))} "Up")
               (dom/button #js {:onClick #(om/transact! data :missiles (fn [x] (dec x)))} "Down")
               ))))

(defn launch [app-state]
  (do
    (println app-state)
    (om/root classes-view app-state
             {:target (.getElementById js/document "missiles")})))