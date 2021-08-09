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
                                   rotate translate strs named?]]
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
            (map vec (partition 2 l)) l))
        rules))]))

(defmulti view
  (juxt :kind :api))

(defmethod view [:animal :html]
  [{[x y] :position path :path title :title naym :name species :species :as p}]
    [:div.animal
     {:style {:transform (ç (translate (px x) (px y)))}
      :class species
      :title (or naym title "animal")
      :on-mouse-down (on {:mouse-down :thing} p)
      :on-mouse-up (on {:mouse-up :thing} p)} species])

(defmethod view [:text :html]
  [{v :text path :path title :title :as p}]
  [:textarea.text-input
   {
    :title title
    :value v
    :on-change
      (on {:change :text} {:path path :convert (fn [s] (assoc p :text s))})
    }])

(defmethod view [:range :html]
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

(defmethod view [:params :html]
  [{path :path params :params}]
  (into [:div.params]
    (map
      (fn [[k v]]
        [:div.param
          [:div.param-name-value
            [:div.param-name (name k)]
           (cond (:unit v) [:div.param-value (str (:magnitude v) (name (:unit v)))])]
         (if (:unit v)
           [view {:kind :range :api :html :value v :path (conj path k) :title k}]
           [view (assoc v :api :html :path (conj path k) :title k)])])
      params)))

(defmethod view [:main :html]
  [{path :path params :params animals :animals title :title}]
  [:div.main
    [view {:kind :params :api :html :path path :params params}]])

(defmethod view [:animals :html]
  [{path :path animals :animals}]
  (into [:div.animals]
    (map
      (fn [[k v]] [view (assoc v :api :html :path (conj path k) :title k)])
       animals)))

(defn root-view
  "
    Returns a view component for
    the root of the whole UI

    We only pass down the data
    each subview needs

  "
  ([{{:keys [main component animation] :as css} :css
      params :params animals :animals :as state}]
   [:div.root
    {
      :on-mouse-move (on {:mouse-move :root :with [:buttons]} {})
    }
    [css-view :main-rules {:vendors ["webkit"  "moz"] :auto-prefix #{:column-width :user-select}} main]
    [css-view :animation-rules {} animation]
    [css-view :component-rules {} component]
    [view {:kind :main :api :html :path [:params] :params params
           :title (select-keys state [:mouse :moving :debug])}]
    [view {:kind :animals :api :html :path [:animals] :animals animals}]
  ]))

(defn make-root-view! [an-atom]
  (fn [] (root-view @an-atom)))