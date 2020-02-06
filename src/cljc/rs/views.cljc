(ns rs.views
  "
   This namespace has functions
   that make view components for the UI
  "
  (:require
    [garden.core :as gc :refer [css]]
    [garden.color :as color :refer [hsl rgb hsla rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px pt em ms percent defunit]]
    [garden.types :as gt]
    [garden.selectors :as gs]
    [rs.css :as rcss :refer [fr rad deg rotate3d perspective translate strs sassify-rule named?]]
    [rs.actions :as actions]
    [clojure.string :as string]
    [rs.css :as rcss]
    [rs.events :refer [on str-number]]))

(defn css-view
 "
   Returns a CSS component
   from the given list of rules
   and optional flags
 "
 ([rules]
   (css-view nil {} rules))
 ([flags rules]
   (css-view nil flags rules))
 ([id flags rules]
   [:style (if id {:type "text/css" :id id} {:type "text/css"})
    (css flags
      (mapcat
        (fn [[f & _ :as l]]
          (if (or (string? f) (named? f))
            (map sassify-rule (partition 2 l)) l))
    (if (map? rules) rules (partition-by :identifier rules))))]))

(defn input-text-view
  "
    Returns a textarea component
    that changes the :text key of the state
  "
  [{v :value title :title path :path id :id}]
  [:input.text-input
   {
    :type :textarea
    :id id
    :title title
    :value v
    :on-change
    (on {:change :thing} {:path path})
    }])

(defn input-number-view
  "Returns an input view that converts to/from a number"
  [{v :value min :min max :max step :step title :title path :path}]
  [:input.range-input
   {
    :type :range
    :title title
    :min min
    :max max
    :step step
    :value v
    :on-change
    (on {:change :thing} {:path path :convert str-number})
    }])

(defn root-view
  "
    Returns a view component for
    the root of the whole UI

    We only pass the data each view needs

    Each component has its own CSS where possible
  "
  ([] (root-view @actions/app-state))
  ([{{:keys [text number colour]} :params
     {:keys [main component animation]} :css
     :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main]
    [css-view :animation-rules {} animation]
    [css-view :component-rules {} component]
    [:div.main
      [:div.params
        [input-text-view {:path [:params :text] :value text}]
        [input-number-view (assoc number :path [:params :number :value] :title "Number")]]
      [:div.preview
        [:div text]
        [:div (:value number)]]
    ]]))
