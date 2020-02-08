(ns rs.conversion
  (:require
    [garden.units :as u :refer [px percent pc pt em ms]]
    [rs.css :refer [fr vw vh % item]]))

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
  (merge p (item (str-number s))))

(defn to-number [x]
  (cond
    (:magnitude x) (:magnitude x)
    :otherwise x))