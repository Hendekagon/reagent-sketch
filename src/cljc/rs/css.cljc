(ns rs.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.types :as gt]
    [garden.stylesheet :refer [at-media]]
    [clojure.string :as string]))


; Garden doesn't have grid-layout's fr unit by default,
; but it provides the defunit macro to create new units
; so let's define it

; define some CSS units missing from garden.units:


(defunit fr)
(defunit rad)
(defunit deg)

; and some CSS functions for animations

(defn rotate [& d]      (gt/->CSSFunction "rotate" d))
(defn translate [& d]   (gt/->CSSFunction "translate" d))
(defn scale [& d]       (gt/->CSSFunction "scale" d))
(defn translate3d [& d] (gt/->CSSFunction "translate3d" d))
(defn scale3d [& d]     (gt/->CSSFunction "scale3d" d))
(defn rotate3d [& d]    (gt/->CSSFunction "rotate3d" d))
(defn perspective [& d] (gt/->CSSFunction "perspective" d))
(defn linear-gradient [& d] (gt/->CSSFunction "linear-gradient" d))

(defn strs
  "Returns a string representation of the given list of lists
  in a form suitable for grid-layout's grid-areas key, which needs
  quoted lists
  (I'm sure Garden's got a built-in way of doing this but I couldn't find it)"
  [lists]
  (apply str
    (map (fn [x] (str "\"" (string/join " " x) "\"")) lists)))

(defn named? [x]
  (or (keyword? x) (symbol? x)))

(defn as-str [x]
  (if (named? x)
    (name x)
    x))

(defn sassify-rule
  "Recurses through the given vector's second element
   which represents a CSS rule, transforming any child rules
   at the :& key into a form that  Garden will compile like SASS"
  [[parent-selector {children :& :as rule}]]
  (if children
    [parent-selector (dissoc rule :&)
      (map (fn [[child-selector child-rule]]
             (sassify-rule [(str (if (keyword? child-selector)
                      (if (namespace child-selector) "&::" "&:")
                      "&") (as-str child-selector))
               child-rule]))
        children)]
     [parent-selector rule]))



; --- rules ---




(defn add-canvas-rules [state]
  (-> state
      (assoc-in [:css :canvas-rules]
             {
              "#canvas"
                                {
                                 :border        [[(px 0) :solid (rgb 86 86 86)]]
                                 :background    (hsla 210 70 85 1)
                                 :border-radius (em 0)
                                 :width         (percent 90)
                                 :height        (px 500)
                                 :display       "table"
                                 }
              "#button-wrapper" {
                                 :display        "table-cell"
                                 :vertical-align "middle"
                                 }
              "#demo-button"    {
                                 :background    (hsl 109 100 70)
                                 :max-width     (px 100)
                                 :height        (px 50)
                                 :overflow      "hidden"
                                 :padding       (px 10)
                                 :display       "flex"
                                 :margin        "auto"
                                 :border-radius (em 1)
                                 :font-size     (pt 16)
                                 :border-width  (px 1)
                                 :box-shadow    [[(px 0) (px 0) (px 10) (hsl 0 0 0)]]
                                 :text-align    "center"
                                 :text-shadow [[(px 1) (px 0.5) (px 0.5) (hsl 0 0 0)]]
                                 }
              })))


(defn add-main-rules [state]
  (assoc-in state [:css :main-rules]
    {
     :.main
                             {
                              :background  (linear-gradient "to bottom" (rgba 216, 215, 215, 0.3) :blue)
                              :text-shadow [[(px 0) (px 0) (px 3) (hsl 0 0 0)] [(px 1) (px 0) (px 1) (hsl 65 49 67)]]
                              :color       (hsl 0 0 100)
                              :width       (percent 100)
                              :height      (percent 100)
                              :display     :flex
                              :flex-flow   "column nowrap"

                              }
     :.demo-grid
                             {
                              :padding       (em 2)
                              :border-radius (px 8)
                              :display       :grid
                              :background    (hsl 137 96 80)

                              }
     ".things"
                             {
                              :display               'grid
                              :grid-area             :content
                              :grid-template-columns [[(percent 20) (percent 80)]]
                              :grid-column-gap       (em 1)
                              :grid-auto-rows        :auto
                              :grid-row-gap          (em 2.5)
                              :background            (rgb 200 205 210)
                              :padding               (percent 4)
                              }
     :body                   {
                              :animation-name            :gradient-flow
                              :animation-duration        (ms 10000)
                              :animation-iteration-count :infinite
                              :animation-timing-function :linear
                              :background                (linear-gradient "to top" :gray :white)
                              :margin                    (percent 1)
                              :font-family               ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
                              :font-size                 (em 1)
                              :font-weight               :normal
                              :cursor                    :default
                              :zoom                      0.8
                              }
     :.button                {
                              :cursor      :pointer
                              :width       (px 10)
                              :height      (px 28)
                              :margin-left (percent 1)
                              }
     ".padding_em-1"         {:padding (em 1)}
     ".button-refresh:hover" {}
     "#text-demo"            {
                              :color (hsl 20 30 90)

                              }
     :div.canvas-parameters  {
                              :max-height (px 200)
                              :padding    (px 10)
                              }
     :div.button-parameters  {
                              :height  (percent 50)
                              :padding (px 10)
                              }

     }))


(defn add-units-rules [state]
  (assoc-in state [:css :units]
    {:.unit    {
                :justify-self    :start
                :display         :flex
                :justify-content :center
                :align-items     :center
                :min-width       (em 2)
                :border-radius   (px 4)
                :border          [[:solid (rgb 200 200 200) (px 1)]]
                :background      (rgb 150 150 150)
                :padding         (em 0.1)
                }
     :.em      {:background (hsl 150 50 50) :color (hsl 150 20 10)}
     :.px      {:background (hsl 10 70 80) :color (hsl 15 20 10)}
     :.percent {:background (hsl 50 70 80) :color (hsl 15 20 10)}
     :.fr      {:background (hsl 200 70 80) :color (hsl 15 20 10)}}))


(defn add-animation-rules [state]
  (assoc-in state [:css :animation]
    [
    (gt/->CSSAtRule :keyframes
      {:identifier :gradient-flow
       :frames     [
                    [:0% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]
                    [:50% {:background (linear-gradient "to top" (rgb 255 255 255) (rgb 150 150 150))}]
                    [:100% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]]})
    ]))


(defn add-grid-rules [state]
  (assoc-in state [:css :demo]
    {
     :.demo-grid
       {
        :padding               (em 1)
        :border-radius         (px 8)
        :display               :grid
        :background            (hsl 197 31 49)
        :grid-template-columns [[(percent 20) (fr 1)]]
        :grid-auto-rows        (em 1.3)
        :grid-row-gap          (em 1)
        :grid-column-gap       (em 1)
        }}))

(defn summarize [v]
  (cond
    (:unit v) (str (name (:unit v)) "-" (:magnitude v))
    (:hue v) (str "hsl-" (string/join "-" ((juxt :hue :saturation :lightness) v)))
    (:red v) (str "rgb-" (string/join "-" ((juxt :red :green :blue) v)))
    (map? v)
      (string/join "-"
        (map str
         (interleave
           (map (fn [k] (if (keyword? k) (name k) k)) (keys v))
           (map (fn [v] (if (vector? v) (string/join "-" (map str v)) (if (keyword? v) (name v) v))) (vals v)))))
    (vector? v) (string/join "-" (map summarize v))
    :otherwise "*"))

(defn make-class-names [rule-maps]
  (sort
    (map (fn [[k v]]
          (keyword
            (str (if (keyword? k) (name k) (str k)) "_" (summarize v))))
      (distinct (mapcat last (mapcat identity (vals rule-maps)))))))
