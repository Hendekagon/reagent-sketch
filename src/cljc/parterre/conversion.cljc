(ns parterre.conversion
  (:require
    [garden.units :as u :refer [px percent pc pt em ms]]
    [parterre.css :refer [fr vw vh % ï]]))

(defn str-number [v]
  #?(:cljs (js/parseFloat v)
     :clj (Double/parseDouble v)))

(defmulti convert
  (fn [{unit :unit} s] unit))

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