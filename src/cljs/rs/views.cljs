(ns rs.views
  "
   This namespace has functions
   that make view components for the UI
  "
  (:require
    [oops.core :refer [oget]]
    [garden.core :as gc :refer [css]]
    [garden.color :as color :refer [hsl rgb hsla rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px pt em ms percent defunit]]
    [garden.types :as gt]
    [garden.selectors :as gs]
    [rs.css :as rcss :refer [fr rad deg rotate3d perspective translate strs sassify-rule named?]]
    [rs.actions :as actions]
    [clojure.string :as string]
    [rs.css :as css]))


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

(defn on [{{convert :convert :or {convert identity}
           prevent-default? :prevent-default?}
           :params :as message}]
  (fn [e]
    (let [v (try (oget e [:target :value])
              (catch js/Error error nil))]
      (actions/handle-message!
       (assoc-in message [:params :target :value] (convert v))))))

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
    (on {:params {:path path}})
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
    (on {:params {:path path :convert (fn [v] (js/parseFloat v))}})
    }])



(defn animation-rules []
  [
   (gt/->CSSAtRule :keyframes
                   {:identifier :rotation
                    :frames
                      [
                       [:0% {:transform (translate (percent 0) (percent 0))}]
                       [:25% {:transform (translate (percent 0) (percent -25))}]
                       [:50% {:transform (translate (percent -25) (percent 0))}]
                       [:75% {:transform (translate (percent 0) (percent 25))}]
                       [:100% {:transform (translate (percent 0) (percent 0))}]
                       ]
                    })
   ["#card1"
    {
     :background                (hsla 168 100 80 1)
     :height                    (px 200)
     :width                     (px 200)
     :display                   "table"
     :margin                    "auto"
     :animation-name            :rotation
     :animation-duration        (ms 10000)
     :animation-iteration-count :infinite
     :animation-timing-function :linear
     }]])


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
