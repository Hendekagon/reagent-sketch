(ns uitui.events
  "
    Convert JS events to useful
    objects
  "
  (:require
    [uitui.actions :as actions]
    [clojure.string :as string]
    #?(:cljs [cljs-bean.core :refer [bean ->clj ->js]])
    #?(:cljs [oops.core :refer [oget oget+ ocall]])))

(defn on [match {convert :convert :or {convert identity}
                 prevent-default? :prevent-default?
                 :as message}]
  #?(:cljs
      (fn [e]
        (let [ev (bean (oget+ e [:nativeEvent]) :recursive true)
              tv (try (oget e [:target :value])
                  (catch js/Error error nil))]
         (actions/handle-message!
           {:match (merge (dissoc match :with) (select-keys ev (:with match)))
            :message
            (assoc message
              :value (if tv (convert tv) nil)
              :event ev)})))
      :clj
       (fn [e] {:match match :message message})))