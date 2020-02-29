(ns uitui.conversion
  "
    Functions for convertion
    values from strings to
    useful objects

  "
  (:require
    [garden.units :as u :refer [defunit px percent pc pt em ms]]
    [uitui.css :refer [fr vw vh % ï]]))

(defunit kW)
(defunit m)

(defn str-number [v]
  #?(:cljs (js/parseFloat v)
     :clj (Double/parseDouble v)))

(defmulti convert
  (fn [{unit :unit} s] unit))

(defmethod convert :default [{unit :unit :as p} s]
  (assoc p :magnitude (str-number s)))

(defmethod convert :double [p s]
  (assoc p :magnitude (str-number s)))

(defmethod convert :% [p s]
  (merge p (% (str-number s))))

(defmethod convert (keyword "") [p s]
  (merge p (ï (str-number s))))

(defn to-number [x]
  (cond
    (:magnitude x) (:magnitude x)
    :otherwise x))