(ns rs.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :as gc :refer [css]]
    [garden.compiler :as gcomp]
    [garden.types :as gt]
    [garden.stylesheet :refer [at-media]]

    #?(:cljs [cljs.tools.reader :refer [read-string]])
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


(def css-str (comp first gcomp/render-css gcomp/expand))

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
                            :padding       (em 0.5)
                            :display       :flex
                            :margin        "auto"
                            :border-radius (px 4)
                            :font-size     (pt 16)
                            :border-width  (px 1)
                            :justify-content :center
                            :align-items :center
                            }
         })))


(defn add-main-rules [state]
  (assoc-in state [:css :main-rules]
    {
     :.main
                             {
                              :background  (rgb 30 30 30)
                              :color       (hsl 0 0 100)
                              :width       (percent 100)
                              :height      (percent 100)
                              :display     :flex
                              :flex-flow   "column nowrap"
                              :font-family ["Gill Sans" "Helvetica" "Sans Serif"]
                              }
     :.imported-rules         {
                                :background  :black
                                :color       :white
                                :width       (percent 100)
                                :height      (percent 100)
                                :display     :flex
                                :flex-flow   "row wrap"
                                :font-size   (em 0.7)
                              }
     :.imported-rule         {:display :flex :flex-flow "row wrap" :margin (px 1)}
     :.selector              {:background (rgb 40 40 40) :display :flex :flex-flow "row wrap" :width (em 16) :margin 0}
     :.rule                  {:display :flex :width (em 32) :background (rgb 50 50 50) :margin 0}
     :.colours               {:display :flex :flex-flow "row wrap"}
     :.colour                {:display :flex :width (px 32) :height (px 32)}
     :.table
       {
        :padding               (em 1)
        :display               :grid
        :color                 :white
        :grid-template-columns [[(percent 50) (fr 1)]]
        :grid-auto-rows        (em 1.3)
        :grid-row-gap          (em 0.5)
        :grid-column-gap       (em 1)
        }
     ".things"
                             {
                              :display               :grid
                              :grid-area             :content
                              :grid-template-columns [[(percent 20) (fr 1)]]
                              :grid-column-gap       (em 1)
                              :grid-auto-rows        :auto
                              :grid-row-gap          (em 2.5)
                              :background            (rgb 200 205 210)
                              :padding               (percent 4)
                              }
     :body                   {

                              :background                (rgb 30 30 30)
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
                :border          :none
                :background      (rgb 150 150 150)
                :padding         (em 0.1)
                }
     :.em      {:background (hsl 150 50 50) :color (hsl 150 20 10)}
     :.px      {:background (hsl 30 30 70) :color (hsl 15 20 10)}
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

(defn parse-colour
  ([s]
   (parse-colour s (string/split s #"\,|\(|\)")))
  ([s [t & a]]
   (if a
     (let [a (map read-string a)]
      (case t
        "rgb" (apply rgb a)
        "rgba" (apply rgba a)
        "hsl" (apply hsl a)
        "hsla" (apply hsla a)
        (keyword "css-variable" s)))
     (keyword "css-variable" s))))

(defn parse-unit
  ([s]
   (parse-unit s (rest (first (re-seq #"(\d+)(\D+)" s)))))
  ([s [x t]]
   (let [v (read-string x)]
    (case t
      "%"  (percent v)
      "px" (px v)
      "em" (em v)
      "fr" (fr v)
      s))))

(defn unit [v]
  (#{"px" "%" "em" "fr"} (last (last (re-seq #"(\d+)(\D+)" v)))))

(defn colour-fn-name [v]
  (#{"rgb" "rgba" "hsl" "hsla"} (first (string/split v #"\,|\(|\)"))))

(defn css-list? [v]
  (> (count (string/split v #"[^\,]\s+")) 1))

(defn css-number? [v]
  (re-matches #"\d+" v))

(defn parse-what [k v]
  (cond
    (css-list? v) :list
    (or (string/ends-with? k "color") (colour-fn-name v)) :colour
    (unit v) :unit
    (css-number? v) :number
    :otherwise :default))

(defmulti parse-value parse-what)

(defmethod parse-value :default [k v]
  v)

(defmethod parse-value :number [k v]
  (read-string v))

(defmethod parse-value :colour [k v]
  (parse-colour v))

(defmethod parse-value :unit [k v]
  (parse-unit v))

(defmethod parse-value :list [k v]
  (mapv (partial parse-value :list) (string/split (string/replace v #",\s+" ",") #"\s+")))