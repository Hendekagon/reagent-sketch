(ns ^:figwheel-hooks uitui.ui
  "
    This namespace (re)starts the UI
  "
  (:require
    [reagent.core :as r]
    [reagent.dom :as dom]
    [uitui.actions :as actions]
    [uitui.views :as views]))

(defn ^:before-load before-start!
  [& q]
  (println "---before restart---"))

(defn ^:after-load start! []
  (let [root-view (views/make-root-view! (actions/handle-message!))]
   (actions/handle-message! {:app :reloaded})
   (dom/render [root-view] (.getElementById js/document "app"))))