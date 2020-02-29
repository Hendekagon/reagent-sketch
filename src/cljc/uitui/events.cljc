(ns uitui.events
  "
    Convert JS events to useful
    objects
  "
  (:require
    [uitui.actions :as actions]
    [clojure.string :as string]
    #?(:cljs [oops.core :refer [oget ocall]])))

(defn on [match {convert :convert :or {convert identity}
                 prevent-default? :prevent-default?
                 :as message}]
  #?(:cljs
      (fn [e]
        (let [v (try (oget e [:target :value])
                  (catch js/Error error nil))]
         (actions/handle-message!
           {:match match :message (assoc message :value (convert v))})))
      :clj
       (fn [e] {:match match :message message})))