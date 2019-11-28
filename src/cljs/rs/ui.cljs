(ns ^:figwheel-hooks rs.ui
  "
    This namespace starts the UI
  "
  (:require
    [reagent.core :as r]
    [rs.actions :as actions]
    [rs.views :as views]))

(defn ^:before-load before-start!
  [& q]
  (println "---before restart--- "))

(defn ^:after-load start! []
  (when (or true (nil? @actions/app-state))
    (do
      (reset! actions/app-state (actions/initialize-state))))
  (r/render [views/boot-view] (.getElementById js/document "app")))