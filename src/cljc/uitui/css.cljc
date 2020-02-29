(ns uitui.css
  "
    CSS things - extra functions and definitions for CSS
    and some CSS rules
  "
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :as gc :refer [css]]
    [garden.compiler :as gcomp]
    [garden.types :as gt]
    [garden.stylesheet :refer [at-media]]
    [clojure.walk :as w]
    [clojure.string :as string]))

; define some CSS units missing from garden.units:

(defunit fr)
(defunit rad)
(defunit deg)
(defunit vh)
(defunit vw)
(defunit ï "")
(def % percent)

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
(defn repeating-linear-gradient [& d] (gt/->CSSFunction "repeating-linear-gradient" d))
(defn repeet  [& d] (gt/->CSSFunction "repeat" d))

(defn strs [l]
  (string/join " "
   (map (fn [x] (str "\"" (string/join " " x) "\"")) l)))

(defn strz [l]
  (apply str
   (map (fn [x] (str "\"" (string/join " " (map css x)) "\"")) l)))

(def areas strs)

(defn named? [x]
  (or (keyword? x) (symbol? x)))

(defn as-str [x]
  (if (named? x)
    (name x)
    x))

(def ç gcomp/render-css)

(defn sassify-rule
  "
    Recurses through the given vector's second element
    which represents a CSS rule, transforming any child rules
    at the :& key into a form that Garden will compile like SASS
  "
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