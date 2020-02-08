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
    [rs.css :as rcss :refer [fr rad deg % rotate3d perspective
                             translate strs sassify-rule named?]]
    [rs.actions :as actions]
    [rs.conversion :refer [convert str-number to-number]]
    [clojure.string :as string]
    #?(:cljs [rs.events :as re])))

(defonce on #?(:cljs re/on
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

(defmethod view :text
  [{v :value title :name path :path}]
  [:textarea.text-input
   {
    :cols 16
    :rows 16
    :wrap :soft
    :title title
    :value v
    :on-change
    (on {:change :text} {:path path})
    }])

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
    (on {:change :thing} {:path path :convert (partial (or cfn convert) p)})
    }])

(defn params-view [{:keys [text number colour] :as params}
                   {:keys [main component animation] :as css}]
  (into [:div.params]
    (concat
     (map
       (fn [[key param]]
         [:div.param
          [:div.param-name (:name param)]
          [:div.param-value
            [view (assoc param :path (conj [:params] key))]]])
       params)
     (map
       (fn [[key style]]
         [:div.param
           [:div.param-name (:name style)]
          [:div.param-value
            [view (assoc style :path (conj [:css :component ".columns"] key))]]])
       [[:column-gap (get-in component [".columns" :column-gap])]
        [:column-count (get-in component [".columns" :column-count])]]))))

(defn preview-view  [{:keys [text number colour] :as params}]
  [:div.preview
   (into
     [:div.columns
       {:contentEditable true :suppressContentEditableWarning true
        :on-input (on {:change :dom-tree} {:path [:params :text]})
        :on-select (on {:debug :text} {:path [:params :text]})}]
       (if (string? (:value text)) [(:value text)] (:value text)))
   [:div.number (:magnitude number)]])

(defn root-view
  "
    Returns a view component for
    the root of the whole UI

    We only pass the data each view needs

  "
  ([] (root-view @actions/app-state))
  ([{params :params
     {:keys [main component animation] :as css} :css
      :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"]
                           :auto-prefix #{:column-width :user-select}} main]
    [css-view :animation-rules {} animation]
    [css-view :component-rules {} component]
    [:div.main
      [:div {:on-click (on {:click :reinitialize} {})} "reset"]
      [params-view params css]
      [preview-view params]
    ]]))
