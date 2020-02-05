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

; define some CSS units missing from garden.units:

(defunit fr)
(defunit rad)
(defunit deg)
(defunit vh)
(defunit vw)
(def pc percent)

(defn minmax [& d] (gt/->CSSFunction "minmax" d))
(defn calc [& d] (gt/->CSSFunction "calc" d))
(defn url [& d] (gt/->CSSFunction "url" d))
(defn rotate [& d] (gt/->CSSFunction "rotate" d))
(defn translate [& d] (gt/->CSSFunction "translate" d))
(defn scale [& d] (gt/->CSSFunction "scale" d))
(defn translate3d [& d] (gt/->CSSFunction "translate3d" d))
(defn scale3d [& d] (gt/->CSSFunction "scale3d" d))
(defn rotate3d [& d] (gt/->CSSFunction "rotate3d" d))
(defn perspective [& d] (gt/->CSSFunction "perspective" d))
(defn linear-gradient [& d] (gt/->CSSFunction "linear-gradient" d))
(defn repeet [& d] (gt/->CSSFunction "repeat" d))

(def css-transition-group
  #?(:cljs :not-implemented-in-cljs-either
     ;(r/adapt-react-class js/React.addons.CSSTransitionGroup)
     :clj  :not-implemented-in-clj))

(defn with-transition [parent properties children]
  [css-transition-group (assoc properties :component (name (first parent)))
   (map-indexed (fn [i child] (with-meta child (or (meta child) {:key i}))) children)])

(def css-str (comp first gcomp/render-css gcomp/expand))

(defn strs [l]
  (string/join "" (map (fn [x] (str "\"" (apply str (interpose " " x)) "\"")) l)))

(defn strz [l]
  (apply str (map (fn [x] (str "\"" (apply str (interpose " " (map css x))) "\"")) l)))

(def areas strs)

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
   (parse-unit s (rest (first (re-seq #"(\d+|\d+.\d+)(\D+)" s)))))
  ([s [x t]]
   (let [v (read-string x)]
    (case t
      "%"  (percent v)
      "px" (px v)
      "em" (em v)
      "fr" (fr v)
      s))))

(defn unit [v]
  (#{"px" "%" "em" "fr"} (last (last (re-seq #"(\d+|\d+.\d+)(\D+)" v)))))

(defn colour-fn-name [v]
  (#{"rgb" "rgba" "hsl" "hsla"} (first (string/split v #"\,|\(|\)"))))

(defn css-list? [v]
  (> (count (string/split v #"[^\,]\s+")) 1))

(defn css-number? [v]
  (re-matches #"\d+|\d+.\d+" v))

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