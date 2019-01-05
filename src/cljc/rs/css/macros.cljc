(ns rs.css.macros
  (:require [garden.types] [garden.color]))

#?(:clj (defmacro read-css []
   (into {}
     (map
       (fn [k] [k (read-string (slurp (str "./resources/style/" (name k) ".css.edn")))])
       [:addons :base :docs :prettify :responsive :styles]))))