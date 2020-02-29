(ns uitui.views
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
    [uitui.css :as rcss :refer [ç fr rad deg % rotate3d perspective
                                   rotate translate strs sassify-rule named?]]
    [uitui.actions :as actions]
    [uitui.conversion :refer [convert str-number to-number]]
    [clojure.string :as string]
    [uitui.events :as re :refer [on]]))

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
  (fn [{{:keys [unit]} :value kind :kind}]
    (cond
      unit :range
      :otherwise kind)))

(defmethod view :animal
  [{[x y] :position path :path title :title naym :name species :species :as p}]
    [:div.animal
     {:style {:transform (ç (translate (px x) (px y)))}
      :class (name species)
      :title (or naym title "animal")
      :on-mouse-down (on {:mouse-down :thing} p)
      :on-mouse-up   (on {:mouse-up :thing} p)} (name species)])

(defmethod view :text
  [{v :text path :path title :title :as p}]
  [:textarea.text-input
   {
    :title title
    :value v
    :on-change
      (on {:change :text} {:path path :convert (fn [s] (assoc p :text s))})
    }])

(defmethod view :range
  [{{v :magnitude u :unit min :min max :max step :step :as va} :value
    title :title path :path cfn :convert :as p}]
  [:input.range-input
   {
    :type :range
    :title title
    :min min
    :max max
    :step step
    :value (to-number v)
    :on-change
    (on {:change :range-slider} {:path path :convert (partial (or cfn convert) va)})
    }])

(defmethod view :params [{path :path params :params}]
  (into [:div.params]
    (map
      (fn [[k v]]
        [:div.param
          [:div.param-name-value
            [:div.param-name (name k)]
           (cond (:unit v) [:div.param-value (str (:magnitude v) (name (:unit v)))])]
         (if (:unit v)
           [view {:value v :path (conj path k) :title k}]
           [view (assoc v :path (conj path k) :title k)])])
      params)))

(defmethod view :main [{path :path params :params animals :animals title :title}]
  (into [:div.main
         ;[:div.title (str title)]
    [view {:kind :params :path path :params params}]]
    (map (fn [[k v]] [view (assoc v :path (conj path k) :title k)]) animals)))

(defmethod view :animals [{path :path animals :animals}]
  (into [:div.animals]
    (map (fn [[k v]] [view (assoc v :path (conj path k) :title k)]) animals)))

(defn root-view
  "
    Returns a view component for
    the root of the whole UI

    We only pass down the data
    each subview needs

  "
  ([] (root-view @actions/app-state))
  ([{{:keys [main component animation] :as css} :css
      params :params animals :animals :as state}]
   [:div.root
    {
      :on-mouse-move (on {:mouse-move :root :with [:buttons]} {})
    }
    [css-view :main-rules {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main]
    [css-view :animation-rules {} animation]
    [css-view :component-rules {} component]
    [view {:kind :main :path [:params] :params params
           :animals animals
           :title (select-keys state [:mouse :moving :debug])}]
    [view {:kind :animals :path [:animals] :animals animals}]
  ]))
