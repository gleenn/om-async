(ns ^:figwheel-always om-async.missiles
  (:require [cljs.reader :as reader]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


(defn- line [x1 y1 x2 y2]
  (dom/line #js {:className "line" :x1 (str x1) :y1 (str y1) :x2 (str x2) :y2 (str y2)}))

(defn graph-view [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/h1 nil "Missiles")
               (dom/h2 nil (str (:num-points data)))
               (dom/div nil
                        (let [height 300
                              width 1000]
                          (apply dom/svg #js {:height height :width width :className "graph"}
                                 (for [i (range (:num-points data))]
                                   (let [f (fn [x] (* (/ height 3) (.sin js/Math x)))
                                         dx (/ width (:num-points data))
                                         x1 (* i dx)
                                         x2 (+ x1 dx)
                                         y1 (+ (/ height 2) (* -1 (f x1)))
                                         y2 (+ (/ height 2) (* -1 (f x2)))]
                                     (line x1 y1 x2 y2))))))
               (dom/button #js {:onClick #(om/transact! data :num-points (fn [x] (+ x 10)))} "Up")
               (dom/button #js {:onClick #(om/transact! data :num-points (fn [x] (- x 10)))} "Down")
               ))))