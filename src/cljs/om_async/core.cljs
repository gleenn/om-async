(ns ^:figwheel-always om-async.core
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-async.missiles :as missiles]
            [om-async.histogram :as histogram]
            )
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)

(def app-state
  (atom {:classes    []
         :num-points 21
         :points     []}))

(def ^:private meths
  {:get    "GET"
   :put    "PUT"
   :post   "POST"
   :delete "DELETE"})

(defn edn-xhr [{:keys [method url data on-complete]}]
  (let [xhr (XhrIo.)]
    (events/listen xhr goog.net.EventType.COMPLETE
                   (fn [e]
                     (on-complete (reader/read-string (.getResponseText xhr)))))
    (. xhr
       (send url (meths method) (when data (pr-str data))
             #js {"Content-Type" "application/edn"}))))

(defn display [show]
  (if show
    #js {}
    #js {:display "none"}))

(defn end-edit [text owner cb]
  (om/set-state! owner :editing false)
  (cb text))

(defn upsert-class [data class]
  (edn-xhr
    (merge {:data class
            :on-complete
                  (fn [res] (println (str "oncomplete " res)))}
           (if-let [id (get class :id)]
             {:method :put
              :url    (str "/classes/" id)}
             {:method :post
              :url    "/classes"}))
    ))

(defn insert-blank-class [data]
  (om/transact! data :classes (fn [data] (conj data {:title "Blank"}))))

(defn handle-change [e data edit-key owner]
  (om/transact! data edit-key (fn [_] (.. e -target -value))))

(defn editable [data owner {:keys [edit-key on-edit] :as opts}]
  (reify
    om/IInitState
    (init-state [_]
      {:editing false})
    om/IRenderState
    (render-state [_ {:keys [editing]}]
      (let [text (get data edit-key)]
        (dom/li nil
                (dom/span #js {:style (display (not editing))} text)
                (dom/input
                  #js {:style     (display editing)
                       :value     text
                       :onChange  #(handle-change % data edit-key owner)
                       :onKeyDown #(when (= (.-key %) "Enter")
                                    (end-edit text owner on-edit))
                       :onBlur    (fn [e]
                                    (when (om/get-state owner :editing)
                                      (end-edit text owner on-edit)))})
                (dom/button
                  #js {:style   (display (not editing))
                       :onClick #(om/set-state! owner :editing true)}
                  "Edit"))))))

(defn fetch-classes [data owner]
  (edn-xhr
    {:method      :get
     :url         "classes"
     :on-complete #(om/transact! data :classes (fn [_] (do (println (str "replacing with " %)) %)))}))

(defn classes-view [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "classes"}
               (dom/h2 nil "Classes")
               (apply dom/ul nil
                      (map
                        (fn [class]
                          (om/build editable class
                                    {:opts {:edit-key :title
                                            :on-edit  #(upsert-class data class)}}))
                        (:classes data)))
               (dom/button
                 #js {:onClick #(insert-blank-class data)}
                 "New")))))

(om/root
  (fn [data owner]
    (reify
      om/IWillMount
      (will-mount [_]
        (fetch-classes data owner))
      om/IRender
      (render [this]
        (dom/div nil
                 (om/build histogram/histogram-view data)
                 (om/build classes-view data)
                 (om/build missiles/graph-view data))
        )))
  app-state
  {:target (.getElementById js/document "formz")})