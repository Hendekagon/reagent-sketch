(ns parterre.events
  (:require
    [parterre.actions :as actions]
    [oops.core :refer [oget ocall]]
    [clojure.string :as string]))

(defn on [match {convert :convert :or {convert identity}
                 prevent-default? :prevent-default?
                 :as message}]
  (fn [e]
    (let [v (try (oget e [:target :value])
              (catch js/Error error nil))]
      (actions/handle-message!
        {:match match :message (assoc-in message [:target :value] (convert v))}))))