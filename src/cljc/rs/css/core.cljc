(ns rs.css.core
  (:require [rs.css.macros :as cm]))

(defn add-imported-rules [state]
  (assoc-in state [:css :imported] (cm/read-css)))