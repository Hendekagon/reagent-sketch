(ns rs.events
  (:require
    [rs.actions :as actions]
    #?(:cljs [oops.core :refer [oget]])))

(defn str-number [v]
  #?(:cljs (js/parseFloat v)
     :clj (Double/parseDouble v)))

(defn on [match {convert :convert :or {convert identity}
                 prevent-default? :prevent-default?
                 :as message}]
  #?(:cljs
      (fn [e]
         (let [v (try (oget e [:target :value])
                   (catch js/Error error nil))]
           (actions/handle-message!
             {:match match :message (assoc-in message [:target :value] (convert v))})))
       :clj
        (fn [e]
         (let [v (get-in e [:target :value])]
           (actions/handle-message!
             {:match match :message (assoc-in message [:target :value] (convert v))})))))