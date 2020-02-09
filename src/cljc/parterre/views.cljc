(ns parterre.views
  "
   This namespace has functions
   that make view components for the UI
  "
  (:require
    [garden.core :as gc :refer [css]]
    [garden.compiler :as gcmp]
    [garden.color :as color :refer [hsl rgb hsla rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px pt em ms percent defunit]]
    [garden.types :as gt]
    [garden.selectors :as gs]
    [parterre.css :as rcss :refer [fr rad deg % rotate3d perspective
                             rotate
                             translate strs sassify-rule named?]]
    [parterre.actions :as actions]
    [parterre.conversion :refer [convert str-number to-number]]
    [clojure.string :as string]
    #?(:cljs [parterre.events :as re])))

(def ç gcmp/render-css)

(defonce on
  #?(:cljs re/on
     :clj (fn [ma me] (fn [e] (actions/handle-message! {:match ma :message me})))))

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

(defmulti view
  (fn [{:keys [unit kind] :as p}]
    (cond
      unit :range
      :otherwise kind)))

(defmethod view :range
  [{v :magnitude u :unit min :min max :max step :step
    title :name path :path cfn :convert :as p}]
  [:input.range-input
   {
    :type :range
    :title title
    :min min
    :max max
    :step step
    :value (to-number v)
    :on-change
    (on {:change :range-slider} {:path path :convert (partial (or cfn convert) p)})
    }])

(defn parterre-view [{:keys [main component animation] :as css}]
  (into
    [:div.soil]
    (cons
      (into [:div.parterre-1]
        (mapcat
          (fn [[key style]]
            (for [x (range 2) y (range 2)]
              [:div.hedge {:style {:transform (ç (rotate (deg (* (+ x y) 0.25 180))))}}
               [view (assoc style :path (conj [:css :component ".parterre-0"] key))]
               ]))
          [
           [:grid-column-gap (get-in component [".parterre-0" :grid-column-gap])]
           ]))
       (map
         (fn [j]
           (into
             [:div.parterre-0
              {:style {:transform (ç (rotate (deg (* (inc j) 0.0 360))))}}]
            (mapcat
              (fn [[key style]]
                (for [x (range 3) y (range 3)]
                  [:div.hedge {:style {:transform (ç (rotate (deg (* (+ x y) 0.0 360))))}}
                   ;[view (assoc style :path (conj [:css :component ".parterre-1"] key))]
                   ]))
              [
               [:grid-row-gap (get-in component [".parterre-1" :grid-row-gap])]
               ]))) (range 8)))))

(defn root-view
  "
    Returns a view component for
    the root of the whole UI

    We only pass down the data
    each subview needs

  "
  ([] (root-view @actions/app-state))
  ([{{:keys [main component animation] :as css} :css
      :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"]
                           :auto-prefix #{:column-width :user-select}} main]
    [css-view :animation-rules {} animation]
    [css-view :component-rules {} component]
    [parterre-view css]
  ]))
