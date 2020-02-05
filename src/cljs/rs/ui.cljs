(ns ^:figwheel-hooks rs.ui
  "
    This namespace (re)starts the UI
  "
  (:require
    [reagent.core :as r]
    [rs.actions :as actions]
    [rs.views :as views]))

(defn ^:before-load before-start!
  [& q]
  (println "---before restart---"))

(defn ^:after-load start! []
  (if (or (and @actions/app-state (:reload? @actions/app-state)) (nil? @actions/app-state))
    (reset! actions/app-state (actions/initialize-state))
    (swap! actions/app-state actions/reset-css))
  (r/render [views/root-view] (.getElementById js/document "app")))