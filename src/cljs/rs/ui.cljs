(ns ^:figwheel-hooks rs.ui
  "
    This namespace starts the UI
  "
  (:require
    [reagent.core :as r]
    [rs.actions :as actions]
    [rs.css :as css]
    [rs.views :as views]))

(defn ^:before-load before-start!
  [& q]
  (println "---before restart--- "))

(defn ^:after-load start! []
  (if (nil? @actions/app-state)
    (do
      (reset! actions/app-state (actions/make-state)))
    (do
      (swap! actions/app-state (fn [s] (-> s css/add-main-rules)))))
  (r/render [views/boot-view] (.getElementById js/document "app")))