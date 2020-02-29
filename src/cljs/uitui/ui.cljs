(ns ^:figwheel-hooks uitui.ui
  "
    This namespace (re)starts the UI
  "
  (:require
    [reagent.core :as r]
    [uitui.actions :as actions]
    [uitui.views :as views]))

(defn ^:before-load before-start!
  [& q]
  (println "---before restart---"))

(defn ^:after-load start! []
  (if (or true (and @actions/app-state (:reload? @actions/app-state)) (nil? @actions/app-state))
    (reset! actions/app-state (actions/initialize-state))
    (swap! actions/app-state actions/reset-css))
  (r/render [views/root-view] (.getElementById js/document "app")))